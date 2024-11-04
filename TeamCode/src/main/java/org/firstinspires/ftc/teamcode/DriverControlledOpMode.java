package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;

import java.lang.*;

@TeleOp(name="Driver Control", group="Driver Control")
public class DriverControlledOpMode extends LinearOpMode {
    MecanumWheels drive;
    Arm arm;

    DcMotor leftFront, leftBack, rightBack, rightFront, slideMotor, slideAngleMotor;
    Servo clawServo;

    ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        slideMotor = hardwareMap.get(DcMotor.class, "slideMotor");
        slideAngleMotor = hardwareMap.get(DcMotor.class, "slideAngleMotor");

        clawServo = hardwareMap.get(Servo.class, "clawServo");

        drive = new MecanumWheels(leftFront, leftBack, rightBack, rightFront);
        arm = new Arm(slideMotor, slideAngleMotor, clawServo);

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Note: Only one controller should be connected - toggle reverse by pressing START+A or START+B to switch controller modes
            if (Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.right_stick_y) == 0.0)
                drive.drive(gamepad2.left_stick_x * 0.5, gamepad2.left_stick_y * 0.5, gamepad2.right_stick_x * 0.5, gamepad2.right_stick_y * 0.5, true);
            else
                drive.drive(gamepad1.left_stick_x * 0.5, gamepad1.left_stick_y * 0.5, gamepad1.right_stick_x * 0.5, gamepad1.right_stick_y * 0.5, false);

            if (gamepad1.right_trigger > 0) arm.up(gamepad1.right_trigger);
            else if (gamepad2.right_trigger > 0) arm.up(gamepad2.right_trigger);
            else if (gamepad1.left_trigger > 0) arm.down(gamepad1.left_trigger * 0.3);
            else if (gamepad2.left_trigger > 0) arm.down(gamepad2.left_trigger * 0.3);
            else arm.neutral();

            if (gamepad1.left_bumper || gamepad2.left_bumper) arm.swivelUp(1.0);
            else if (gamepad1.right_bumper || gamepad2.right_bumper) arm.swivelDown(1.0);
            else arm.swivelNeutral();   

            if (gamepad1.x || gamepad2.x) {
                while (gamepad1.x || gamepad2.x);
                arm.close();
            }
            if (gamepad1.y || gamepad2.y) {
                while (gamepad1.y || gamepad2.y);
                arm.open();
            }

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front Left Wheel", leftFront.getPower());
            telemetry.addData("Back Left Wheel", leftBack.getPower());
            telemetry.addData("Back Right Wheel", rightBack.getPower());
            telemetry.addData("Front Right Wheel", rightFront.getPower());
            telemetry.addData("Slide Motor", slideMotor.getPower() + " (use the left/right trigger to control)");
            telemetry.addData("Slide Motor Encoder", slideMotor.getCurrentPosition());
            telemetry.addData("Slide Angle Motor", slideAngleMotor.getPower() + " (use the left/right shoulder buttons to control)");
            telemetry.addData("Slide Angle Encoder", slideAngleMotor.getCurrentPosition());
            telemetry.addData("Claw", (clawServo.getPosition() == 0 ? "Closed" : "Open") + " (press " + (clawServo.getPosition() == 0 ? "Y" : "X") + " to toggle)");
            telemetry.update();
        }
    }
}