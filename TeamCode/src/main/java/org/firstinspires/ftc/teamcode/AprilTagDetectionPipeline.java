package org.firstinspires.ftc.teamcode;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.apriltag.AprilTagDetectorJNI;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

class AprilTagDetectionPipeline extends OpenCvPipeline {
    private long nativeApriltagPtr;
    private Mat grey = new Mat();
    private ArrayList<AprilTagDetection> detections = new ArrayList<>();

    private ArrayList<AprilTagDetection> detectionsUpdate = new ArrayList<>();
    private final Object detectionsUpdateSync = new Object();

    Mat cameraMatrix;

    Scalar blue = new Scalar(7, 197, 235, 255);
    Scalar red = new Scalar(255, 0, 0, 255);
    Scalar green = new Scalar(0, 255, 0, 255);
    Scalar white = new Scalar(255, 255, 255 ,255);

    double fx;
    double fy;
    double cx;
    double cy;

    double tagsize;
    double tagsizeX;
    double tagsizeY;

    private float decimation;
    private boolean needToSetDecimation;
    private final Object decimationSync = new Object();

    public AprilTagDetectionPipeline(double tagsize, double fx, double fy, double cx, double cy)  {
        this.tagsize = tagsize;
        this.tagsizeX = tagsize;
        this.tagsizeY = tagsize;
        this.fx = fx;
        this.fy = fy;
        this.cx = cx;
        this.cy = cy;

        constructMatrix();

        // Allocate a native context object. See the corresponding deletion in the finalizer
        nativeApriltagPtr = AprilTagDetectorJNI.createApriltagDetector(AprilTagDetectorJNI.TagFamily.TAG_36h11.string, 3, 3);
    }

