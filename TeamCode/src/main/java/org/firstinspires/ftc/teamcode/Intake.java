package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

public class Intake {
    private final DcMotor intakeMotor;

    public Intake(DcMotor intakeMotor) { this.intakeMotor = intakeMotor; }

    private boolean running = false;

    public void toggle() { running = !running; }
    public void runMotor() { intakeMotor.setPower(running ? 1 : 0); }
}