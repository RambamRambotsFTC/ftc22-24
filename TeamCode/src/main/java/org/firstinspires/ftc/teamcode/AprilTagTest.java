package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@TeleOp(name="AprilTag Test", group="Linear Opmode")
public class AprilTagTest extends LinearOpMode {
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    double tagSize = 0.166;

    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagSize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() { camera.startStreaming(1280,720, OpenCvCameraRotation.UPRIGHT); }

            @Override
            public void onError(int errorCode) { }
        });

        waitForStart();

        while (opModeIsActive()) {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

            if (currentDetections.size() != 0) {
                for (AprilTagDetection tag : currentDetections) {
                    if (tag.id == 1) telemetry.addData("Tag 1 detected", "Blue Alliance Left");
                    else if (tag.id == 2) telemetry.addData("Tag 2 detected", "Blue Alliance Center");
                    else if (tag.id == 3) telemetry.addData("Tag 3 detected", "Blue Alliance Right");
                    else if (tag.id == 4) telemetry.addData("Tag 4 detected", "Red Alliance Left");
                    else if (tag.id == 5) telemetry.addData("Tag 5 detected", "Red Alliance Center");
                    else if (tag.id == 6) telemetry.addData("Tag 6 detected", "Red Alliance Right");
                    telemetry.update();
                }
            }
        }
    }
}