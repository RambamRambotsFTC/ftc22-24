package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.Range;

class MecanumWheels {
    private final DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;

    public MecanumWheels(DcMotor frontLeftMotor, DcMotor frontRightMotor, DcMotor backLeftMotor, DcMotor backRightMotor) {
        this.frontLeftMotor = frontLeftMotor;
        this.frontRightMotor = frontRightMotor;
        this.backLeftMotor = backLeftMotor;
        this.backRightMotor = backRightMotor;
    }

    public void drive(double lx, double ly, double rx, double ry, boolean reverse) {
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
}