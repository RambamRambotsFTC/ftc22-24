package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Carousel {
    private Servo servo;
    private ElapsedTime timer;
    private DcMotor motor;

    public Carousel(DcMotor motor ) {
        this.motor = motor;
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void on() { motor.setPower(0.25); }
    public void off() { motor.setPower(0.0); }
}
