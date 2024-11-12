package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

@Config
class AutonomousConfig {
    public static Zone zone = Zone.OBSERVATION_ZONE;
    public static Park park = Park.NO_PARK;

    public enum Zone { OBSERVATION_ZONE, NET_ZONE }
    public enum Park { NO_PARK, PARK, ASCENT }

    public Zone getZone() { return zone; }
    public Park getPark() { return park; }
}

@Autonomous(name="Autonomous", group="Autonomous")
public class AutoOpMode extends LinearOpMode {
    MecanumWheels drive;
    Arm arm;

    DcMotor leftFront, leftBack, rightBack, rightFront, slideMotor, slideAngleMotor;
    Servo clawServo;

    AutonomousConfig config = new AutonomousConfig();

    @Override
    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        slideMotor = hardwareMap.get(DcMotor.class, "slideMotor");
        slideAngleMotor = hardwareMap.get(DcMotor.class, "slideAngleMotor");

        drive = new MecanumWheels(leftFront, leftBack, rightBack, rightFront);
        arm = new Arm(slideMotor, slideAngleMotor, clawServo);

        while (opModeInInit()) {
            telemetry.addData("Status", "Initialized");
            telemetry.addLine();
            telemetry.addLine("Change these variables in the FTC Dashboard");
            telemetry.addData("Zone", config.getZone());
            telemetry.addData("Park", config.getPark());
            telemetry.update();
        }

        AutonomousConfig.Zone zone = config.getZone();
        AutonomousConfig.Park park = config.getPark();

        while (opModeIsActive()) {
            telemetry.addData("Status", "Running Autonomous");
            telemetry.addLine();
            telemetry.addLine("Configuration variables are now locked. Put autonomous back in INIT to change them.");
            telemetry.addData("Zone", zone);
            telemetry.addData("Park", park);
            telemetry.update();
        }
    }
}