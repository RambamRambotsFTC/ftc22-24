package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.Range;

class MecanumWheels {
    private final DcMotor backLeftMotor, frontLeftMotor, backRightMotor, frontRightMotor;

    public MecanumWheels(DcMotor backLeftMotor, DcMotor frontLeftMotor, DcMotor backRightMotor, DcMotor frontRightMotor) {
        this.backLeftMotor = backLeftMotor;
        this.frontLeftMotor = frontLeftMotor;
        this.backRightMotor = backRightMotor;
        this.frontRightMotor = frontRightMotor;

        this.backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void drive(double rx, double ry, double lx, double ly, boolean reverse) {
        double FR = -ry;
        double FL = ly;
        double BR = -ry;
        double BL = ly;

        if (rx >= 0.25 || lx >= 0.25) {
            double lrx = (rx + lx) / 2;
            FR += -lrx;
            FL += -lrx;
            BR += lrx;
            BL += lrx;
        }
        if (lx <= -0.25 || rx <= -0.25) {
            double llx = (rx + lx) / 2;
            FR += -llx;
            FL += -llx;
            BR += llx;
            BL += llx;
        }
        FR = Range.clip(FR, -1, 1);
        FL = Range.clip(FL, -1, 1);
        BR = Range.clip(BR, -1, 1);
        BL = Range.clip(BL, -1, 1);

        backLeftMotor.setPower(reverse ? FR : BL);
        frontLeftMotor.setPower(reverse ? BR : FL);
        backRightMotor.setPower(reverse ? FL : BR);
        frontRightMotor.setPower(reverse ? BL : FR);
    }

    public void driveWithEncoders(double speed, int bl, int fl, int br, int fr) {
        backLeftMotor.setTargetPosition(backLeftMotor.getCurrentPosition() + bl);
        frontLeftMotor.setTargetPosition(frontLeftMotor.getCurrentPosition() + fl);
        backRightMotor.setTargetPosition(backRightMotor.getCurrentPosition() + br);
        frontRightMotor.setTargetPosition(frontRightMotor.getCurrentPosition() + fr);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftMotor.setPower(speed);
        frontRightMotor.setPower(speed);
        backRightMotor.setPower(speed);
        frontLeftMotor.setPower(speed);
        while (backLeftMotor.isBusy() || backRightMotor.isBusy() || frontLeftMotor.isBusy() || frontRightMotor.isBusy());
        frontLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
    }

    public void forwards(double speed, int amount) { driveWithEncoders(speed, -amount, -amount, amount, amount); }
    public void backwards(double speed, int amount) { driveWithEncoders(speed, amount, amount, -amount, -amount); }
    public void left(double speed, int amount) { driveWithEncoders(speed, -amount, amount, -amount, amount); }
    public void right(double speed, int amount) { driveWithEncoders(speed, amount, -amount, amount, -amount); }
    public void turnLeft(double speed, int amount) { driveWithEncoders(speed, amount, amount, amount, amount); }
    public void turnRight(double speed, int amount) { driveWithEncoders(speed, -amount, -amount, -amount, -amount); }
}