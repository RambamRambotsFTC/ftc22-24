package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Autonomous(name="Auto L", group="Linear Opmode")
public class AutoOpModeL extends LinearOpMode {
    private MecanumWheels drive;
    private Arm arm;

    private DcMotor leftBackMotor;
    private DcMotor leftFrontMotor;
    private DcMotor rightBackMotor;
    private DcMotor rightFrontMotor;

    private Servo armServo;
    private DcMotor armMotor1;
    private DcMotor armMotor2;

    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftBackMotor = hardwareMap.get(DcMotor.class, "leftBackMotor");
        leftFrontMotor = hardwareMap.get(DcMotor.class, "leftFrontMotor");
        rightBackMotor = hardwareMap.get(DcMotor.class, "rightBackMotor");
        rightFrontMotor = hardwareMap.get(DcMotor.class, "rightFrontMotor");

        armServo = hardwareMap.get(Servo.class, "armServo");
        armMotor1 = hardwareMap.get(DcMotor.class, "armMotor1");
        armMotor2 = hardwareMap.get(DcMotor.class, "armMotor2");

        drive = new MecanumWheels(leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor);
        arm = new Arm(armServo, armMotor1, armMotor2);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(0.166, 580, 580, 400, 220);
        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(1280,720, OpenCvCameraRotation.UPRIGHT);
            }
            @Override
            public void onError(int errorCode) { }
        });

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();
            if (currentDetections.size() != 0) {
                for (AprilTagDetection tag : currentDetections) {
                    camera.stopStreaming();
                    if (tag.id == 0 && opModeIsActive()) {
                        runtime.reset();
                        while (runtime.milliseconds() < 1000 ) {};
                        drive.forward(1.10);
                        while (runtime.milliseconds() < 1000 ) {};
                        arm.up(.85);
                        while (runtime.milliseconds() < 1000 ) {};
                        drive.left(.7);
                        while (runtime.milliseconds() < 1000 ) {};
                        drive.left(.4);
                        arm.neutral();
                        requestOpModeStop();
                    } else if (tag.id == 19 && opModeIsActive()) {
                        drive.forward(1.10);
                        runtime.reset();
                        while (runtime.milliseconds() < 1000 ) {};
                        drive.forward(.2);
                        requestOpModeStop();
                    } else if (tag.id == 242 && opModeIsActive()) {
                        drive.forward(1.10);
                        runtime.reset();
                        while (runtime.milliseconds() < 3000 ) {};
                        drive.right(1.2);
                        requestOpModeStop();
                    }

                }
            }

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}