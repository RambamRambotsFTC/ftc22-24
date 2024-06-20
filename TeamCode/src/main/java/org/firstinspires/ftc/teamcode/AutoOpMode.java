package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

@Autonomous(name="Autonomous", group="Autonomous")
public class AutoOpMode extends LinearOpMode {
    MecanumWheels drive;

    DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

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
    }
}