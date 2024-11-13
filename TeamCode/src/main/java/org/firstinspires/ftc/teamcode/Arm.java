package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.*;

@Config
public class Arm {
    private final DcMotor slideMotor, slideAngleMotor;
    private final Servo clawServo;

    // Values found on https://www.gobilda.com/2mm-pitch-gt2-hub-mount-timing-belt-pulley-14mm-bore-60-tooth, https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-13-7-1-ratio-24mm-length-8mm-rex-shaft-435-rpm-3-3-5v-encoder
    public static double GEAR_RATIO = 13.7, WHEEL_CIRCUMFERENCE = (2 * Math.PI * 19.1) / 25.4; // Convert from mm to in.
    public static double getDistancePerRevolution() { return (Math.PI * WHEEL_CIRCUMFERENCE) / GEAR_RATIO; }
    public static double ENCODER_COUNTS_PER_REVOLUTION = 28 * Math.pow(1 + (46.0 / 17.0), 2); // ~384.5
    public static double MAX_DISTANCE = 30; // Measure in inches, starts at base of the arm
    public static double RETRACTED_LENGTH = 240 / 25.4; // Convert from mm to in.
    public static double getDistancePerCount() { return getDistancePerRevolution() / ENCODER_COUNTS_PER_REVOLUTION; }

    public Arm(DcMotor slideMotor, DcMotor slideAngleMotor, Servo clawServo) {
        this.slideMotor = slideMotor;
        this.slideAngleMotor = slideAngleMotor;
        this.clawServo = clawServo;

        this.slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.slideAngleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slideAngleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void extend(double power) {
        double maxLengthAtAngle = MAX_DISTANCE / Math.cos(getAngle()) + RETRACTED_LENGTH;
        int maxTicksAtAngle = (int)(maxLengthAtAngle / getDistancePerCount());
        if (slideMotor.getCurrentPosition() < maxTicksAtAngle) {
            if (slideMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            slideMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            slideMotor.setPower(power);
        } else {
            slideMotor.setTargetPosition(maxTicksAtAngle);
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }
    public void neutral() { slideMotor.setPower(0.0); }
    public void retract(double power) {
        if (slideMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        slideMotor.setPower(power);
    }
    public double getSlideLength() {
        int currentTicks = slideMotor.getCurrentPosition();
        double currentLengthInches = currentTicks * getDistancePerCount();
        return RETRACTED_LENGTH + currentLengthInches;
    }

    public void swivelUp(double power) {
        if (slideAngleMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) slideAngleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideAngleMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        slideAngleMotor.setPower(power);
    }
    public void swivelNeutral() { slideAngleMotor.setPower(0.0); }
    public void swivelDown(double power) {
        if (slideAngleMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) slideAngleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideAngleMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        slideAngleMotor.setPower(power);

        double maxLengthAtAngle = MAX_DISTANCE / Math.cos(getAngle()) + RETRACTED_LENGTH;
        int maxTicksAtAngle = (int)(maxLengthAtAngle / getDistancePerCount());
        if (slideMotor.getCurrentPosition() > maxTicksAtAngle) {
            slideMotor.setTargetPosition(maxTicksAtAngle);
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }
    public double getAngle() { return 2 * Math.PI * (double) this.slideAngleMotor.getCurrentPosition() / ENCODER_COUNTS_PER_REVOLUTION; }
    public void setAngle(double angle) { // angle MUST be in radians
        if (angle < 0 || angle > Math.PI / 6) return;
        slideAngleMotor.setTargetPosition((int)((angle / (2 * Math.PI)) * ENCODER_COUNTS_PER_REVOLUTION));
        slideAngleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (slideAngleMotor.isBusy()) {
            double maxLengthAtAngle = MAX_DISTANCE / Math.cos(getAngle()) + RETRACTED_LENGTH;
            int maxTicksAtAngle = (int)(maxLengthAtAngle / getDistancePerCount());
            if (slideMotor.getCurrentPosition() > maxTicksAtAngle) {
                slideMotor.setTargetPosition(maxTicksAtAngle);
                slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        }
        slideAngleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void open() {
        clawServo.setPosition(1.0);
    }
    public void close() {
        clawServo.setPosition(0.0);
    }
}