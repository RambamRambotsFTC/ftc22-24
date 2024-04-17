package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.*;

@Autonomous(name="Autonomous", group="Autonomous")
public class AutoOpMode extends LinearOpMode {
    MecanumWheels drive;
    Arm arm;
    Drone drone;
    Intake intake;

    DcMotor leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor, armMotor, intakeMotor1, intakeMotor2;
    Servo pocketServo1, pocketServo2, droneServo, dataServo;

    OpenCvCamera camera;
    PropDetectionPipeline propDetectionPipeline;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftBackMotor = hardwareMap.get(DcMotor.class, "leftBackMotor");
        leftFrontMotor = hardwareMap.get(DcMotor.class, "leftFrontMotor");
        rightBackMotor = hardwareMap.get(DcMotor.class, "rightBackMotor");
        rightFrontMotor = hardwareMap.get(DcMotor.class, "rightFrontMotor");
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        intakeMotor1 = hardwareMap.get(DcMotor.class, "intakeMotor1");
        intakeMotor2 = hardwareMap.get(DcMotor.class, "intakeMotor2");

        pocketServo1 = hardwareMap.get(Servo.class, "pocketServo1");
        pocketServo2 = hardwareMap.get(Servo.class, "pocketServo2");
        droneServo = hardwareMap.get(Servo.class, "droneServo");
        dataServo = hardwareMap.get(Servo.class, "dataServo");

        drive = new MecanumWheels(leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor);
        arm = new Arm(armMotor, pocketServo1, pocketServo2);
        intake = new Intake(intakeMotor1, intakeMotor2);
        drone = new Drone(droneServo);

        DataServo data = new DataServo(dataServo);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        propDetectionPipeline = new PropDetectionPipeline(data.getAlliance() == DataServo.RED_ALLIANCE);

