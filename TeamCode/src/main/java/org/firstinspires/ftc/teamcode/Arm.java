package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Arm {
    private Servo servo;
    private DcMotor motor1;
    private DcMotor motor2;

    private ElapsedTime runtime = new ElapsedTime();

    public Arm(Servo servo, DcMotor motor1, DcMotor motor2) {
        this.servo = servo;
        this.motor1 = motor1;
        this.motor2 = motor2;

        motor1.setDirection(Direction.FORWARD);
        motor2.setDirection(Direction.FORWARD);
    }

    public void up(double power) {
        motor1.setPower(power);
        motor2.setPower(power);
    }
    public void neutral() {
        motor1.setPower(-0.25);
        motor2.setPower(-0.25);

        runtime.reset();
        while (runtime.milliseconds() < 750) { }

        motor1.setPower(0.0);
        motor2.setPower(0.0);
    }

    public void open() { servo.setPosition(0.0); }
    public void close() { servo.setPosition(1.0); }
}
