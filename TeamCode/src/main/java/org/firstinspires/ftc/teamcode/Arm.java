package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    private final DcMotor motor;
    private final Servo armServo, clawServo;

    public Arm(DcMotor motor, Servo armServo, Servo clawServo) {
        this.motor = motor;
        this.armServo = armServo;
        this.clawServo = clawServo;
    }

    public void up(double power) {
        motor.setDirection(Direction.FORWARD);
        motor.setPower(power);
    }
    public void neutral() { motor.setPower(0.0); }
    public void down(double power) {
        motor.setDirection(Direction.REVERSE);
        motor.setPower(power);
    }

    public void extend() { armServo.setPosition(1.0); }
    public void retract() { armServo.setPosition(0.0); }

    public void open() { clawServo.setPosition(0.0); }
    public void close() { clawServo.setPosition(1.0); }
}