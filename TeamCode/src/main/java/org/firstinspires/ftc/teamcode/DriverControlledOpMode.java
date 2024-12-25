package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.*;
import java.util.Locale;

@Config
@TeleOp(name="Driver Control", group="Driver Control")
public class DriverControlledOpMode extends LinearOpMode {
    public static double DRIVE_POWER_A = 0.5, DRIVE_POWER_B = 0.25,
        SLIDE_POWER_A = 0.25, SLIDE_POWER_B = 1.0,
        ANGLE_POWER_A = 0.25, ANGLE_POWER_B = 1.0,
        HANG_POWER = 0.75;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor slideMotor = hardwareMap.get(DcMotor.class, "slideMotor");
        DcMotor slideAngleMotor = hardwareMap.get(DcMotor.class, "slideAngleMotor");
        DcMotor hangMotor1 = hardwareMap.get(DcMotor.class, "hangMotor1");
        DcMotor hangMotor2 = hardwareMap.get(DcMotor.class, "hangMotor2");
        Servo clawServo = hardwareMap.get(Servo.class, "clawServo");

        MecanumWheels drive = new MecanumWheels(leftFront, leftBack, rightBack, rightFront);
        Arm arm = new Arm(slideMotor, slideAngleMotor);
        Claw claw = new Claw(clawServo);
        Hang hang = new Hang(hangMotor1, hangMotor2);

        waitForStart();
        ElapsedTime runtime = new ElapsedTime();
        Telemetry telemetry = FtcDashboard.getInstance().getTelemetry();

        while (opModeIsActive()) {
            if (Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.right_stick_y) == 0.0)
                drive.drive(gamepad2.left_stick_x * DRIVE_POWER_B, gamepad2.left_stick_y * DRIVE_POWER_B, gamepad2.right_stick_x * DRIVE_POWER_B, gamepad2.right_stick_y * DRIVE_POWER_B);
            else
                drive.drive(gamepad1.left_stick_x * DRIVE_POWER_A, gamepad1.left_stick_y * DRIVE_POWER_A, gamepad1.right_stick_x * DRIVE_POWER_A, gamepad1.right_stick_y * DRIVE_POWER_A);

            if (gamepad1.left_bumper) arm.retract(SLIDE_POWER_A);
            else if (gamepad1.right_bumper) arm.extend(SLIDE_POWER_A);
            else if (gamepad2.left_bumper) arm.retract(SLIDE_POWER_B);
            else if (gamepad2.right_bumper) arm.extend(SLIDE_POWER_B);
            else arm.neutral();

            if (gamepad1.right_trigger > 0) arm.swivelUp(gamepad1.right_trigger * ANGLE_POWER_A);
            else if (gamepad2.right_trigger > 0) arm.swivelUp(gamepad2.right_trigger * ANGLE_POWER_B);
            else if (gamepad1.left_trigger > 0) arm.swivelDown(gamepad1.left_trigger * ANGLE_POWER_A);
            else if (gamepad2.left_trigger > 0) arm.swivelDown(gamepad2.left_trigger * ANGLE_POWER_B);
            else arm.swivelNeutral();

            if (gamepad1.x || gamepad2.x) {
                while (gamepad1.x || gamepad2.x);
                claw.close();
            }
            if (gamepad1.y || gamepad2.y) {
                while (gamepad1.y || gamepad2.y);
                claw.open();
            }

            if (gamepad1.dpad_up || gamepad2.dpad_up) hang.spool(HANG_POWER);
            else if (gamepad1.dpad_down || gamepad2.dpad_down) hang.unspool(HANG_POWER);
            else hang.neutral();

            if (gamepad1.a) {
                while (gamepad1.a);
                // TODO: turn -90 deg
            }
            if (gamepad1.b) {
                while (gamepad1.b);
                // TODO: turn 90 deg
            }

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addLine();
            telemetry.addData("Front Left Wheel", leftFront.getPower());
            telemetry.addData("Back Left Wheel", leftBack.getPower());
            telemetry.addData("Back Right Wheel", rightBack.getPower());
            telemetry.addData("Front Right Wheel", rightFront.getPower());
            telemetry.addLine();
            telemetry.addData("Slide Motor", slideMotor.getPower());
            telemetry.addData("Slide Motor Encoder", String.format(Locale.US, "%d ticks; Max: %d; Length: %.2f inches", slideMotor.getCurrentPosition(), arm.getMaxSlideLength(), arm.getSlideLength()));
            telemetry.addData("Slide Angle Motor", slideAngleMotor.getPower());
            telemetry.addData("Slide Angle Encoder", String.format(Locale.US, "%d ticks; Angle: %.2f radians", slideAngleMotor.getCurrentPosition(), arm.getAngle()));
            telemetry.addLine();
            telemetry.addData("Claw", (clawServo.getPosition() == 0 ? "Open" : "Closed") + " (press " + (clawServo.getPosition() == 0 ? "X" : "Y") + " to toggle)");
            telemetry.addLine();
            telemetry.addData("Hang Motors", "Going " + (hangMotor1.getPower() < 0 ? "up": hangMotor1.getPower() > 0 ? "down" : "neutral"));
            telemetry.update();
        }
    }
}