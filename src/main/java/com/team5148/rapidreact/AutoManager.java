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

    // Sensors
    Timer timer = new Timer();
    AHRS navx = new AHRS(Port.kMXP);

    // Network Tables
    ShuffleboardTab autoTab = Shuffleboard.getTab("Autonomous");
    NetworkTableEntry stateEntry = autoTab.add("Auto State", 0).getEntry();
    NetworkTableEntry timeentry = autoTab.add("Time Entry", 0).getEntry();
    NetworkTableEntry xBallEntry = NetworkTableInstance.getDefault().getTable("Autonomous").getEntry("X Ball Value");
    NetworkTableEntry yBallEntry = NetworkTableInstance.getDefault().getTable("Autonomous").getEntry("Y Ball Value");
    // State
    int autoState = 1;
    int autoPosition = 10;

    public void reset() {
        timer.reset();
        timer.start();
        navx.reset();
        autoState = 1;
    }

    public void calculate() {
        if (autoPosition == 2)
            calculatePosition2();
        if (autoPosition == 3)
            calculatePosition3();
        if(autoPosition == 10) {
            trackBall();
        }
    }

    public void nextState() {
        timer.reset();
        autoState++;
        stateEntry.setNumber(autoState);
    }

    public void calculatePosition2() {
        double time = timer.get();
        double rotation = navx.getFusedHeading();

        if (autoState == 1) {
            leftSpeed = -.5;
            rightSpeed = -.5;

            if (time > 1)
                nextState();
        } else if (autoState == 2) {
            // TODO: Launch Ball

            if (time > 1)
                nextState();
        } else if (autoState == 3) {
            leftSpeed = .5;
            rightSpeed = -.5;

            if (rotation > 135)
                nextState();
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
        double x = xBallEntry.getDouble(SCREEN_WIDTH / 2);
        double y = yBallEntry.getDouble(SCREEN_HEIGHT / 2);
        double newX = ((2*x)/SCREEN_WIDTH)-1;
        double newY = ((2*y)/SCREEN_HEIGHT)-1;



        leftSpeed = newX * 0.6;
        rightSpeed = -newX * 0.6;
    }

}
