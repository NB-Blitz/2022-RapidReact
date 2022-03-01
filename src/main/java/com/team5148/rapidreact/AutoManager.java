package com.team5148.rapidreact;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class AutoManager {
    public final int SCREEN_WIDTH = 640;
    public final int SCREEN_HEIGHT = 480;

    public final double BALL_ROTATE_SPEED = 0.3;
    public final double GOAL_ROTATE_SPEED = 0.2;

    public final int ANGLE_TARGET_RANGE = 20;

    // Output Data
    public double xInput = 0;
    public double yInput = 0;
    public double zInput = 0;
    public boolean isIntaking = false;
    public boolean isStoraging = false;
    public boolean isFeeding = false;
    public boolean isLaunching = false;

    private int autoState = 1;

    // Sensors
    Timer timer = new Timer();
    AHRS navx = new AHRS(Port.kMXP);

    // Network Tables
    ShuffleboardTab autoTab = Shuffleboard.getTab("Autonomous");
    NetworkTableEntry posEntry = autoTab.add("Position", 2).getEntry();
    NetworkTableEntry stateEntry = autoTab.add("State", 0).getEntry();
    NetworkTableEntry timeentry = autoTab.add("Timer", 0).getEntry();
    NetworkTableEntry rotateEntry = autoTab.add("Gyro Angle", 0).getEntry();

    NetworkTable ballTable = NetworkTableInstance.getDefault().getTable("Ball Auto");
    NetworkTableEntry ballXEntry = ballTable.getEntry("X Value");
    NetworkTableEntry ballYEntry = ballTable.getEntry("Y Value");
    NetworkTableEntry ballAreaEntry = ballTable.getEntry("Area");

    NetworkTable goalTable = NetworkTableInstance.getDefault().getTable("Goal Auto");
    NetworkTableEntry goalXEntry = goalTable.getEntry("X Value");
    NetworkTableEntry goalYEntry = goalTable.getEntry("Y Value");
    NetworkTableEntry goalAreaEntry = goalTable.getEntry("Area");

    public void reset() {
        timer.reset();
        timer.start();
        navx.reset();
        autoState = 1;
        xInput = 0;
        yInput = 0;
        zInput = 0;
        isIntaking = false;
        isStoraging = false;
        isFeeding = false;
        isLaunching = false;
    }

    public void nextState() {
        timer.reset();
        autoState++;

        xInput = 0;
        yInput = 0;
        zInput = 0;
        isIntaking = false;
        isStoraging = false;
        isFeeding = false;
        isLaunching = false;

        stateEntry.setNumber(autoState);
    }

    public void processAutonomous() {
        int autoPosition = posEntry.getNumber(11).intValue();
        if (autoPosition == 2)
            processPosition2();
        if (autoPosition == 5)
            processPosition5();
        if(autoPosition == 10)
            trackBall();
        if(autoPosition == 11)
            trackBall();
    }

    private void processPosition5() {
        double time = timer.get();

        switch (autoState) {
            case 1:
                yInput = -0.5;
                if (time > 1)
                    nextState();
                break;
            case 2:
                isLaunching = true;
                if (time > 1)
                    nextState();
                break;
            case 3:
                isFeeding = true;
                isLaunching = true;
                if (time > 1)
                    nextState();
                break;
            case 4:
                zInput = 0.5;
                if (time > 1)
                    nextState();
                break;
        }
    }

    private void processPosition2() {
        double time = timer.get();

        switch (autoState) {
            case 1: // Ball
                trackBall();

                yInput = .2;
                isIntaking = true;

                if (time > 1.5)
                    nextState();
                break;
            case 2: // Turn
                if (rotateTo(180) || time > 2)
                    nextState();
                break;
            case 3: // Launch
                isLaunching = true;
                isStoraging = true;

                if (time > 2)
                    nextState();
                break;
            case 4: // Turn
                if (rotateTo(250) || time > 2)
                    nextState();
                break;
            case 5: // Ball
                trackBall();

                yInput = .3;
                isIntaking = true;

                if (time > 5)
                    nextState();
                break;
            case 6: // Turn

                if (rotateTo(100) || time > 2)
                    nextState();
                break;
            case 7: // Goal
                trackGoal();

                yInput = .5;

                if (time > 1.5)
                    nextState();
                break;
            case 8: // Launch
                isLaunching = true;
                isStoraging = false;

                if (time > 2)
                    nextState();
                break;
        }
    }

    public void trackBall() {
        /*
        double x = ballXEntry.getDouble(SCREEN_WIDTH / 2);
        double y = ballYEntry.getDouble(SCREEN_HEIGHT / 2);
        double newX = ((2*x)/SCREEN_WIDTH)-1;
        double newY = ((2*y)/SCREEN_HEIGHT)-1;

        xInput = 0;
        yInput = 0;
        zInput = newX * -BALL_ROTATE_SPEED;
        */

        xInput = 0;
        yInput = 0;
        zInput = 0;
    }

    public void trackGoal() {
        double x = goalXEntry.getDouble(SCREEN_WIDTH / 2);
        double y = goalYEntry.getDouble(SCREEN_HEIGHT / 2);
        double newX = ((2*x)/SCREEN_WIDTH)-1;
        double newY = ((2*y)/SCREEN_HEIGHT)-1;

        xInput = 0;
        yInput = 0;
        zInput = newX * -GOAL_ROTATE_SPEED;
    }

    public boolean rotateTo(double angle) {
        double currentAngle = navx.getAngle();
        double deltaAngle = angle - currentAngle;
        double power = deltaAngle / 180;

        rotateEntry.setDouble(currentAngle);
        zInput = power;

        return Math.abs(deltaAngle) < ANGLE_TARGET_RANGE;
    }

}
