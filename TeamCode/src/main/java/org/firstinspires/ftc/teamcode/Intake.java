package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

public class Intake {
    private final DcMotor intakeMotor1, intakeMotor2;

    public Intake(DcMotor intakeMotor1, DcMotor intakeMotor2) {
        this.intakeMotor1 = intakeMotor1;
        this.intakeMotor2 = intakeMotor2;
    }

    private boolean running = false, reverse = false;

    public void toggle() { running = !running; }
    public void reverse() { reverse = !reverse; }
    public void runMotor() {
        intakeMotor1.setDirection(reverse ? Direction.REVERSE : Direction.FORWARD);
        intakeMotor2.setDirection(reverse ? Direction.REVERSE : Direction.FORWARD);
        intakeMotor1.setPower(running ? 1 : 0);
        intakeMotor2.setPower(running ? 1 : 0);
    }
}