package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

import java.util.ArrayList;

@Autonomous(name="Autonomus", group="Linear Opmode")
public class AutoOpMode extends LinearOpMode {
    private MecanumWheels drive;
    private Arm arm;

    private DcMotor leftBackMotor;
    private DcMotor leftFrontMotor;
    private DcMotor rightBackMotor;
    private DcMotor rightFrontMotor;

    private Servo armServo;
    private CRServo armMotor1;
    private CRServo armMotor2;

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
        armMotor1 = hardwareMap.get(CRServo.class, "armMotor1");
        armMotor2 = hardwareMap.get(CRServo.class, "armMotor2");

        drive = new MecanumWheels(leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor);
        arm = new Arm(armServo, armMotor1, armMotor2);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
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
                    if (tag.id == 0) {
                        runtime.reset();
                        drive.drive(-1, 0, -1, 0);
                        while (runtime.milliseconds() < 1000) { }
                        drive.drive(0, -1, 0, -1);
                        while (runtime.milliseconds() < 2000) { }
                        drive.drive(0, 0, 0, 0);
                    } else if (tag.id == 19) {
                        runtime.reset();
                        drive.drive(0, -1, 0, -1);
                        while (runtime.milliseconds() < 750) { }
                        drive.drive(0, 0, 0, 0);
                    } else if (tag.id == 242) {
                        runtime.reset();
                        drive.drive(1, 0, 1, 0);
                        while (runtime.milliseconds() < 1000) { }
                        drive.drive(0, -1, 0, -1);
                        while (runtime.milliseconds() < 2000) { }
                        drive.drive(0, 0, 0, 0);
                    }
                    stop();
                }
            }

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}
