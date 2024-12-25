package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    private final Servo clawServo;

    public Claw(Servo clawServo) { this.clawServo = clawServo; }

    public void open() { clawServo.setPosition(1.0); }
    public void close() { clawServo.setPosition(0.0); }
}
