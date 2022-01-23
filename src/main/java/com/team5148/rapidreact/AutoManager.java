package com.team5148.rapidreact;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoManager {

    public final int SCREEN_WIDTH = 640;
    public final int SCREEN_HEIGHT = 480;

    // Output Data
    public double leftSpeed = 0;
    public double rightSpeed = 0;
    public boolean isIntaking = false;
    public boolean isStoraging = false;
    public boolean isLaunching = false;

    // Sensors
    Timer timer = new Timer();
    AHRS navx = new AHRS(Port.kMXP);

    // Network Tables
    ShuffleboardTab autoTab = Shuffleboard.getTab("Autonomous");
    NetworkTableEntry stateEntry = autoTab.add("Autonomous State", 0).getEntry();
    NetworkTableEntry timeentry = autoTab.add("Autonomous Timer", 0).getEntry();
    NetworkTableEntry rotateEntry = autoTab.add("Gyro Angle", 0).getEntry();

    NetworkTable ballTable = NetworkTableInstance.getDefault().getTable("Ball Auto");
    NetworkTableEntry ballXEntry = ballTable.getEntry("X Value");
    NetworkTableEntry ballYEntry = ballTable.getEntry("Y Value");
    NetworkTableEntry ballAreaEntry = ballTable.getEntry("Area");

    // State
    int autoState = 1;
    int autoPosition = 2;

    public void reset() {
        timer.reset();
        timer.start();
        navx.reset();
        autoState = 1;
        leftSpeed = 0;
        rightSpeed = 0;
        isIntaking = false;
        isStoraging = false;
        isLaunching = false;
        
    }

    public void calculate() {
        if (autoPosition == 2)
            calculatePosition2();
        if (autoPosition == 3)
            calculatePosition3();
        if(autoPosition == 10)
            trackBall();
    }

    public void nextState() {
        timer.reset();
        autoState++;
        leftSpeed = 0;
        rightSpeed = 0;
        isIntaking = false;
        isStoraging = false;
        isLaunching = false;

        stateEntry.setNumber(autoState);
    }

    public void calculatePosition2() {
        double time = timer.get();
        double rotation = navx.getAngle();
        rotateEntry.setDouble(rotation);

        if (autoState == 1) { // Backwards
            leftSpeed = -.2;
            rightSpeed = -.2;

            if (time > 2)
                nextState();
        } else if (autoState == 2) { // Launch
            isLaunching = true;
            isStoraging = true;

            if (time > 2)
                nextState();
        } else if (autoState == 3) { // Turn
            leftSpeed = .2;
            rightSpeed = -.2;

            if (rotation > 180)
                nextState();
        } else if (autoState == 4)  { // Ball
            trackBall();

            leftSpeed += 0.2;
            rightSpeed += 0.2;
            isIntaking = true;

            if (time > 4)
                nextState();
        } else if (autoState == 5) { // Turn
            leftSpeed = -.2;
            rightSpeed = .2;

            if (rotation < 10)
                nextState();
        } else if (autoState == 6)  { // Goal
            trackGoal();

            leftSpeed += 0.2;
            rightSpeed += 0.2;

            if (time > 3)
                nextState();
        } else if (autoState == 2) { // Launch
            isLaunching = true;
            isStoraging = false;

            if (time > 2)
                nextState();
        } else {
            leftSpeed = 0;
            rightSpeed = 0;
        }
    }

    public void calculatePosition3() {
        double time = timer.get();
        double rotation = (navx.getFusedHeading() + 180) % 360;

        stateEntry.setNumber(autoState);
        timeentry.setNumber(time);

        if (autoState == 1) {
            leftSpeed = -.5;
            rightSpeed = -.5;

            if (time > 1)
                nextState();
        } else if (autoState == 2) {
            leftSpeed = 0;
            rightSpeed = 0;
            // TODO: Launch Ball

            if (time > 1)
                nextState();
        } else if (autoState == 3) {
            leftSpeed = .5;
            rightSpeed = -.5;

            if (rotation > 150)
                nextState();
        }
    }

    public void trackBall() {
        double x = ballXEntry.getDouble(SCREEN_WIDTH / 2);
        double y = ballYEntry.getDouble(SCREEN_HEIGHT / 2);
        double newX = ((2*x)/SCREEN_WIDTH)-1;
        double newY = ((2*y)/SCREEN_HEIGHT)-1;

        leftSpeed = newX * 0.4;
        rightSpeed = -newX * 0.4;
    }

    public void trackGoal() {
        trackBall(); // TODO: Track Goal
    }

}
