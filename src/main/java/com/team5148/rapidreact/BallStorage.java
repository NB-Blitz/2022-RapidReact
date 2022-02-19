package com.team5148.rapidreact;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team5148.rapidreact.config.DefaultSpeed;
import com.team5148.rapidreact.config.MotorIDs;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class BallStorage {
    // Network
    ShuffleboardTab storageTab = Shuffleboard.getTab("Storage");
    NetworkTableEntry storageSpeedEntry = storageTab.add("Storage Speed", DefaultSpeed.STORAGE).getEntry();
    NetworkTableEntry intakeSpeedEntry = storageTab.add("Intake Speed", DefaultSpeed.INTAKE).getEntry();
    NetworkTableEntry feedSpeedEntry = storageTab.add("Feed Speed", DefaultSpeed.FEED).getEntry();

    // Motors
    CANSparkMax feedMotor = new CANSparkMax(MotorIDs.FEED, MotorType.kBrushless);
    CANSparkMax leftStorageMotor = new CANSparkMax(MotorIDs.LEFT_STORAGE, MotorType.kBrushless);
    CANSparkMax rightStorageMotor = new CANSparkMax(MotorIDs.RIGHT_STORAGE, MotorType.kBrushless);
    CANSparkMax intakeMotor = new CANSparkMax(MotorIDs.INTAKE, MotorType.kBrushless);

    // Line Breaks
    DigitalInput lineBreakBegin = new DigitalInput(0);
    DigitalInput lineBreakEnd = new DigitalInput(1);

    // Values
    boolean isStorageFull = false;

    /**
     * Resets storage system
     */
    public void resetStorage() {
        isStorageFull = false;
    }

    /**
     * Runs storage motors based on the line break sensors
     */
    public void runAuto() {
        boolean start = lineBreakBegin.get();
        boolean end = lineBreakEnd.get();

        if (end && !start)
            runStorage(false);
        else
            runStorage(true);
        
        if (end && start)
            isStorageFull = true;
        runIntake(!isStorageFull);
    }

    /**
     * Runs storage motors based on isRunning
     * @param isRunning - Whether or not to run the storage
     */
    public void runStorage(boolean isRunning) {
        double storageSpeed = isRunning ? storageSpeedEntry.getDouble(DefaultSpeed.STORAGE) : 0;
        runStorage(storageSpeed);
    }

    /**
     * Run storage motors at a given speed
     * @param speed - Speed to run storage motors at
     */
    public void runStorage(double speed){
        leftStorageMotor.set(speed);
        rightStorageMotor.set(-speed);
    }

    /**
     * Runs intake motors based on isRunning
     * @param isRunning - Whether or not to run the intake
     */
    public void runIntake(boolean isRunning) {
        double intakeSpeed = isRunning ? intakeSpeedEntry.getDouble(DefaultSpeed.INTAKE) : 0;
        runIntake(intakeSpeed);
    }

    /**
     * Run intake motors at a given speed
     * @param speed - Speed to run intake motors at
     */
    public void runIntake(double speed) {
        intakeMotor.set(speed);
    }

    /**
     * Runs launcher feed motors based on isRunning
     * @param isRunning - Whether or not to run the feed to the launcher
     */
    public void runFeed(boolean isRunning) {
        double feedSpeed = isRunning ? feedSpeedEntry.getDouble(DefaultSpeed.FEED) : 0;
        runFeed(feedSpeed);
    }

    /**
     * Run launcher feed motors at a given speed
     * @param speed - Speed to run launcher feed motors at
     */
    public void runFeed(double speed) {
        feedMotor.set(speed);
    }
}
