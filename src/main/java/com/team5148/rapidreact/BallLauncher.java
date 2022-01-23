package com.team5148.rapidreact;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class BallLauncher {
    // Constants
    final double DEFAULT_TOP_SPEED = 0.7;
    final double DEFAULT_BOTTOM_SPEED = 0.7;

    // Network Tables
    ShuffleboardTab launcherTab = Shuffleboard.getTab("Launcher");
    NetworkTableEntry topSpeedEntry = launcherTab.add("Top Launcher Speed", DEFAULT_TOP_SPEED).getEntry();
    NetworkTableEntry bottomSpeedEntry = launcherTab.add("Bottom Launcher Speed", DEFAULT_BOTTOM_SPEED).getEntry();
    NetworkTableEntry topVelocityEntry = launcherTab.add("Top Launcher Velocity", 0).getEntry();
    NetworkTableEntry bottomVelocityEntry = launcherTab.add("Bottom Launcher Velocity", 0).getEntry();

    // Motors
    CANSparkMax topMotor = new CANSparkMax(5, MotorType.kBrushless);
    CANSparkMax bottomMotor = new CANSparkMax(6,MotorType.kBrushless);
    RelativeEncoder topEncoder = topMotor.getEncoder();
    RelativeEncoder bottomEncoder = bottomMotor.getEncoder();

    public void runLauncher() {
        double topSpeed = topSpeedEntry.getDouble(DEFAULT_TOP_SPEED);
        double bottomSpeed = bottomSpeedEntry.getDouble(DEFAULT_BOTTOM_SPEED);
        runLauncher(topSpeed, bottomSpeed);
    }

    public void runLauncher(double speed){
        runLauncher(speed, speed);
    }

    public void runLauncher(double topSpeed, double bottomSpeed) {
        topVelocityEntry.setDouble(topEncoder.getVelocity());
        bottomVelocityEntry.setDouble(bottomEncoder.getVelocity());

        topMotor.set(-topSpeed);
        bottomMotor.set(bottomSpeed);
    }

    public void stopLauncher() {
        runLauncher(0);
    }
}
