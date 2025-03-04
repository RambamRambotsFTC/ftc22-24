// https://rr.brott.dev/docs/v1-0/guides/centerstage-auto

package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

import java.util.Locale;

@Config
class AutonomousConfig {
    public static Zone zone = Zone.NET_ZONE;
    public enum Zone { OBSERVATION_ZONE, NET_ZONE }
    public static Zone getZone() { return zone; }

    // All coordinates are in inches, using the RoadRunner grid with X +72 ↓ -72 and Y +72 → -72
    // https://rr.brott.dev/docs/v1-0/builder-ref
    public static class Coordinates {
        public Point start = new Point(12, 63, 90),
                chamber = new Point(12, 52, start.heading),
                basket = new Point(52, 50, 35),
                sweep = new Point(36, 12, start.heading),
                park = new Point(40, 60, start.heading),
                ascent = new Point(34, 12, 180);
        public Vector2d sample1 = new Vector2d(48, 26), sample2 = new Vector2d(61, 26), sample3 = new Vector2d(73, 26);

        public Pose2d getStartPose() { return new Pose2d(start.x * (AutonomousConfig.getZone() == Zone.NET_ZONE ? 1 : -1), start.y, -Math.toRadians(start.heading)); }
        public Pose2d getChamberPose() { return new Pose2d(chamber.x * (AutonomousConfig.getZone() == Zone.NET_ZONE ? 1 : -1), chamber.y, -Math.toRadians(chamber.heading)); }
        public Pose2d getBasketPose() { return new Pose2d(basket.x, basket.y, -Math.toRadians(basket.heading)); } // only in net zone
        public Pose2d getAscentPose() { return new Pose2d(ascent.x, ascent.y, -Math.toRadians(ascent.heading)); } // only in net zone
        public Pose2d getSweepPose() { return new Pose2d(-sweep.x, sweep.y, -Math.toRadians(sweep.heading)); } // only in observation zone
        public Pose2d getParkPose() { return new Pose2d(-park.x, park.y, -Math.toRadians(park.heading)); } // only in observation zone
    }
    public static Coordinates COORDINATES = new Coordinates();
}

@Autonomous(name="Autonomous", group="Autonomous", preselectTeleOp="Driver Control")
public class AutoOpMode extends LinearOpMode {
    public static class RRDrive {
        private final MecanumDrive drive;

        public RRDrive(HardwareMap hardwareMap, Pose2d initialPose) { drive = new MecanumDrive(hardwareMap, initialPose); }
        public RRDrive(HardwareMap hardwareMap) { this(hardwareMap, new Pose2d(0, 0, 0)); }

        public void setDrivePowers(Gamepad gamepad, double multiplier) {
            drive.setDrivePowers(new PoseVelocity2d(new Vector2d(-gamepad.left_stick_y * multiplier, -gamepad.left_stick_x * multiplier), -gamepad.right_stick_x * multiplier));
            drive.updatePoseEstimate();
        }

        public Action driveToPose(Pose2d pose) { return startAction().strafeToLinearHeading(pose.position, pose.heading).build(); }
        public Action turnToHeading(double heading) { return startAction().turnTo(heading).build(); }
        public Action turnToHeading(Rotation2d heading) { return startAction().turnTo(heading).build(); }
        public Action turnInPlace(double angle) { return startAction().turn(angle).build(); }

        public TrajectoryActionBuilder startAction() {
            drive.updatePoseEstimate();
            return drive.actionBuilder(drive.pose);
        }
    }

    public static class RRArm {
        private final DcMotor slideMotor, slideAngleMotor;
        private final DigitalChannel limitSwitch;
        // private final SupportingSlide supportingSlide;

        public RRArm(@NonNull HardwareMap hardwareMap) {
            slideMotor = hardwareMap.get(DcMotor.class, "slideMotor");
            slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideAngleMotor = hardwareMap.get(DcMotor.class, "slideAngleMotor");
            slideAngleMotor.setDirection(DcMotor.Direction.REVERSE);
            limitSwitch = hardwareMap.get(DigitalChannel.class, "limitSwitch");
            // supportingSlide = new SupportingSlide(hardwareMap.get(DcMotor.class, "secondarySlideMotor"));
        }

