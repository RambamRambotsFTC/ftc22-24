// https://youtu.be/0_w7UTN9LnE

package org.firstinspires.ftc.teamcode;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.*;

public class PropDetectionPipeline extends OpenCvPipeline {
    boolean checkRed;
    private int biggestBlob = -1;

    public PropDetectionPipeline(boolean checkRed) { this.checkRed = checkRed; }

    public int getBiggestBlob() { return biggestBlob; }

    @Override
    public Mat processFrame(Mat input) {
        Rect left = new Rect(1, 1, (1280 / 3) - 1, 719);
        Rect middle = new Rect(1280 / 3, 1, (1280 / 3) - 1, 719);
        Rect right = new Rect((1280 / 3) * 2, 1, (1280 / 3) - 1, 719);
        Imgproc.rectangle(input, left, new Scalar(0, 255, 0), 2);
        Imgproc.rectangle(input, middle, new Scalar(0, 255, 0), 2);
        Imgproc.rectangle(input, right, new Scalar(0, 255, 0), 2);

        Mat mask = preprocessFrame(input);

        Mat[] segregatedMats = { mask.submat(left), mask.submat(middle), mask.submat(right) };
        Mat[] segregatedMatsColor = { input.submat(left), input.submat(middle), input.submat(right) };
        double[] areas = new double[3];

        for (int i = 0; i < segregatedMats.length; i++) {
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(segregatedMats[i], contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            hierarchy.release();

            MatOfPoint largestContour = findLargestContour(contours);
            if (largestContour != null) {
                Imgproc.drawContours(segregatedMatsColor[i], contours, -1, checkRed ? new Scalar(0, 0, 155) : new Scalar(155, 0, 0), 5);
                Imgproc.drawContours(segregatedMatsColor[i], contours, contours.indexOf(largestContour), checkRed ? new Scalar(0, 0, 255) : new Scalar(255, 0, 0), 5);
                areas[i] = Imgproc.boundingRect(largestContour).area();
            } else areas[i] = 0;
            segregatedMats[i].release();
        }
        Core.hconcat(Arrays.asList(segregatedMatsColor), input);
        for (Mat mat : segregatedMatsColor) mat.release();
        mask.release();

        double greatest = Math.max(areas[0], (Math.max(areas[1], areas[2])));
        if (greatest == 0) biggestBlob = -1;
        else if (greatest == areas[0]) biggestBlob = 0;
        else if (greatest == areas[1]) biggestBlob = 1;
        else if (greatest == areas[2]) biggestBlob = 2;

        return input;
    }

    private Mat preprocessFrame(Mat frame) {
        Mat hsvFrame = new Mat();
        Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_RGB2HSV);

        Mat mask = new Mat();
        if (checkRed) {
            Mat lowerRed = new Mat(), upperRed = new Mat();
            Core.inRange(hsvFrame, new Scalar(0, 50, 50), new Scalar(10, 255, 255), lowerRed);
            Core.inRange(hsvFrame, new Scalar(170, 50, 50), new Scalar(180, 255, 255), upperRed);
            Core.bitwise_or(lowerRed, upperRed, mask);
            lowerRed.release();
            upperRed.release();
        } else Core.inRange(hsvFrame, new Scalar(100, 150, 0), new Scalar(140, 255, 255), mask);
        hsvFrame.release();

        return mask;
    }

    private MatOfPoint findLargestContour(List<MatOfPoint> contours) {
        double maxArea = 0;
        MatOfPoint largestContour = null;

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }

        return largestContour;
    }
}