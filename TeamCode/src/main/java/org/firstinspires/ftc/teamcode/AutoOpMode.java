package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

@Autonomous(name="Autonomous", group="Autonomous")
public class AutoOpMode extends LinearOpMode {
    MecanumWheels drive;
    Arm arm;

    DcMotor leftFront, leftBack, rightBack, rightFront, slideMotor, slideAngleMotor;
    Servo clawServo;

    @Override
    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        slideMotor = hardwareMap.get(DcMotor.class, "slideMotor");
        slideAngleMotor = hardwareMap.get(DcMotor.class, "slideAngleMotor");

        drive = new MecanumWheels(leftFront, leftBack, rightBack, rightFront);
        arm = new Arm(slideMotor, slideAngleMotor, clawServo);



        waitForStart();
    }
}