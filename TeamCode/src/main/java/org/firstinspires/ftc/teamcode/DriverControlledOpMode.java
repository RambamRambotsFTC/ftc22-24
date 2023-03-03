package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.lang.Math;

@TeleOp(name="Driver Control", group="Linear Opmode")
public class DriverControlledOpMode extends LinearOpMode {
    private MecanumWheels drive;
    private Arm arm;

    private DcMotor leftBackMotor;
    private DcMotor leftFrontMotor;
    private DcMotor rightBackMotor;
    private DcMotor rightFrontMotor;

    private Servo armServo;
    private DcMotor armMotor1;
    private DcMotor armMotor2;

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

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.right_stick_y) == 0.0) {
                drive.drive(gamepad2.right_stick_x, gamepad2.right_stick_y, gamepad2.left_stick_x, gamepad2.left_stick_y);
            } else {
                drive.drive(gamepad1.right_stick_x * 0.8, gamepad1.right_stick_y * 0.8, gamepad1.left_stick_x * 0.8, gamepad1.left_stick_y * 0.8);
            }

            if (gamepad2.right_trigger > 0) arm.up(gamepad2.right_trigger * 0.9);
            else if (gamepad1.right_trigger > 0) arm.up(gamepad1.right_trigger * 0.9);
            //else arm.up(0);


            if (gamepad1.right_bumper || gamepad2.right_bumper) arm.neutral();

            if (gamepad1.left_bumper || gamepad2.left_bumper) {
                while (gamepad1.left_bumper || gamepad2.left_bumper) { }

                if (armServo.getPosition() == 1) arm.open();
                else arm.close();
            }

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front Right Wheel", rightFrontMotor.getPower());
            telemetry.addData("Front Left Wheel", leftFrontMotor.getPower());
            telemetry.addData("Back Right Wheel", rightBackMotor.getPower());
            telemetry.addData("Back Left Wheel", leftBackMotor.getPower());
            telemetry.addData("Direction", getDriveDirection(rightFrontMotor.getPower(), leftFrontMotor.getPower(), rightBackMotor.getPower(), leftBackMotor.getPower()));
            telemetry.addData("Arm Motor 1", armMotor1.getPower() );
            telemetry.addData("Arm Motor 2", armMotor2.getPower());
            telemetry.addData("Servo Position", armServo.getPosition());
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
        else if (FR == 0 && FL == 0 && BR == 0 && BL == 0) return "Not moving";
        else return "Unable to determine (Ari dumb L)";
    }
}
