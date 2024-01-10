package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Servo;

public class Drone {
    private final Servo droneServo;

    public Drone(Servo droneServo) { this.droneServo = droneServo; }

    public void launch() { droneServo.setPosition(0); }
    public void reset() { droneServo.setPosition(1); }
}