        camera.setPipeline(propDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() { camera.startStreaming(1280, 720, OpenCvCameraRotation.SENSOR_NATIVE); }
            @Override
            public void onError(int errorCode) {}
        });

        waitForStart();

        if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.NONE) { requestOpModeStop(); return; }
        camera.closeCameraDevice();
        arm.retract();
        switch (propDetectionPipeline.getBiggestBlob()) {
            case PropDetectionPipeline.LEFT:
                if ((data.getStage() == DataServo.FRONT_STAGE && data.getAlliance() == DataServo.BLUE_ALLIANCE) || (data.getStage() == DataServo.BACK_STAGE && data.getAlliance() == DataServo.RED_ALLIANCE)) {
                    drive.backwards(1, 3500);
                    sleep(100);
                    drive.turnLeft(1, 2150);
                    sleep(100);
                    drive.backwards(1, 1000);
                    sleep(100);
                    drive.forwards(1, data.getStage() == DataServo.FRONT_STAGE ? 1000 : 3000);
                    sleep(100);
                    if (data.getStage() == DataServo.FRONT_STAGE) {
                        drive.left(1, 3000);
                        sleep(100);
                        drive.turnLeft(1, 4300);
                    } else drive.left(1, 250);
                } else {
                    drive.right(1, 1500);
                    sleep(100);
                    drive.backwards(1, 3000);
                    sleep(100);
                    drive.forwards(1, 1500);
                    sleep(100);
                    drive.turnRight(1, 2050);
                    sleep(100);
                    drive.forwards(1, data.getStage() == DataServo.FRONT_STAGE ? 1250 : 750);
                    sleep(100);
                    drive.right(1, data.getStage() == DataServo.FRONT_STAGE ? 5000 : 1000);
                    if (data.getStage() == DataServo.FRONT_STAGE) {
                        sleep(100);
                        drive.turnLeft(1, 4200);
                        sleep(100);
                        drive.forwards(1, 2500);
                    }
                }
                break;
            case PropDetectionPipeline.MIDDLE:
                drive.backwards(1, 4200);
                sleep(100);
                drive.forwards(1, 2000);
                sleep(100);
                if ((data.getStage() == DataServo.FRONT_STAGE && data.getAlliance() == DataServo.BLUE_ALLIANCE) || (data.getStage() == DataServo.BACK_STAGE && data.getAlliance() == DataServo.RED_ALLIANCE)) {
                    drive.turnLeft(1, 2050);
                    sleep(100);
                    drive.forwards(1, 1650);
                    sleep(100);
                    drive.left(1, data.getStage() == DataServo.FRONT_STAGE ? 5500 : 1000);
                } else {
                    drive.turnRight(1, 2050);
                    sleep(100);
                    drive.forwards(1, 1650);
                    sleep(100);
                    drive.right(1, data.getStage() == DataServo.FRONT_STAGE ? 5500 : 1000);
                }
                if (data.getStage() == DataServo.FRONT_STAGE) {
                    sleep(100);
                    drive.backwards(1, 1500);
                    sleep(100);
                    if (data.getAlliance() == DataServo.RED_ALLIANCE) drive.turnLeft(1, 4100);
                    else drive.turnRight(1, 4100);
                    sleep(100);
                    if (data.getAlliance() == DataServo.RED_ALLIANCE) drive.right(1, 1500);
                    else drive.left(1, 1500);
                }
                break;
            case PropDetectionPipeline.RIGHT:
                if ((data.getStage() == DataServo.FRONT_STAGE && data.getAlliance() == DataServo.BLUE_ALLIANCE) || (data.getStage() == DataServo.BACK_STAGE && data.getAlliance() == DataServo.RED_ALLIANCE)) {
                    drive.left(1, 1500);
                    sleep(100);
                    drive.backwards(1, 3000);
                    sleep(100);
                    drive.forwards(1, 1000);
                    sleep(100);
                    if (data.getStage() == DataServo.FRONT_STAGE) drive.turnRight(1, 2000);
                    else drive.turnLeft(1, 2000);
                    sleep(100);
                    drive.forwards(1, 1000);
                    sleep(100);
                    if (data.getStage() == DataServo.FRONT_STAGE) drive.right(1, 4500);
                    else drive.left(1, 550);
                } else {
                    drive.backwards(1, 3500);
                    sleep(100);
                    drive.turnRight(1, 2000);
                    sleep(100);
                    drive.backwards(1, 1000);
                    sleep(100);
                    drive.forwards(1, data.getStage() == DataServo.FRONT_STAGE ? 1000 : 3000);
                    sleep(100);
                    if (data.getStage() == DataServo.FRONT_STAGE) {
                        drive.right(1, 3000);
                        sleep(100);
                        drive.turnLeft(1, 4200);
                    } else drive.right(1, 500);
                }
                break;
        }
        if (data.getStage() == DataServo.FRONT_STAGE && data.getBackdrop() == DataServo.BACKDROP) {
            sleep(100);
            drive.forwards(1, 8000);
            sleep(100);
            if (data.getAlliance() == DataServo.RED_ALLIANCE) {
                if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.LEFT) drive.right(1, 2000);
                if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.MIDDLE) drive.right(1, 3000);
                if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.RIGHT) drive.right(1, 4000);
            } else {
                if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.LEFT) drive.left(1, 4000);
                if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.MIDDLE) drive.left(1, 3000);
                if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.RIGHT) drive.left(1, 2000);
            }
            sleep(100);
        }
        if (data.getBackdrop() == DataServo.BACKDROP) {
            drive.forwards(1, 3000);
            sleep(100);
            arm.up(1);
            sleep(350);
            arm.up(0.25);
            sleep(1000);
            arm.extend();
            sleep(1500);
            arm.retract();
            sleep(1000);
            arm.extend();
            sleep(1000);
            arm.retract();
            sleep(1000);
            arm.down(0.1);
            sleep(1000);
            arm.neutral();
            sleep(100);
            if (data.getStage() == DataServo.BACK_STAGE) {
                if (data.getAlliance() == DataServo.RED_ALLIANCE) {
                    if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.LEFT) drive.right(1, 4000);
                    else if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.MIDDLE) drive.right(1, 3500);
                    else if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.RIGHT) drive.right(1, 3000);
                } else {
                    if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.LEFT) drive.left(1, 3000);
                    else if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.MIDDLE) drive.left(1, 3500);
                    else if (propDetectionPipeline.getBiggestBlob() == PropDetectionPipeline.RIGHT) drive.left(1, 4000);
                }
                sleep(100);
                drive.forwards(1, 500);
            }
        }
    }
}