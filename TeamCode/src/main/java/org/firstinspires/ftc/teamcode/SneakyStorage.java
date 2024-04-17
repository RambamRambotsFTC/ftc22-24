package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;

import java.lang.*;

@TeleOp(name="Sneaky Storage", group="Data Store")
public class SneakyStorage extends LinearOpMode {
    Servo dataServo;

    ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        dataServo = hardwareMap.get(Servo.class, "dataServo");

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (gamepad1.dpad_left || gamepad2.dpad_left) {
                while (gamepad1.dpad_left || gamepad2.dpad_left);
                dataServo.setPosition((int)(dataServo.getPosition() * 10 - 1) / 10.0);
            }
            if (gamepad1.dpad_right || gamepad2.dpad_right) {
                while (gamepad1.dpad_right || gamepad2.dpad_right);
                dataServo.setPosition((int)(dataServo.getPosition() * 10 + 1) / 10.0);
                if (dataServo.getPosition() > 0.7) dataServo.setPosition(0.7);
            }

            DataServo data = new DataServo(dataServo);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Alliance", data.getAlliance() == DataServo.RED_ALLIANCE ? "Red" : "Blue");
            telemetry.addData("Stage", data.getStage() == DataServo.FRONT_STAGE ? "Front" : "Back");
            telemetry.addData("Backdrop", data.getBackdrop() == DataServo.BACKDROP ? "Yes" : "No");
            telemetry.addData("Raw", String.valueOf(data.getAlliance()) + data.getStage() + data.getBackdrop());
            telemetry.addData("Position", (int)(dataServo.getPosition() * 10 + 1) + "/8");
            telemetry.update();
        }
    }
}