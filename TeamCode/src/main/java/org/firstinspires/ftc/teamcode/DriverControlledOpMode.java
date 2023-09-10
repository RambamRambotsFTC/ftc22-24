package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;

@TeleOp(name="Driver Control", group="Linear Opmode")
public class DriverControlledOpMode extends LinearOpMode {
    private MecanumWheels drive;

    private DcMotor leftBackMotor;
    private DcMotor leftFrontMotor;
    private DcMotor rightBackMotor;
    private DcMotor rightFrontMotor;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftBackMotor = hardwareMap.get(DcMotor.class, "leftRear");
        leftFrontMotor = hardwareMap.get(DcMotor.class, "leftFront");
        rightBackMotor = hardwareMap.get(DcMotor.class, "rightRear");
        rightFrontMotor = hardwareMap.get(DcMotor.class, "rightFront");

        drive = new MecanumWheels(leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor);

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.right_stick_y) == 0.0) {
                drive.drive(gamepad2.right_stick_x * 0.3, gamepad2.right_stick_y * 0.3, gamepad2.left_stick_x * 0.3, gamepad2.left_stick_y * 0.3);
            } else {
                drive.drive(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x, gamepad1.left_stick_y);
            }

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front Right Wheel", rightFrontMotor.getPower());
            telemetry.addData("Front Left Wheel", leftFrontMotor.getPower());
            telemetry.addData("Back Right Wheel", rightBackMotor.getPower());
            telemetry.addData("Back Left Wheel", leftBackMotor.getPower());
            telemetry.addData("Direction", getDriveDirection(rightFrontMotor.getPower(), leftFrontMotor.getPower(), rightBackMotor.getPower(), leftBackMotor.getPower()));
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
        else return "Unable to determine (Ari dumb L)";
    }
}