    @Override
    public void finalize() {
        if (nativeApriltagPtr != 0) {
            // Delete the native context we created in the constructor
            AprilTagDetectorJNI.releaseApriltagDetector(nativeApriltagPtr);
            nativeApriltagPtr = 0;
        } else {
            System.out.println("AprilTagDetectionPipeline.finalize(): nativeApriltagPtr was NULL");
        }
    }

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, grey, Imgproc.COLOR_RGBA2GRAY);

        synchronized (decimationSync) {
            if (needToSetDecimation) {
                AprilTagDetectorJNI.setApriltagDetectorDecimation(nativeApriltagPtr, decimation);
                needToSetDecimation = false;
            }
        }

        detections = AprilTagDetectorJNI.runAprilTagDetectorSimple(nativeApriltagPtr, grey, tagsize, fx, fy, cx, cy);

        synchronized (detectionsUpdateSync) {
            detectionsUpdate = detections;
        }

        for (AprilTagDetection detection : detections) {
            Pose pose = poseFromTrapezoid(detection.corners, cameraMatrix, tagsizeX, tagsizeY);
            drawAxisMarker(input, tagsizeY / 2.0, 6, pose.rvec, pose.tvec, cameraMatrix);
            draw3dCubeMarker(input, tagsizeX, tagsizeX, tagsizeY, 5, pose.rvec, pose.tvec, cameraMatrix);
        }

        return input;
    }

    public void setDecimation(float decimation)  {
        synchronized (decimationSync) {
            this.decimation = decimation;
            needToSetDecimation = true;
        }
    }

    public ArrayList<AprilTagDetection> getLatestDetections() {
        return detections;
    }

    public ArrayList<AprilTagDetection> getDetectionsUpdate() {
        synchronized (detectionsUpdateSync) {
            ArrayList<AprilTagDetection> ret = detectionsUpdate;
            detectionsUpdate = null;
            return ret;
        }
    }

    void constructMatrix() {
        cameraMatrix = new Mat(3,3, CvType.CV_32FC1);

        cameraMatrix.put(0,0, fx);
        cameraMatrix.put(0,1,0);
        cameraMatrix.put(0,2, cx);

        cameraMatrix.put(1,0,0);
        cameraMatrix.put(1,1,fy);
        cameraMatrix.put(1,2,cy);

        cameraMatrix.put(2, 0, 0);
        cameraMatrix.put(2,1,0);
        cameraMatrix.put(2,2,1);
    }

    /**
     * Draw a 3D axis marker on a detection. (Similar to what Vuforia does)
     *
     * @param buf the RGB buffer on which to draw the marker
     * @param length the length of each of the marker 'poles'
     * @param rvec the rotation vector of the detection
     * @param tvec the translation vector of the detection
     * @param cameraMatrix the camera matrix used when finding the detection
     */
    void drawAxisMarker(Mat buf, double length, int thickness, Mat rvec, Mat tvec, Mat cameraMatrix) {
        MatOfPoint3f axis = new MatOfPoint3f(
                new Point3(0,0,0),
                new Point3(length,0,0),
                new Point3(0,length,0),
                new Point3(0,0,-length)
        );

        MatOfPoint2f matProjectedPoints = new MatOfPoint2f();
        Calib3d.projectPoints(axis, rvec, tvec, cameraMatrix, new MatOfDouble(), matProjectedPoints);
        Point[] projectedPoints = matProjectedPoints.toArray();

        Imgproc.line(buf, projectedPoints[0], projectedPoints[1], red, thickness);
        Imgproc.line(buf, projectedPoints[0], projectedPoints[2], green, thickness);
        Imgproc.line(buf, projectedPoints[0], projectedPoints[3], blue, thickness);

        Imgproc.circle(buf, projectedPoints[0], thickness, white, -1);
    }

    void draw3dCubeMarker(Mat buf, double length, double tagWidth, double tagHeight, int thickness, Mat rvec, Mat tvec, Mat cameraMatrix) {
        MatOfPoint3f axis = new MatOfPoint3f(
                new Point3(-tagWidth / 2, tagHeight / 2,0),
                new Point3( tagWidth / 2, tagHeight / 2,0),
                new Point3( tagWidth / 2,-tagHeight / 2,0),
                new Point3(-tagWidth / 2,-tagHeight / 2,0),
                new Point3(-tagWidth / 2, tagHeight / 2,-length),
                new Point3( tagWidth / 2, tagHeight / 2,-length),
                new Point3( tagWidth / 2,-tagHeight / 2,-length),
                new Point3(-tagWidth / 2,-tagHeight / 2,-length));

        MatOfPoint2f matProjectedPoints = new MatOfPoint2f();
        Calib3d.projectPoints(axis, rvec, tvec, cameraMatrix, new MatOfDouble(), matProjectedPoints);
        Point[] projectedPoints = matProjectedPoints.toArray();

        for(int i = 0; i < 4; i++)
        {
            Imgproc.line(buf, projectedPoints[i], projectedPoints[i+4], blue, thickness);
        }

        Imgproc.line(buf, projectedPoints[4], projectedPoints[5], green, thickness);
        Imgproc.line(buf, projectedPoints[5], projectedPoints[6], green, thickness);
        Imgproc.line(buf, projectedPoints[6], projectedPoints[7], green, thickness);
        Imgproc.line(buf, projectedPoints[4], projectedPoints[7], green, thickness);
    }

    /**
     * Extracts 6DOF pose from a trapezoid, using a camera intrinsics matrix and the
     * original size of the tag.
     *
     * @param points the points which form the trapezoid
     * @param cameraMatrix the camera intrinsics matrix
     * @param tagsizeX the original width of the tag
     * @param tagsizeY the original height of the tag
     * @return the 6DOF pose of the camera relative to the tag
     */
    Pose poseFromTrapezoid(Point[] points, Mat cameraMatrix, double tagsizeX , double tagsizeY) {
        MatOfPoint2f points2d = new MatOfPoint2f(points);

        Point3[] arrayPoints3d = new Point3[4];
        arrayPoints3d[0] = new Point3(-tagsizeX / 2, tagsizeY / 2, 0);
        arrayPoints3d[1] = new Point3(tagsizeX / 2, tagsizeY / 2, 0);
        arrayPoints3d[2] = new Point3(tagsizeX / 2, -tagsizeY / 2, 0);
        arrayPoints3d[3] = new Point3(-tagsizeX / 2, -tagsizeY / 2, 0);
        MatOfPoint3f points3d = new MatOfPoint3f(arrayPoints3d);

        Pose pose = new Pose();
        Calib3d.solvePnP(points3d, points2d, cameraMatrix, new MatOfDouble(), pose.rvec, pose.tvec, false);

        return pose;
    }

    class Pose {
        Mat rvec;
        Mat tvec;

        public Pose() {
            rvec = new Mat();
            tvec = new Mat();
        }

        public Pose(Mat rvec, Mat tvec) {
            this.rvec = rvec;
            this.tvec = tvec;
        }
    }
}

// https://github.com/OpenFTC/EOCV-AprilTag-Plugin/blob/master/examples/src/main/java/org/firstinspires/ftc/teamcode/AprilTagDetectionPipeline.java