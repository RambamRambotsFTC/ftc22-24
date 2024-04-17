package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Servo;

public class DataServo {
    public static final int RED_ALLIANCE = 0, BLUE_ALLIANCE = 1, FRONT_STAGE = 0, BACK_STAGE = 1, NO_BACKDROP = 0, BACKDROP = 1;

    private int alliance, stage, backdrop;

    public DataServo(Servo dataServo) {
        switch ((int)(dataServo.getPosition() * 10 + 0.5)) {
            case 0:
                alliance = RED_ALLIANCE;
                stage = FRONT_STAGE;
                backdrop = BACKDROP;
                break;
            case 1:
                alliance = BLUE_ALLIANCE;
                stage = FRONT_STAGE;
                backdrop = BACKDROP;
                break;
            case 2:
                alliance = RED_ALLIANCE;
                stage = BACK_STAGE;
                backdrop = BACKDROP;
                break;
            case 3:
                alliance = BLUE_ALLIANCE;
                stage = BACK_STAGE;
                backdrop = BACKDROP;
                break;
            case 4:
                alliance = RED_ALLIANCE;
                stage = FRONT_STAGE;
                backdrop = NO_BACKDROP;
                break;
            case 5:
                alliance = BLUE_ALLIANCE;
                stage = FRONT_STAGE;
                backdrop = NO_BACKDROP;
                break;
            case 6:
                alliance = RED_ALLIANCE;
                stage = BACK_STAGE;
                backdrop = NO_BACKDROP;
                break;
            case 7:
                alliance = BLUE_ALLIANCE;
                stage = BACK_STAGE;
                backdrop = NO_BACKDROP;
                break;
        }
    }

    public int getAlliance() { return alliance; }
    public int getStage() { return stage; }
    public int getBackdrop() { return backdrop; }
}