package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.*;

@Config
public class Arm {
    private final DcMotor slideMotor, slideAngleMotor;

    public static double ANGLE_GEAR_RATIO = 90.0 / 45.0, ANGLE_ENCODER_COUNTS_PER_REVOLUTION = 28 * Math.pow(1 + (46.0 / 17.0), 4); // ~5281.1
    public static double SLIDE_IN_PER_TICK = 29.4 / 3348; // ~0.00878 - found by measuring the difference between the retracted length and the extended length divided by the difference of ticks extended

    public static double MAX_DISTANCE = 35; // Measure in inches, starts at focal point of slide
    public static double RETRACTED_LENGTH = 16, STATIC_LENGTH = 2.5; // in.

    public static int ENCODER_TICK_TOLERANCE = 50;
    public static double ENCODER_POWER_LEVEL = 0.75;

    public Arm(DcMotor slideMotor, DcMotor slideAngleMotor) {
        this.slideMotor = slideMotor;
        this.slideAngleMotor = slideAngleMotor;

        this.slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slideAngleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.slideAngleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        this.slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.slideAngleMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.slideAngleMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void extend(double power) {
        double maxLengthAtAngle = (MAX_DISTANCE - RETRACTED_LENGTH - STATIC_LENGTH) / Math.cos(getAngle());
        if (slideMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        int maxTicksAtAngle = (int)(maxLengthAtAngle / SLIDE_IN_PER_TICK);
        if (slideMotor.getCurrentPosition() + ENCODER_TICK_TOLERANCE < maxTicksAtAngle) slideMotor.setPower(power);
        else neutral();
    }
    public void neutral() {
        if (slideMotor.getCurrentPosition() > getMaxSlideLength()) {
            slideMotor.setTargetPosition(getMaxSlideLength());
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slideMotor.setPower(ENCODER_POWER_LEVEL);
        } else slideMotor.setPower(0.0);
    }
    public void retract(double power) {
        if (slideMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (slideMotor.getCurrentPosition() > ENCODER_TICK_TOLERANCE) slideMotor.setPower(-power);
        else neutral();
    }
    public double getSlideLength() {
        int currentTicks = slideMotor.getCurrentPosition();
        double currentLengthInches = currentTicks * SLIDE_IN_PER_TICK;
        return RETRACTED_LENGTH + currentLengthInches;
    }
    public int getMaxSlideLength() {
        double maxLengthAtAngle = (MAX_DISTANCE - RETRACTED_LENGTH - STATIC_LENGTH) / Math.cos(getAngle());
        return (int)(maxLengthAtAngle / SLIDE_IN_PER_TICK);
    }

    public void swivelUp(double power) {
        if (slideAngleMotor.getCurrentPosition() + ENCODER_TICK_TOLERANCE > (int)(ANGLE_ENCODER_COUNTS_PER_REVOLUTION / 2)) return;
        if (slideAngleMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) slideAngleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideAngleMotor.setPower(power);
    }
    public void swivelNeutral() {
        if (slideAngleMotor.isBusy()) return;
        if (slideAngleMotor.getCurrentPosition() + ENCODER_TICK_TOLERANCE > (int)(ANGLE_ENCODER_COUNTS_PER_REVOLUTION / 2)) {
            slideAngleMotor.setTargetPosition((int)(ANGLE_ENCODER_COUNTS_PER_REVOLUTION / 2) - ENCODER_TICK_TOLERANCE);
            slideAngleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slideAngleMotor.setPower(ENCODER_POWER_LEVEL);
        } else slideAngleMotor.setPower(0.0);
    }
    public void swivelDown(double power) {
        if (slideAngleMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) slideAngleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (slideAngleMotor.getCurrentPosition() > ENCODER_TICK_TOLERANCE) slideAngleMotor.setPower(-power);
        else swivelNeutral();
    }
    public double getAngle() { return (2 * Math.PI * slideAngleMotor.getCurrentPosition()) / (ANGLE_ENCODER_COUNTS_PER_REVOLUTION * ANGLE_GEAR_RATIO); }
    public void setAngle(double angle) { // angle MUST be in radians
        if (angle < 0 || angle > Math.PI / 2.1) return; // if angle is pi/2, cos(theta) = 0 and division by 0 is bad - therefore, limit it to a bit less than pi/2
        slideAngleMotor.setTargetPosition((int)((angle / Math.PI) * ANGLE_ENCODER_COUNTS_PER_REVOLUTION));
        slideAngleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (slideAngleMotor.isBusy()) slideAngleMotor.setPower(ENCODER_POWER_LEVEL);
    }
}