package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;

import java.lang.*;

@TeleOp(name="Driver Control", group="Driver Control")
public class DriverControlledOpMode extends LinearOpMode {
    MecanumWheels drive;
    Arm arm;
    Drone drone;
    Intake intake;

    DcMotor leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor, armMotor, intakeMotor1, intakeMotor2;
    Servo pocketServo1, pocketServo2, droneServo;

    ElapsedTime runtime = new ElapsedTime();

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

        drive = new MecanumWheels(leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor);
        arm = new Arm(armMotor, pocketServo1, pocketServo2);
        intake = new Intake(intakeMotor1, intakeMotor2);
        drone = new Drone(droneServo);

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.right_stick_y) == 0.0)
                drive.drive(gamepad2.right_stick_x, gamepad2.right_stick_y, gamepad2.left_stick_x, gamepad2.left_stick_y, true);
            else
                drive.drive(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x, gamepad1.left_stick_y, false);

            if (gamepad1.right_trigger > 0) arm.up(gamepad1.right_trigger);
            else if (gamepad2.right_trigger > 0) arm.up(gamepad2.right_trigger);
            else if (gamepad1.left_trigger > 0) arm.down(gamepad1.left_trigger * 0.3);
            else if (gamepad2.left_trigger > 0) arm.down(gamepad2.left_trigger * 0.3);
            else arm.neutral();

            if (gamepad1.right_bumper || gamepad2.right_bumper) {
                while (gamepad1.right_bumper || gamepad2.right_bumper);
                if (pocketServo1.getPosition() < 0.15) arm.retract();
                else arm.extend();
            }

            if (gamepad1.x || gamepad2.x) drone.launch();
            if (gamepad1.y || gamepad2.y) drone.reset();

            if (gamepad1.left_bumper || gamepad2.left_bumper) {
                while (gamepad1.left_bumper || gamepad2.left_bumper);
                intake.toggle();
            }
            if (gamepad1.back || gamepad2.back) {
                while (gamepad1.back || gamepad2.back);
                intake.reverse();
            }
            intake.runMotor();

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front Right Wheel", rightFrontMotor.getPower());
            telemetry.addData("Front Left Wheel", leftFrontMotor.getPower());
            telemetry.addData("Back Right Wheel", rightBackMotor.getPower());
            telemetry.addData("Back Left Wheel", leftBackMotor.getPower());
            telemetry.addData("Direction", getDriveDirection(rightFrontMotor.getPower(), leftFrontMotor.getPower(), rightBackMotor.getPower(), leftBackMotor.getPower()));
            telemetry.addData("Arm Motor", armMotor.getPower() + " (use the left/right trigger to control)");
            telemetry.addData("Pocket Extender", (pocketServo1.getPosition() > 0.15 ? "Retracted" : "Extended") + " (press the right bumper to toggle)");
            telemetry.addData("Drone Launcher", (droneServo.getPosition() > 0 ? "Waiting for reset" : "Ready to launch") + " (press " + (droneServo.getPosition() > 0 ? "Y" : "X") + ")");
            telemetry.addData("Intake Motor", (intakeMotor1.getPower() > 0 ? "Running" : "Not running") + " (press the left shoulder button to toggle)");
            telemetry.update();
        }
    }

    public String getDriveDirection(double FR, double FL, double BR, double BL) {
        if (FR > 0 && FL < 0 && BR > 0 && BL < 0) return "Forward";
        else if (FR < 0 && FL > 0 && BR < 0 && BL > 0) return "Backwards";
        else if (FR > 0 && FL > 0 && BR < 0 && BL < 0) return "Left";
        else if (FR < 0 && FL < 0 && BR < 0 && BL < 0) return "Left (in place)";
        else if (FR < 0 && FL < 0 && BR > 0 && BL > 0) return "Right";
        else if (FR > 0 && FL > 0 && BR > 0 && BL > 0) return "Right (in place)";
        else if (FR == 0 && FL < 0 && BR == 0 && BL < 0) return "Forwards right rotation";
        else if (FR == 0 && FL > 0 && BR == 0 && BL > 0) return "Backwards right rotation";
        else if (FR > 0 && FL == 0 && BR > 0 && BL == 0) return "Forwards left rotation";
        else if (FR < 0 && FL == 0 && BR < 0 && BL == 0) return "Backwards left rotation";
        else if (FR == 0 && FL == 0 && BR == 0 && BL == 0) return "Not moving";
        else return "Unable to determine";
    }
}