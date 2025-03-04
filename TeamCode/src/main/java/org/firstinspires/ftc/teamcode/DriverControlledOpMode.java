package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;

import java.util.Locale;

@Config
@TeleOp(name="Driver Control", group="Driver Control")
public class DriverControlledOpMode extends LinearOpMode {
    public static double drivePowerA = 0.5, drivePowerB = 0.25,
        slidePowerA = 0.25, slidePowerB = 1.0,
        anglePowerA = 0.25, anglePowerB = 1.0;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront"),
                leftBack = hardwareMap.get(DcMotor.class, "leftBack"),
                rightBack = hardwareMap.get(DcMotor.class, "rightBack"),
                rightFront = hardwareMap.get(DcMotor.class, "rightFront"),
                slideMotor = hardwareMap.get(DcMotor.class, "slideMotor"),
                slideAngleMotor = hardwareMap.get(DcMotor.class, "slideAngleMotor"),
                secondarySlideMotor = hardwareMap.get(DcMotor.class, "secondarySlideMotor");
        Servo clawServo = hardwareMap.get(Servo.class, "clawServo");

        AutoOpMode.RRDrive drive = new AutoOpMode.RRDrive(hardwareMap);
        Arm arm = new Arm(slideMotor, slideAngleMotor, new SupportingSlide(secondarySlideMotor));
        AutoOpMode.RRArm zeroingAssist = new AutoOpMode.RRArm(hardwareMap);
        Claw claw = new Claw(clawServo);

        waitForStart();
        ElapsedTime runtime = new ElapsedTime();

        boolean isAngleZeroed = false;

        while (opModeIsActive()) {
            if (Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.right_stick_y) == 0.0) drive.setDrivePowers(gamepad2, drivePowerB);
            else drive.setDrivePowers(gamepad1, drivePowerA);

            if (isAngleZeroed) {
                if (gamepad1.left_bumper) arm.retract(slidePowerA);
                else if (gamepad1.right_bumper) arm.extend(slidePowerA);
                else if (gamepad2.left_bumper) arm.retract(slidePowerB);
                else if (gamepad2.right_bumper) arm.extend(slidePowerB);
                else arm.neutral();

                if (gamepad1.left_trigger > 0) arm.swivelDown(gamepad1.left_trigger * anglePowerA);
                else if (gamepad2.left_trigger > 0) arm.swivelDown(gamepad2.left_trigger * anglePowerB);
                else if (gamepad1.right_trigger > 0) arm.swivelUp(gamepad1.right_trigger * anglePowerA);
                else if (gamepad2.right_trigger > 0) arm.swivelUp(gamepad2.right_trigger * anglePowerB);
                else arm.swivelNeutral();
            } else {
                arm.overrideRetract(gamepad2.left_bumper ? slidePowerB : gamepad1.left_bumper ? slidePowerA : 0);
                arm.overrideSwivelUp(gamepad2.right_trigger > 0 ? gamepad2.right_trigger * anglePowerB : gamepad1.right_trigger * anglePowerA);
            }

            if (gamepad1.x || gamepad2.x) {
                while (gamepad1.x || gamepad2.x);
                claw.close();
            }
            if (gamepad1.y || gamepad2.y) {
                while (gamepad1.y || gamepad2.y);
                claw.open();
            }

            if (gamepad1.dpad_left) {
                while (gamepad1.dpad_left);
                Actions.runBlocking(drive.turnInPlace(Math.toRadians(90)));
            }
            if (gamepad1.dpad_right) {
                while (gamepad1.dpad_right);
                Actions.runBlocking(drive.turnInPlace(Math.toRadians(-90)));
            }

            if (gamepad1.back || gamepad2.back) {
                while (gamepad1.back || gamepad2.back);
                if (isAngleZeroed) isAngleZeroed = false;
                else {
                    Actions.runBlocking(zeroingAssist.zeroAngleMotor());
                    slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    isAngleZeroed = true;
                }
            }

            /* if (gamepad1.dpad_up) {
                while (gamepad1.dpad_up);
                secondarySlideMotor.setTargetPosition(125);
                secondarySlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                secondarySlideMotor.setPower(0.75);
            } */

            telemetry.addData("Runtime", runtime);
            telemetry.addLine();
            telemetry.addData("Front Left Wheel", leftFront.getPower());
            telemetry.addData("Back Left Wheel", leftBack.getPower());
            telemetry.addData("Back Right Wheel", rightBack.getPower());
            telemetry.addData("Front Right Wheel", rightFront.getPower());
            telemetry.addLine();
            if (isAngleZeroed) {
                telemetry.addData("Slide Motor", slideMotor.getPower());
                telemetry.addData("Slide Motor Encoder", String.format(Locale.US, "%d ticks; Max: %d; Length: %.2f inches", slideMotor.getCurrentPosition(), arm.getMaxSlideLength(arm.getAngle()), arm.getSlideLength()));
                telemetry.addLine();
                telemetry.addData("Slide Angle Motor", slideAngleMotor.getPower());
                telemetry.addData("Slide Angle Encoder", String.format(Locale.US, "%d ticks; Angle: %.2f rad (%.2f°)", slideAngleMotor.getCurrentPosition(), arm.getAngle(), Math.toDegrees(arm.getAngle())));
                telemetry.addLine();
                /* telemetry.addData("Secondary Slide Motor Encoder", slideAngleMotor.getCurrentPosition());
                telemetry.addLine(); */
                telemetry.addLine("Press the BACK button on the gamepad to enter override mode for the slide and angle motors.");
            } else telemetry.addLine("After you have swiveled the arm to a reasonable angle and zeroed the slide, press the BACK button on the gamepad again to zero the angle motor.");
            telemetry.addLine();
            telemetry.addData("Claw", (clawServo.getPosition() == 0 ? "Open" : "Closed") + " (press " + (clawServo.getPosition() == 0 ? "X" : "Y") + " to toggle)");
            telemetry.update();
        }
    }
}