        public class ZeroAngleMotor implements Action {
            private boolean initialized = false;
            private final double power;

            public ZeroAngleMotor(double power) { this.power = power; }

            public boolean run(@NonNull TelemetryPacket packet) {
                boolean isZero = limitSwitch.getState();
                packet.put("Limit Switch Pressed?", isZero);
                if (!isZero) {
                    if (!initialized) {
                        slideAngleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        slideAngleMotor.setPower(-power);
                        initialized = true;
                    }
                    return true;
                } else {
                    slideAngleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    slideAngleMotor.setPower(0);
                    // supportingSlide.setZero();
                    return false;
                }
            }
        }
        public class BounceOffLimitSwitch implements Action {
            private boolean initialized = false;

            public boolean run(@NonNull TelemetryPacket packet) {
                boolean isZero = limitSwitch.getState();
                packet.put("Limit Switch Pressed?", isZero);
                if (isZero) {
                    if (!initialized) {
                        slideAngleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        slideAngleMotor.setPower(0.2);
                        initialized = true;
                    }
                    return true;
                } else {
                    slideAngleMotor.setPower(0);
                    return false;
                }
            }
        }
        public SequentialAction zeroAngleMotor() {
            return new SequentialAction(
                    new ZeroAngleMotor(0.2), new SleepAction(0.5),
                    new BounceOffLimitSwitch(),
                    new ZeroAngleMotor(0.05)
            );
        }

        public class FullyRetract implements Action {
            private boolean initialized = false;

            public boolean run(@NonNull TelemetryPacket packet) {
                double pos = Arm.retractedLength + Arm.ticksToInches(slideMotor.getCurrentPosition());
                packet.put("Slide Length", pos);
                if (pos > 0) {
                    if (!initialized) {
                        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        slideMotor.setPower(-0.25);
                        initialized = true;
                    }
                    return true;
                } else {
                    slideMotor.setPower(0);
                    return false;
                }
            }
        }
        public Action fullyRetract() { return new FullyRetract(); }

        public class Extend implements Action {
            private boolean initialized = false;
            private final double power, targetInches;

            public Extend(double power, double targetInches) {
                this.power = power;
                this.targetInches = targetInches;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                double pos = Arm.retractedLength + Arm.ticksToInches(slideMotor.getCurrentPosition());
                packet.put("Slide Length", pos);
                if (pos < targetInches) {
                    if (!initialized) {
                        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        slideMotor.setPower(power);
                        initialized = true;
                    }
                    return true;
                } else {
                    slideMotor.setTargetPosition(Arm.inchesToTicks(targetInches - Arm.retractedLength));
                    slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    return false;
                }
            }
        }
        public Action extend(double power, double targetInches) { return new Extend(power, targetInches); }
        public Action extend(double power, int targetTicks) { return extend(power, Arm.retractedLength + Arm.ticksToInches(targetTicks)); }

        public class Retract implements Action {
            private boolean initialized = false;
            private final double power, targetInches;

            public Retract(double power, double targetInches) {
                this.power = power;
                this.targetInches = targetInches;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                double pos = Arm.retractedLength + Arm.ticksToInches(slideMotor.getCurrentPosition());
                packet.put("Slide Length", pos);
                if (pos > targetInches) {
                    if (!initialized) {
                        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        slideMotor.setPower(-power);
                        initialized = true;
                    }
                    return true;
                } else {
                    slideMotor.setTargetPosition(Arm.inchesToTicks(targetInches - Arm.retractedLength));
                    slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    return false;
                }
            }
        }
        public Action retract(double power, double targetLength) { return new Retract(power, targetLength); }
        public Action retract(double power, int targetTicks) { return retract(power, Arm.retractedLength + Arm.ticksToInches(targetTicks)); }

        public class SwivelUp implements Action {
            private boolean initialized = false;
            private final double power, targetAngle;

