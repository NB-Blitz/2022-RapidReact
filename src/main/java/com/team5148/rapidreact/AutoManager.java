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
    public final double GOAL_ROTATE_SPEED = 0.3;

    // Output Data
    public double xInput = 0;
    public double yInput = 0;
    public double zInput = 0;
    public boolean isIntaking = false;
    public boolean isStoraging = false;
    public boolean isLaunching = false;

    // Sensors
    Timer timer = new Timer();
    AHRS navx = new AHRS(Port.kMXP);

    // Network Tables
    ShuffleboardTab autoTab = Shuffleboard.getTab("Autonomous");
    NetworkTableEntry posEntry = autoTab.add("Position", 10).getEntry();
    NetworkTableEntry stateEntry = autoTab.add("State", 0).getEntry();
    NetworkTableEntry timeentry = autoTab.add("Timer", 0).getEntry();
    NetworkTableEntry rotateEntry = autoTab.add("Gyro Angle", 0).getEntry();

    NetworkTable ballTable = NetworkTableInstance.getDefault().getTable("Ball Auto");
    NetworkTableEntry ballXEntry = ballTable.getEntry("X Value");
    NetworkTableEntry ballYEntry = ballTable.getEntry("Y Value");
    NetworkTableEntry ballAreaEntry = ballTable.getEntry("Area");

    NetworkTable goalTable = NetworkTableInstance.getDefault().getTable("Goal Auto");
    NetworkTableEntry goalXEntry = ballTable.getEntry("X Value");
    NetworkTableEntry goalYEntry = ballTable.getEntry("Y Value");
    NetworkTableEntry goalAreaEntry = ballTable.getEntry("Area");

    // State
    int autoState = 5;

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
        isLaunching = false;
        
    }

    public void processAutonomous() {
        int autoPosition = posEntry.getNumber(10).intValue();
        if (autoPosition == 2)
            processPosition2();
        if (autoPosition == 5)
            processPosition5();
        if(autoPosition == 10)
            trackBall();
    }

    public void nextState() {
        timer.reset();
        autoState++;

        xInput = 0;
        yInput = 0;
        zInput = 0;
        isIntaking = false;
        isStoraging = false;
        isLaunching = false;

        stateEntry.setNumber(autoState);
    }

    private void processPosition5() {
        double time = timer.get();

        if (autoState == 1) {
            yInput = 0.5;
            if (time > 1)
                nextState();
        } else if (autoState == 2) {
            isLaunching = true;
            if (time > 1)
                nextState();
        }
    }

    private void processPosition2() {
        double time = timer.get();
        double rotation = navx.getAngle();
        rotateEntry.setDouble(rotation);

        if (autoState == 1) { // Backwards
            yInput = .5;

            if (time > 0.7)
                nextState();
        } else if (autoState == 2) { // Launch
            isLaunching = true;
            isStoraging = true;

            if (time > 2)
                nextState();
        } else if (autoState == 3) { // Turn
            zInput = -.3;

            if (rotation > 150)
                nextState();
        } else if (autoState == 4)  { // Ball
            trackBall();

            yInput = -.3;
            isIntaking = true;

            if (time > 3)
                nextState();
        } else if (autoState == 5) { // Turn
            zInput = .3;

            if (rotation < 20)
                nextState();
        } else if (autoState == 6)  { // Goal
            trackGoal();

            yInput = -.3;

            if (time > 4)
                nextState();
        } else if (autoState == 7) { // Launch
            isLaunching = true;
            isStoraging = false;

            if (time > 3)
                nextState();
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
    }

    public void trackGoal() {
        /*
        double x = goalXEntry.getDouble(SCREEN_WIDTH / 2);
        double y = goalYEntry.getDouble(SCREEN_HEIGHT / 2);
        double newX = ((2*x)/SCREEN_WIDTH)-1;
        double newY = ((2*y)/SCREEN_HEIGHT)-1;

        xInput = 0;
        yInput = 0;
        zInput = newX * GOAL_ROTATE_SPEED;
        */

        trackBall();
    }

}
