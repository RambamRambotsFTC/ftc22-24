package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;

@TeleOp(name="Driver OpMode", group="Linear Opmode")
public class DriverControlledOpMode extends LinearOpMode {
    private MecanumWheels drive;
    private Arm arm;

    private DcMotor leftBackMotor;
    private DcMotor leftFrontMotor;
    private DcMotor rightBackMotor;
    private DcMotor rightFrontMotor;

    private Servo armServo;
    private CRServo armMotor1;
    private CRServo armMotor2;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        drive = new MecanumWheels(leftBackMotor, leftFrontMotor, rightBackMotor, rightFrontMotor);
        arm = new Arm(armServo, armMotor1, armMotor2);

        leftBackMotor = hardwareMap.get(DcMotor.class, "leftBackMotor");
        leftFrontMotor = hardwareMap.get(DcMotor.class, "leftFrontMotor");
        rightBackMotor = hardwareMap.get(DcMotor.class, "rightBackMotor");
        rightFrontMotor = hardwareMap.get(DcMotor.class, "rightFrontMotor");

        armServo = hardwareMap.get(Servo.class, "armServo");
        armMotor1 = hardwareMap.get(CRServo.class, "armMotor1");
        armMotor2 = hardwareMap.get(CRServo.class, "armMotor2");

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.right_stick_x) + Math.abs(gamepad1.right_stick_y) == 0.0) {
                drive.drive(gamepad2.right_stick_x * 0.3, gamepad2.right_stick_y * 0.3, gamepad2.left_stick_x * 0.3, gamepad2.left_stick_y * 0.3);
            } else {
                drive.drive(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x, gamepad1.left_stick_y);
            }

            if (gamepad2.right_trigger > 0) arm.up(gamepad2.right_trigger);
            else if (gamepad1.right_trigger > 0) arm.up(gamepad1.right_trigger);

            if (gamepad1.right_bumper || gamepad2.right_bumper) arm.neutral();

            if (gamepad1.left_bumper || gamepad2.left_bumper) {
                arm.open();
            } else {
                arm.close();
            }

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}
