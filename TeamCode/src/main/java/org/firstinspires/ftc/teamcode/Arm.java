package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.*;

@Config
public class Arm {
    private final DcMotor slideMotor, slideAngleMotor;
    // private final SupportingSlide supportingSlide;

    public static double angleGearRatio = 20.0 / 15.0, angleEncoderCountsPerRevolution = 28 * Math.pow(1 + (46.0 / 17.0), 4); // ~5281.1
//    public static double ANGLE_GEAR_RATIO = 90.0 / 45.0, ANGLE_ENCODER_COUNTS_PER_REVOLUTION = 28 * Math.pow(1 + (46.0 / 17.0), 4); // ~5281.1
    public static double slideInPerTick = 29.4 / 3348; // ~0.00878 - found by measuring the difference between the retracted length and the extended length divided by the difference of ticks extended

    public static double maxDistance = 33.0; // Measure in inches, starts at focal point of slide
    public static double retractedLength = 16.0, staticLength = 2.5, maxLength = 50.0; // in.
    public static double angleScale = 90.0 / 80.0;

    public static int encoderTickTolerance = 50;
    public static double encoderPowerLevel = 0.75;

//    public static int angleToTicks(double angle) { return (int)((angle / Math.PI) * ANGLE_ENCODER_COUNTS_PER_REVOLUTION); } // old
//    public static double ticksToAngle(int ticks) { return (2 * Math.PI * ticks) / (ANGLE_ENCODER_COUNTS_PER_REVOLUTION * ANGLE_GEAR_RATIO); } // old
//    public static double ticksToAngle(int ticks) { return (Math.PI * ticks * ANGLE_GEAR_RATIO) / (2 * ANGLE_ENCODER_COUNTS_PER_REVOLUTION); } // attempt at new
    public static int angleToTicks(double angle) { return (int)((angle / (2 * Math.PI)) * angleEncoderCountsPerRevolution * angleGearRatio * (1 / angleScale)); }
    public static double ticksToAngle(int ticks) { return (2 * Math.PI * ticks * angleScale) / (angleEncoderCountsPerRevolution * angleGearRatio); }
    public static int inchesToTicks(double inches) { return (int)(inches / slideInPerTick); }
    public static double ticksToInches(int ticks) { return (ticks * slideInPerTick); }

    public Arm(DcMotor slideMotor, DcMotor slideAngleMotor, SupportingSlide supportingSlide) {
        this.slideMotor = slideMotor;
        this.slideAngleMotor = slideAngleMotor;

        this.slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slideAngleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.slideAngleMotor.setDirection(DcMotor.Direction.REVERSE);
        this.slideAngleMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // this.supportingSlide = supportingSlide;
    }

    public void extend(double power) {
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (slideMotor.getCurrentPosition() + encoderTickTolerance < getMaxSlideLength(getAngle()) && slideMotor.getCurrentPosition() + encoderTickTolerance < inchesToTicks(maxLength - retractedLength) /* && getAngle() > Math.toRadians(17.5) */) slideMotor.setPower(power);
        else neutral();
    }
    public void neutral() {
        if (getAngle() + ticksToAngle(encoderTickTolerance) < Math.PI / 2 && slideMotor.getCurrentPosition() + encoderTickTolerance > getMaxSlideLength(getAngle()))
            slideMotor.setTargetPosition(getMaxSlideLength(getAngle()) - encoderTickTolerance);
        else if (slideMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION) slideMotor.setTargetPosition(slideMotor.getCurrentPosition());

        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(/* getAngle() < Math.toRadians(17.5) ? 0 : */ encoderPowerLevel);
    }
    public void retract(double power) {
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (slideMotor.getCurrentPosition() > encoderTickTolerance /* && getAngle() > Math.toRadians(17.5) */) slideMotor.setPower(-power);
        else neutral();
    }
    public void overrideRetract(double power) {
        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor.setPower(-power);
    }
    public double getSlideLength() { return retractedLength + ticksToInches(slideMotor.getCurrentPosition()); }
    public int getMaxSlideLength(double angle) { return inchesToTicks((maxDistance - retractedLength - staticLength) / Math.cos(angle)); }
    /*public void setSlideLength(double inches) {
        //int targetTicks = inchesToTicks(inches);
        //if (targetTicks + 50 > getMaxSlideLength(getAngle())) return; // this line might not be necessary TEST IF IT WORKS WITHOUT
        slideMotor.setTargetPosition(inchesToTicks(inches - RETRACTED_LENGTH));
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (slideMotor.isBusy() && slideMotor.getCurrentPosition() + ENCODER_TICK_TOLERANCE < getMaxSlideLength(getAngle())) slideMotor.setPower(ENCODER_POWER_LEVEL);
    }*/

    public void swivelUp(double power) {
        // supportingSlide.moveToTicks(Arm.encoderPowerLevel, SupportingSlide.calculateTicks(getAngle()));
        slideAngleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        if (slideAngleMotor.getCurrentPosition() + ENCODER_TICK_TOLERANCE < (int)(ANGLE_ENCODER_COUNTS_PER_REVOLUTION / 2)) slideAngleMotor.setPower(power); // old gear ratio
        if (slideAngleMotor.getCurrentPosition() + encoderTickTolerance < angleToTicks(Math.toRadians(90))) slideAngleMotor.setPower(power);
        else swivelNeutral();
    }
    public void overrideSwivelUp(double power) {
        slideAngleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideAngleMotor.setPower(power);
    }
    public void swivelNeutral() { swivelNeutral(slideAngleMotor.getCurrentPosition()); }
    public void swivelNeutral(int targetTicks) {
//      if (slideAngleMotor.getCurrentPosition() + ENCODER_TICK_TOLERANCE > (int)(ANGLE_ENCODER_COUNTS_PER_REVOLUTION / 2)) // old gear ratio
        if (slideAngleMotor.getCurrentPosition() + encoderTickTolerance > angleToTicks(Math.toRadians(90)))
            slideAngleMotor.setTargetPosition(angleToTicks(Math.toRadians(90)) - encoderTickTolerance);
        else if (slideAngleMotor.getCurrentPosition() < encoderTickTolerance) slideAngleMotor.setTargetPosition(encoderTickTolerance);
        else if (slideAngleMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION) slideAngleMotor.setTargetPosition(targetTicks);

        slideAngleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideAngleMotor.setPower(encoderPowerLevel);
    }
    public void swivelDown(double power) {
        // supportingSlide.moveToTicks(Arm.encoderPowerLevel, SupportingSlide.calculateTicks(getAngle()));
        slideAngleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (slideAngleMotor.getCurrentPosition() > encoderTickTolerance) slideAngleMotor.setPower(-power);
        else swivelNeutral();
    }
    public double getAngle() { return ticksToAngle(slideAngleMotor.getCurrentPosition()); }
    /*public void setAngle(double angle) { // angle MUST be in radians
        if (angle < 0 || Math.toDegrees(angle) > 80) return; // if angle is pi/2, cos(theta) = 0 and division by 0 is bad - therefore, limit it to a bit less than pi/2
        slideAngleMotor.setTargetPosition(angleToTicks(angle));
        slideAngleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (slideAngleMotor.isBusy()) slideAngleMotor.setPower(ENCODER_POWER_LEVEL);
    }*/
}