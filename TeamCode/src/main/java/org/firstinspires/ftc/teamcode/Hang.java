package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Hang {
    private final DcMotor hangMotor1, hangMotor2;

    public Hang(DcMotor hangMotor1, DcMotor hangMotor2) {
        this.hangMotor1 = hangMotor1;
        this.hangMotor2 = hangMotor2;
    }

    public void spool(double power) {
        hangMotor1.setPower(power);
        hangMotor2.setPower(power);
    }
    public void neutral() {
        hangMotor1.setPower(0.0);
        hangMotor2.setPower(0.0);
    }
    public void unspool(double power) {
        hangMotor1.setPower(-power);
        hangMotor2.setPower(-power);
    }
}