            public SwivelUp(double power, double targetAngle) {
                this.power = power;
                this.targetAngle = Math.min(targetAngle, Math.toRadians(90));
            }

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                double angle = Arm.ticksToAngle(slideAngleMotor.getCurrentPosition());
                packet.put("Arm Angle", String.format(Locale.US, "%.2f rad (%.2f°)", angle, Math.toDegrees(angle)));
                if (angle < targetAngle) {
                    if (!initialized) {
                        slideAngleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        slideAngleMotor.setPower(power);
                        // supportingSlide.moveToTicks(power, SupportingSlide.calculateTicks(angle));
                        initialized = true;
                    }
                    return true;
                } else {
                    slideAngleMotor.setTargetPosition(Arm.angleToTicks(targetAngle));
                    slideAngleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    return false;
                }
            }
        }
        public Action swivelUp(double power, double targetAngle) { return new SwivelUp(power, targetAngle); }
        public Action swivelUp(double power, int targetTicks) { return swivelUp(power, Arm.ticksToAngle(targetTicks)); }

        public class SwivelDown implements Action {
            private boolean initialized = false;
            private final double power, targetAngle;

            public SwivelDown(double power, double targetAngle) {
                this.power = power;
                this.targetAngle = Math.max(targetAngle, Math.toRadians(5));
            }

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                double angle = Arm.ticksToAngle(slideAngleMotor.getCurrentPosition());
                packet.put("Arm Angle", String.format(Locale.US, "%.2f rad (%.2f°)", angle, Math.toDegrees(angle)));
                if (angle > targetAngle) {
                    if (!initialized) {
                        slideAngleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                        slideAngleMotor.setPower(-power);
                        // supportingSlide.moveToTicks(power, SupportingSlide.calculateTicks(angle));
                        initialized = true;
                    }
                    return true;
                } else {
                    slideAngleMotor.setTargetPosition(Arm.angleToTicks(targetAngle));
                    slideAngleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    return false;
                }
            }
        }
        public Action swivelDown(double power, double targetAngle) { return new SwivelDown(power, targetAngle); }
        public Action swivelDown(double power, int targetTicks) { return swivelDown(power, Arm.ticksToAngle(targetTicks)); }
    }

    public static class RRClaw {
        final Claw claw;

        public RRClaw(@NonNull HardwareMap hardwareMap) {
            Servo clawServo = hardwareMap.get(Servo.class, "clawServo");
            claw = new Claw(clawServo);
        }

        public InstantAction open() { return new InstantAction(claw::open); }
        public InstantAction close() { return new InstantAction(claw::close); }
    }

    public SequentialAction grabNetSample(RRArm arm, RRClaw claw) {
        return new SequentialAction(
                arm.swivelDown(Arm.encoderPowerLevel, Math.toRadians(15)), new SleepAction(0.25),
                claw.close(), new SleepAction(0.5), arm.swivelUp(Arm.encoderPowerLevel, Math.toRadians(30)),
                arm.retract(Arm.encoderPowerLevel, 19.0)
        );
    }
    public SequentialAction grabObservationSpecimen(RRArm arm, RRClaw claw) {
        return new SequentialAction(
                arm.extend(0.25, 18.0),
                arm.swivelDown(0.1, Math.toRadians(10)), new SleepAction(0.25),
                claw.close(), new SleepAction(0.5), arm.swivelUp(Arm.encoderPowerLevel, Math.toRadians(30))
        );
    }
    /* public SequentialAction sweepSample(RRDrive drive, RRArm arm, RRClaw claw, double extendLength) {
        return new SequentialAction(
                arm.swivelDown(Arm.encoderPowerLevel, Math.toRadians(15)), new SleepAction(0.25),
                claw.close(), new SleepAction(0.5), drive.turnInPlace(Math.toRadians(-90)),
                arm.extend(Arm.encoderPowerLevel, extendLength),
                claw.open(), arm.swivelUp(Arm.encoderPowerLevel, Math.toRadians(30)), new SleepAction(0.5)
        );
    } */
    public SequentialAction scoreInBucket(RRArm arm, RRClaw claw) {
        double netZoneBasketExtendDistance = Math.sqrt(Math.pow(72 - AutonomousConfig.COORDINATES.basket.x - 6, 2) + Math.pow(72 - AutonomousConfig.COORDINATES.basket.y - 6, 2)),
                netZoneBasketExtendHypotenuse = Math.sqrt(Math.pow(47, 2) + Math.pow(netZoneBasketExtendDistance, 2)),
                netZoneBasketAngle = Math.asin(50 / (netZoneBasketExtendHypotenuse - 1));
        return new SequentialAction(
                new ParallelAction(arm.swivelUp(Arm.encoderPowerLevel, netZoneBasketAngle), arm.extend(Arm.encoderPowerLevel, netZoneBasketExtendHypotenuse * (4.0 / 5.0))),
                arm.extend(Arm.encoderPowerLevel, netZoneBasketExtendHypotenuse), new SleepAction(0.1), arm.swivelDown(0.1, netZoneBasketAngle - Math.toRadians(5)),
                claw.open(), new SleepAction(0.1), arm.swivelUp(Arm.encoderPowerLevel, netZoneBasketAngle + Math.toRadians(10)),
                new SleepAction(0.1), arm.retract(Arm.encoderPowerLevel, netZoneBasketExtendHypotenuse / 2),
                new ParallelAction(arm.retract(Arm.encoderPowerLevel, 19.0), arm.swivelDown(Arm.encoderPowerLevel, Math.toRadians(40)))
        );
    }
    public SequentialAction scoreOnChamber(RRArm arm, RRClaw claw) {
        double submersibleChamberExtendDistance = AutonomousConfig.COORDINATES.chamber.y - 24,
                submersibleChamberExtendHypotenuse = Math.sqrt(Math.pow(37 - 1, 2) + Math.pow(submersibleChamberExtendDistance, 2)) - 4, // subtract 1 for the length of the clip after the arm itself ends; subtract 4 to account the offset from the center of the robot
                submersibleChamberAngle = Math.asin(38 / submersibleChamberExtendHypotenuse);

        return new SequentialAction(
                new ParallelAction(
                        arm.swivelUp(Arm.encoderPowerLevel, submersibleChamberAngle),
                        arm.extend(Arm.encoderPowerLevel, submersibleChamberExtendHypotenuse)
                ),
                arm.swivelDown(Arm.encoderPowerLevel, submersibleChamberAngle - Math.toRadians(15)), new SleepAction(0.25),
                arm.retract(Arm.encoderPowerLevel, submersibleChamberExtendHypotenuse - 5),
                claw.open(), new SleepAction(0.1),
                arm.retract(Arm.encoderPowerLevel, submersibleChamberExtendHypotenuse * (4.0 / 5.0)),
                new ParallelAction(
                        arm.retract(Arm.encoderPowerLevel, 19.0),
                        arm.swivelDown(Arm.encoderPowerLevel, Math.toRadians(30))
                )
        );
    }
    public SequentialAction ascendToLowRung(RRArm arm) {
        double ascensionRungExtendDistance = AutonomousConfig.COORDINATES.ascent.x - 12,
                ascensionRungExtendHypotenuse = Math.sqrt(Math.pow(22, 2) + Math.pow(ascensionRungExtendDistance, 2)),
                ascensionRungAngle = Math.asin(22 / ascensionRungExtendHypotenuse);

        return new SequentialAction(
                new ParallelAction(
                        arm.swivelUp(Arm.encoderPowerLevel, ascensionRungAngle),
                        arm.extend(Arm.encoderPowerLevel, ascensionRungExtendHypotenuse)
                ),
                arm.swivelDown(0.1, ascensionRungAngle - Math.toRadians(10))
        );
    }

    @Override
    public void runOpMode() {
        RRDrive drive;
        RRArm arm = new RRArm(hardwareMap);
        RRClaw claw = new RRClaw(hardwareMap);

        Actions.runBlocking(new SequentialAction(
                arm.zeroAngleMotor(),
                arm.swivelUp(0.5, Math.toRadians(55)), arm.extend(0.1, Arm.encoderTickTolerance),
                new SleepAction(3), claw.close()
        ));

        while (opModeInInit()) {
            telemetry.addData("Status", "Initialized");
            telemetry.addLine();
            telemetry.addLine("If the slide is not yet fully retracted, do so now and then restart Autonomous.");
            telemetry.addLine();
            telemetry.addLine("Change the zone variable in the FTC Dashboard.");
            telemetry.addData("Zone", AutonomousConfig.getZone());
            telemetry.update();
        }

        if (isStopRequested()) return;

        drive = new RRDrive(hardwareMap, AutonomousConfig.COORDINATES.getStartPose());

        Actions.runBlocking(new ParallelAction(drive.driveToPose(AutonomousConfig.COORDINATES.getChamberPose()), new SequentialAction(new SleepAction(0.5), scoreOnChamber(arm, claw))));
        switch (AutonomousConfig.getZone()) {
            case NET_ZONE:
                // rip sample 3, not enough time :'(
                double[] sampleDistances = { // +5 to account for offset to the sample; +6 to account for length of slide behind the center of the robot
                        Math.sqrt(Math.pow(AutonomousConfig.COORDINATES.basket.x - (AutonomousConfig.COORDINATES.sample1.x + 5), 2) + Math.pow(AutonomousConfig.COORDINATES.basket.y - AutonomousConfig.COORDINATES.sample1.y, 2)) + 6,
                        Math.sqrt(Math.pow(AutonomousConfig.COORDINATES.basket.x - (AutonomousConfig.COORDINATES.sample2.x + 5), 2) + Math.pow(AutonomousConfig.COORDINATES.basket.y - AutonomousConfig.COORDINATES.sample2.y, 2)) + 6,
                        Math.sqrt(Math.pow(AutonomousConfig.COORDINATES.basket.x - (AutonomousConfig.COORDINATES.sample3.x + 5), 2) + Math.pow(AutonomousConfig.COORDINATES.basket.y - AutonomousConfig.COORDINATES.sample3.y, 2)) + 6
                };
                double[] sampleAngles = { // in rad
                        Math.atan((AutonomousConfig.COORDINATES.basket.x - (AutonomousConfig.COORDINATES.sample1.x + 5)) / (AutonomousConfig.COORDINATES.basket.y - AutonomousConfig.COORDINATES.sample1.y)),
                        Math.atan((AutonomousConfig.COORDINATES.basket.x - (AutonomousConfig.COORDINATES.sample2.x + 5)) / (AutonomousConfig.COORDINATES.basket.y - AutonomousConfig.COORDINATES.sample2.y)),
                        Math.atan((AutonomousConfig.COORDINATES.basket.x - (AutonomousConfig.COORDINATES.sample3.x + 5)) / (AutonomousConfig.COORDINATES.basket.y - AutonomousConfig.COORDINATES.sample3.y))
                };

                Actions.runBlocking(new SequentialAction(
                        new ParallelAction(
                                drive.driveToPose(new Pose2d(AutonomousConfig.COORDINATES.getBasketPose().position, AutonomousConfig.COORDINATES.getStartPose().heading.plus(-sampleAngles[0]))),
                                arm.extend(Arm.encoderPowerLevel, sampleDistances[0] * (2.0 / 3.0))
                        ),
                        new ParallelAction(arm.swivelDown(Arm.encoderPowerLevel, Math.toRadians(25)), arm.extend(Arm.encoderPowerLevel, sampleDistances[0])),
                        grabNetSample(arm, claw)
                ));
                Actions.runBlocking(new ParallelAction(
                        drive.turnToHeading(Math.toRadians(AutonomousConfig.COORDINATES.basket.heading * Math.signum(AutonomousConfig.COORDINATES.getBasketPose().position.x))),
                        scoreInBucket(arm, claw)
                ));
                Actions.runBlocking(new SequentialAction(
                        new ParallelAction(
                                drive.turnToHeading(AutonomousConfig.COORDINATES.getStartPose().heading.plus(-sampleAngles[1])),
                                arm.extend(Arm.encoderPowerLevel, sampleDistances[1] * (2.0 / 3.0))
                        ),
                        new ParallelAction(arm.swivelDown(Arm.encoderPowerLevel, Math.toRadians(25)), arm.extend(Arm.encoderPowerLevel, sampleDistances[1])),
                        grabNetSample(arm, claw)
                ));
                Actions.runBlocking(new ParallelAction(
                        drive.turnToHeading(Math.toRadians(AutonomousConfig.COORDINATES.basket.heading * Math.signum(AutonomousConfig.COORDINATES.getBasketPose().position.x))),
                        scoreInBucket(arm, claw)
                ));

                Actions.runBlocking(new SequentialAction(
                        drive.startAction().splineTo(AutonomousConfig.COORDINATES.getAscentPose().position, AutonomousConfig.COORDINATES.getAscentPose().heading).build(),
                        ascendToLowRung(arm)
                ));

                break;
            case OBSERVATION_ZONE:
                /* sampleDistances = new double[] { // +6.5 to account for length of slide behind the center of the robot
                        Math.sqrt(Math.pow(AutonomousConfig.COORDINATES.sweep.x - AutonomousConfig.COORDINATES.sample1.x, 2) + Math.pow(AutonomousConfig.COORDINATES.sweep.y - AutonomousConfig.COORDINATES.sample1.y, 2)) + 6.5,
                        Math.sqrt(Math.pow(AutonomousConfig.COORDINATES.sweep.x - AutonomousConfig.COORDINATES.sample2.x, 2) + Math.pow(AutonomousConfig.COORDINATES.sweep.y - AutonomousConfig.COORDINATES.sample2.y, 2)) + 6.5,
                        Math.sqrt(Math.pow(AutonomousConfig.COORDINATES.sweep.x - AutonomousConfig.COORDINATES.sample3.x, 2) + Math.pow(AutonomousConfig.COORDINATES.sweep.y - AutonomousConfig.COORDINATES.sample3.y, 2)) + 6.5
                };
                sampleAngles = new double[] { // in rad
                        Math.atan((AutonomousConfig.COORDINATES.sweep.x - AutonomousConfig.COORDINATES.sample1.x) / (AutonomousConfig.COORDINATES.sweep.y - AutonomousConfig.COORDINATES.sample1.y)),
                        Math.atan((AutonomousConfig.COORDINATES.sweep.x - AutonomousConfig.COORDINATES.sample2.x) / (AutonomousConfig.COORDINATES.sweep.y - AutonomousConfig.COORDINATES.sample2.y)),
                        Math.atan((AutonomousConfig.COORDINATES.sweep.x - AutonomousConfig.COORDINATES.sample3.x) / (AutonomousConfig.COORDINATES.sweep.y - AutonomousConfig.COORDINATES.sample3.y))
                };

                Actions.runBlocking(new ParallelAction(
                        drive.driveToPose(new Pose2d(AutonomousConfig.COORDINATES.getSweepPose().position, AutonomousConfig.COORDINATES.getStartPose().heading.plus(sampleAngles[0]))),
                        arm.extend(Arm.encoderPowerLevel, sampleDistances[0])
                ));
                Actions.runBlocking(sweepSample(drive, arm, claw, sampleDistances[0] + 4));
                Actions.runBlocking(new ParallelAction(
                        drive.turnToHeading(AutonomousConfig.COORDINATES.getStartPose().heading.plus(sampleAngles[1])),
                        arm.extend(Arm.encoderPowerLevel, sampleDistances[1])
                ));
                Actions.runBlocking(sweepSample(drive, arm, claw, sampleDistances[1]));
                Actions.runBlocking(new ParallelAction(
                        drive.turnToHeading(AutonomousConfig.COORDINATES.getStartPose().heading.plus(sampleAngles[2])),
                        arm.extend(Arm.encoderPowerLevel, sampleDistances[2])
                ));
                Actions.runBlocking(sweepSample(drive, arm, claw, sampleDistances[2]));

                Actions.runBlocking(new SequentialAction(
                        drive.driveToPose(new Pose2d(new Vector2d(-20, 58), 180)),
                        grabObservationSpecimen(arm, claw),
                        drive.driveToPose(new Pose2d(new Vector2d(AutonomousConfig.COORDINATES.getChamberPose().position.x * 2, AutonomousConfig.COORDINATES.getChamberPose().position.y), AutonomousConfig.COORDINATES.getChamberPose().heading)),
                        scoreOnChamber(arm, claw),
                        new ParallelAction(
                                drive.driveToPose(AutonomousConfig.COORDINATES.getParkPose()),
                                arm.swivelDown(Arm.encoderPowerLevel, Math.toRadians(20)),
                                arm.fullyRetract()
                        )
                )); */

                Actions.runBlocking(drive.startAction()
//                        .setReversed(true).splineToSplineHeading(AutonomousConfig.COORDINATES.getSweepPose(), AutonomousConfig.COORDINATES.getSweepPose().heading.plus(-180)).setReversed(false)

                        .strafeTo(new Vector2d(-40, 36)).strafeTo(new Vector2d(-40, AutonomousConfig.COORDINATES.sweep.y))

                        .strafeTo(new Vector2d(2 - AutonomousConfig.COORDINATES.sample1.x, AutonomousConfig.COORDINATES.sweep.y))
                        .strafeTo(new Vector2d(2 - AutonomousConfig.COORDINATES.sample1.x, AutonomousConfig.COORDINATES.basket.y))
                        .strafeToLinearHeading(new Vector2d(2 - AutonomousConfig.COORDINATES.sample1.x, AutonomousConfig.COORDINATES.sweep.y), AutonomousConfig.COORDINATES.getSweepPose().heading.plus(Math.toRadians(90)))

                        .strafeTo(new Vector2d(-AutonomousConfig.COORDINATES.sample2.x - 1, AutonomousConfig.COORDINATES.sweep.y))
                        .strafeTo(new Vector2d(3 - AutonomousConfig.COORDINATES.sample3.x, 60))

                        /* .waitSeconds(1).strafeToLinearHeading(new Vector2d(-30, 58), Math.toRadians(180))

                        .strafeTo(new Vector2d(3 - AutonomousConfig.COORDINATES.sample2.x, AutonomousConfig.COORDINATES.sweep.y))
                        .strafeTo(new Vector2d(3 - AutonomousConfig.COORDINATES.sample2.x, AutonomousConfig.COORDINATES.basket.y))
                        .strafeToLinearHeading(new Vector2d(3 - AutonomousConfig.COORDINATES.sample2.x, AutonomousConfig.COORDINATES.sweep.y), AutonomousConfig.COORDINATES.getSweepPose().heading.plus(Math.toRadians(90)))

                        .strafeTo(new Vector2d(3.5 - AutonomousConfig.COORDINATES.sample3.x, AutonomousConfig.COORDINATES.sweep.y))
                        .strafeTo(new Vector2d(3.5 - AutonomousConfig.COORDINATES.sample3.x, AutonomousConfig.COORDINATES.basket.y))
                        .strafeTo(new Vector2d(3.5 - AutonomousConfig.COORDINATES.sample3.x, 60)) */

                        .build());

                Actions.runBlocking(new SequentialAction(
                        grabObservationSpecimen(arm, claw),
                        drive.startAction()
                                .strafeTo(new Vector2d(AutonomousConfig.COORDINATES.getChamberPose().position.x * 2, AutonomousConfig.COORDINATES.getChamberPose().position.y))
                                .turnTo(AutonomousConfig.COORDINATES.getChamberPose().heading)
                                .build(),
                        // drive.driveToPose(new Pose2d(new Vector2d(AutonomousConfig.COORDINATES.getChamberPose().position.x * 2, AutonomousConfig.COORDINATES.getChamberPose().position.y), AutonomousConfig.COORDINATES.getChamberPose().heading)),
                        scoreOnChamber(arm, claw)
                ));
                Actions.runBlocking(new ParallelAction(
                        drive.driveToPose(AutonomousConfig.COORDINATES.getParkPose()),
                        arm.swivelDown(Arm.encoderPowerLevel, Math.toRadians(20)),
                        arm.fullyRetract()
                ));

                break;
        }
    }
}