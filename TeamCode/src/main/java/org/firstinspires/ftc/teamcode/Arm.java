package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;

public class Arm {
    private final DcMotor motor;
    private final Servo pocketServo1, pocketServo2;

    public Arm(DcMotor motor, Servo pocketServo1, Servo pocketServo2) {
        this.motor = motor;
        this.pocketServo1 = pocketServo1;
        this.pocketServo2 = pocketServo2;

        this.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.pocketServo2.setDirection(Servo.Direction.REVERSE);
    }

    public void up(double power) {
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setPower(power);
    }
    public void neutral() { motor.setPower(0.0); }
    public void down(double power) {
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setPower(power);
    }

    public void retract() {
        pocketServo1.setPosition(0.23);
        pocketServo2.setPosition(0.23);
    }
    public void extend() {
        pocketServo1.setPosition(0.1);
        pocketServo2.setPosition(0.1);
    }
}