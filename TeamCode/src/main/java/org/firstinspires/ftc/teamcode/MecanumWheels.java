package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.util.Range;

class MecanumWheels {

    // initializes the 4 local Powers & Motors, & sets them to 0
    private DcMotor frontRight;
    private double frontRightPower = 0;
    private DcMotor frontLeft;
    private double frontLeftPower = 0;
    private DcMotor backRight;
    private double backRightPower = 0;
    private CRServo backLeft;
    private double backLeftPower = 0;

    public MecanumWheels (CRServo leftBack, DcMotor leftFront, DcMotor rightBack, DcMotor rightFront){
        // sets the local motors to Graber's motors
        frontRight = rightFront;
        frontLeft = leftFront;
        backRight = rightBack;
        backLeft = leftBack;
        //sets the local motor's directions to forward
        frontLeft.setDirection(Direction.FORWARD);
        frontRight.setDirection(Direction.FORWARD);
        backLeft.setDirection(Direction.REVERSE);
        backRight.setDirection(Direction.FORWARD);
    }

    // sets the 4 local to what Graber gives when the method is called
    public void drive (double rx, double ry, double lx, double ly) {
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

        frontRight.setPower(FR);
        frontLeft.setPower(FL);
        backRight.setPower(BR);
        backLeft.setPower(BL);
    }
//        if (rx < 0.25 && rx > -0.25 && lx > -0.25 && lx < 0.25) {
//            frontRight.setPower(-ry);
//            frontLeft.setPower(ly);
//            backRight.setPower(-ry);
//            backLeft.setPower(ly);
//        }
//        if (rx >= 0.25 || lx > 0.25 ) {
//            double lrx = (rx + lx)/2;
//            frontRight.setPower(-lrx);
//            frontLeft.setPower(-lrx);
//            backRight.setPower(lrx);
//            backLeft.setPower(lrx);
//        }
//        if (lx <= -0.25 || rx <= -0.25) {
//            double llx = (rx + lx)/2;
//            frontRight.setPower(-llx);
//            frontLeft.setPower(-llx);
//            backRight.setPower(llx);
//            backLeft.setPower(llx);
//        }



    // setters
//    private void setFrontRightPower(double angle, double magnitude, double rotationalVelocity) {
//        frontRightPower = (-(Math.sin(angle - (Math.PI/4.0) )) * magnitude - rotationalVelocity)/2;
//    }
//    private void setFrontLeftPower(double angle, double magnitude, double rotationalVelocity) {
//        frontLeftPower = (Math.sin(angle + (Math.PI/4.0) ) * magnitude + rotationalVelocity)/2;
//    }
//    private void setBackRightPower(double angle, double magnitude, double rotationalVelocity) {
//        backRightPower = (-(Math.sin(angle + (Math.PI/4.0) )) * magnitude - rotationalVelocity)/2;
//    }
//    private void setBackLeftPower(double angle, double magnitude, double rotationalVelocity) {
//        backLeftPower = (Math.sin(angle - (Math.PI/4.0) ) * magnitude + rotationalVelocity)/2;
//    }


}