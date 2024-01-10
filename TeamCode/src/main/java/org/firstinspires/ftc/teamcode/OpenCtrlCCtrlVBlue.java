// https://gist.github.com/oakrc/12a7b5223df0cb55d7c1288ce96a6ab7

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@TeleOp(name="OpenCtrlCCtrlV (Blue)", group="Object Detection")
public class OpenCtrlCCtrlVBlue extends LinearOpMode {
    OpenCvCamera camera;
    PropDetectionPipeline propDetectionPipeline;

    ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        propDetectionPipeline = new PropDetectionPipeline(false);

        camera.setPipeline(propDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() { camera.startStreaming(1280, 720, OpenCvCameraRotation.SENSOR_NATIVE); }
            @Override
            public void onError(int errorCode) {}
        });

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Position of biggest blue blob", new String[]{ "None", "Left", "Middle", "Right" }[propDetectionPipeline.getBiggestBlob() + 1]);
            telemetry.update();
        }
    }
}