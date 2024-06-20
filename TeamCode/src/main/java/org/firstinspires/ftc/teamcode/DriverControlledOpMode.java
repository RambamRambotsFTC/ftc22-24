package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;

import java.lang.*;

@TeleOp(name="Driver Control", group="Driver Control")
public class DriverControlledOpMode extends LinearOpMode {
    MecanumWheels drive;

    DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

    ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");

        drive = new MecanumWheels(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor);

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Note: Only one controller should be connected - toggle reverse by pressing START+A or START+B to switch controller modes
            if (Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.right_stick_y) == 0.0)
                drive.drive(gamepad2.left_stick_x, gamepad2.left_stick_y, gamepad2.right_stick_x, gamepad2.right_stick_y, true);
            else
                drive.drive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.right_stick_y, false);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front Left Wheel", frontLeftMotor.getPower());
            telemetry.addData("Front Right Wheel", frontRightMotor.getPower());
            telemetry.addData("Back Left Wheel", backLeftMotor.getPower());
            telemetry.addData("Back Right Wheel", backRightMotor.getPower());
            telemetry.addData("Direction", getDriveDirection(frontLeftMotor.getPower(), frontRightMotor.getPower(), backLeftMotor.getPower(), backRightMotor.getPower()));
            telemetry.update();
        }
    }

    public String getDriveDirection(double FL, double FR, double BL, double BR) {
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