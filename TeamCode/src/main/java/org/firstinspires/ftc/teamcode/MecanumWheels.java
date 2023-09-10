package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;

class MecanumWheels {
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

    private ElapsedTime runtime = new ElapsedTime();

    public MecanumWheels(DcMotor leftBack, DcMotor leftFront, DcMotor rightBack, DcMotor rightFront) {
        frontRight = rightFront;
        frontLeft = leftFront;
        backRight = rightBack;
        backLeft = leftBack;

        frontLeft.setDirection(Direction.FORWARD);
        frontRight.setDirection(Direction.FORWARD);
        backLeft.setDirection(Direction.FORWARD);
        backRight.setDirection(Direction.FORWARD);
    }

    public void drive(double rx, double ry, double lx, double ly) {
        double FR = -ry;
        double FL = ly;
        double BR = -ry;
        double BL = ly;

        if (rx >= 0.25 || lx >= 0.25) {
            double lrx = (rx + lx) / 2;
            FR += -lrx;
            FL += lrx;
            BR += lrx;
            BL += -lrx;
        }
        if (lx <= -0.25 || rx <= -0.25) {
            double llx = (rx + lx) / 2;
            FR += -llx;
            FL += llx;
            BR += llx;
            BL += -llx;
        }
        FR = Range.clip(FR, -1, 1);
        FL = Range.clip(FL, -1, 1);
        BR = Range.clip(BR, -1, 1);
        BL = Range.clip(BL, -1, 1);

        frontRight.setPower(FR);
        frontLeft.setPower(FL);
        backRight.setPower(BR);
        backLeft.setPower(BL);
    }

    public void forward(double seconds) {
        runtime.reset();
        drive(0, -1, 0, -1);
        while (runtime.milliseconds() < seconds * 1000) { }
        drive(0, 0, 0, 0);
    }

    public void backwards(double seconds) {
        runtime.reset();
        drive(0, 1, 0, 1);
        while (runtime.milliseconds() < seconds * 1000) { }
        drive(0, 0, 0, 0);
    }

    public void left(double seconds) {
        runtime.reset();
        drive(-1, 0, -1, 0);
        while (runtime.milliseconds() < seconds * 1000) { }
        drive(0, 0, 0, 0);
    }

    public void right(double seconds) {
        runtime.reset();
        drive(1, 0, 1, 0);
        while (runtime.milliseconds() < seconds * 1000) { }
        drive(0, 0, 0, 0);
    }
}