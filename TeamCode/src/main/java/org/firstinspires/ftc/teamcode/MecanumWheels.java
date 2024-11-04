package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.Range;

class MecanumWheels {
    private final DcMotor leftFront, leftBack, rightBack, rightFront;

    public MecanumWheels(DcMotor leftFront, DcMotor leftBack, DcMotor rightBack, DcMotor rightFront) {
        this.leftFront = leftFront;
        this.leftBack = leftBack;
        this.rightBack = rightBack;
        this.rightFront = rightFront;
    }

    public void drive(double lx, double ly, double rx, double ry, boolean reverse) {
        double LF = ly, LB = ly, RB = -ry, RF = -ry;

        if (rx >= 0.25 || lx >= 0.25) {
            double lrx = (rx + lx) / 2;
            LF += -lrx;
            LB += lrx;
            RB += lrx;
            RF += -lrx;
        }
        if (lx <= -0.25 || rx <= -0.25) {
            double llx = (rx + lx) / 2;
            LF += -llx;
            LB += llx;
            RB += llx;
            RF += -llx;
        }
        LF = Range.clip(LF, -1, 1);
        LB = Range.clip(LB, -1, 1);
        RB = Range.clip(RB, -1, 1);
        RF = Range.clip(RF, -1, 1);

        leftFront.setPower(reverse ? RB : LF);
        leftBack.setPower(reverse ? RF : LB);
        rightBack.setPower(reverse ? LF : RB);
        rightFront.setPower(reverse ? LB : RF);
    }
}