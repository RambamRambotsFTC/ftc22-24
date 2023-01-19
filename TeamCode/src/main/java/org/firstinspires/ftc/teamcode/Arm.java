package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    private Servo servo;
    private DcMotor motor1;
    private CRServo motor2;

    public Arm(Servo servo, DcMotor motor1, CRServo motor2) {
        this.servo = servo;
        this.motor1 = motor1;
        this.motor2 = motor2;
        motor1.setDirection(DcMotorSimple.Direction.REVERSE);
        motor2.setDirection(CRServo.Direction.FORWARD);
    }

    public void up(double power) {
        motor1.setPower(power);
        motor2.setPower(power);
    }
    public void neutral() {
        motor1.setPower(0.0);
        motor2.setPower(0.0);
    }

    public void open() { servo.setPosition(1.0); }
    public void close() { servo.setPosition(0.0); }
}
