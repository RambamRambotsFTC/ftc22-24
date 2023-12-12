package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;

class MecanumWheels {
    private DcMotor leftBackMotor;
    private DcMotor leftFrontMotor;
    private DcMotor rightBackMotor;
    private DcMotor rightFrontMotor;

    private ElapsedTime runtime = new ElapsedTime();

    public MecanumWheels(DcMotor leftBackMotor, DcMotor leftFrontMotor, DcMotor rightBackMotor, DcMotor rightFrontMotor) {
        this.leftBackMotor = leftBackMotor;
        this.leftFrontMotor = leftFrontMotor;
        this.rightBackMotor = rightBackMotor;
        this.rightFrontMotor = rightFrontMotor;

        this.leftBackMotor.setDirection(Direction.FORWARD);
        this.leftFrontMotor.setDirection(Direction.FORWARD);
        this.rightBackMotor.setDirection(Direction.FORWARD);
        this.rightFrontMotor.setDirection(Direction.FORWARD);
    }

    public void drive(double rx, double ry, double lx, double ly) {
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

        this.leftBackMotor.setPower(BL);
        this.leftFrontMotor.setPower(FL);
        this.rightBackMotor.setPower(BR);
        this.rightFrontMotor.setPower(FR);
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