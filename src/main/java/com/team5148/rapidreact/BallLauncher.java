package com.team5148.rapidreact;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team5148.rapidreact.config.DefaultSpeed;
import com.team5148.rapidreact.config.MotorIDs;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class BallLauncher {

    // Network Tables
    ShuffleboardTab launcherTab = Shuffleboard.getTab("Launcher");
    NetworkTableEntry topSpeedEntry = launcherTab.add("Top Launcher Speed", DefaultSpeed.TOP_LAUNCHER).getEntry();
    NetworkTableEntry bottomSpeedEntry = launcherTab.add("Bottom Launcher Speed", DefaultSpeed.BOTTOM_LAUNCHER).getEntry();
    NetworkTableEntry topVelocityEntry = launcherTab.add("Top Launcher Velocity", 0).getEntry();
    NetworkTableEntry bottomVelocityEntry = launcherTab.add("Bottom Launcher Velocity", 0).getEntry();

    // Motors
    CANSparkMax topMotor = new CANSparkMax(MotorIDs.LAUNCHER_TOP, MotorType.kBrushless);
    CANSparkMax bottomMotor = new CANSparkMax(MotorIDs.LAUNCHER_BOTTOM, MotorType.kBrushless);
    RelativeEncoder topEncoder = topMotor.getEncoder();
    RelativeEncoder bottomEncoder = bottomMotor.getEncoder();
    SparkMaxPIDController topPIDController = topMotor.getPIDController();
    SparkMaxPIDController bottomPIDController = bottomMotor.getPIDController();

    /**
     * Runs the launcher at the default speed
     * @param isRunning - Whether or not the launcher is running
     */
    public void runLauncher(boolean isRunning) {
        double topSpeed = isRunning ? topSpeedEntry.getDouble(MotorIDs.LAUNCHER_TOP) : 0;
        double bottomSpeed = isRunning ? bottomSpeedEntry.getDouble(MotorIDs.LAUNCHER_BOTTOM) : 0;
        runLauncher(topSpeed, bottomSpeed);
    }

    /**
     * Runs the launcher at a set speed
     * @param speed - Speed to set to [-1 - 1]
     */
    public void runLauncher(double speed){
        runLauncher(speed, speed);
    }

    /**
     * Runs each launcher motor independently
     * @param topSpeed - Speed of the top motor [-1 - 1]
     * @param bottomSpeed - Speed of the bottom motor [-1 - 1]
     */
    public void runLauncher(double topSpeed, double bottomSpeed) {
        topVelocityEntry.setDouble(topEncoder.getVelocity());
        bottomVelocityEntry.setDouble(bottomEncoder.getVelocity());

        topMotor.set(topSpeed);
        bottomMotor.set(-bottomSpeed);
    }
    
    /**
     * Runs each launcher motor using PID
     * @param topVelocity - Velocity of the top motor in RPM
     * @param bottomVelocity - Velocity of the bottom motor in RPM
     */
    public void runLauncherVelocity(double topVelocity, double bottomVelocity) {
        topVelocityEntry.setDouble(topEncoder.getVelocity());
        bottomVelocityEntry.setDouble(bottomEncoder.getVelocity());

        // TODO
    }
}
