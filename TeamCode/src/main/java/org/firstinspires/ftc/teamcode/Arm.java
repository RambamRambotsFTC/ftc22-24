package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;

public class Arm {
    private final DcMotor slideMotor, slideAngleMotor;
    private final Servo clawServo;

    public Arm(DcMotor slideMotor, DcMotor slideAngleMotor, Servo clawServo) {
        this.slideMotor = slideMotor;
        this.slideAngleMotor = slideAngleMotor;
        this.clawServo = clawServo;

        this.slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.slideAngleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slideAngleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void up(double power) {
        slideMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        slideMotor.setPower(power);
    }
    public void neutral() { slideMotor.setPower(0.0); }
    public void down(double power) {
        slideMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        slideMotor.setPower(power);
    }

    public void swivelUp(double power) {
        slideAngleMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        slideAngleMotor.setPower(power);
    }
    public void swivelNeutral() { slideAngleMotor.setPower(0.0); }
    public void swivelDown(double power) {
        slideAngleMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        slideAngleMotor.setPower(power);
    }

    public void open() {
        clawServo.setPosition(1.0);
    }
    public void close() {
        clawServo.setPosition(0.0);
    }
}