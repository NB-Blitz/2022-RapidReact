package com.team5148.rapidreact;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class BallStorage {
    // Constants
    final double DEFAULT_INTAKE_SPEED = 0.5;
    final double DEFAULT_STORAGE_SPEED = 0.5;

    // Network
    ShuffleboardTab storageTab = Shuffleboard.getTab("Storage");
    NetworkTableEntry storageSpeedEntry = storageTab.add("Storage Speed", DEFAULT_STORAGE_SPEED).getEntry();
    NetworkTableEntry intakeSpeedEntry = storageTab.add("Intake Speed", DEFAULT_INTAKE_SPEED).getEntry();

    // Motors
    TalonSRX storageMotor1 = new TalonSRX(8);
    CANSparkMax storageMotor2 = new CANSparkMax(9, MotorType.kBrushless);
    CANSparkMax storageMotor3 = new CANSparkMax(10, MotorType.kBrushless);
    CANSparkMax intakeMotor = new CANSparkMax(11,MotorType.kBrushless);

    // Line Breaks
    DigitalInput lineBreak1 = new DigitalInput(0);
    DigitalInput lineBreak2 = new DigitalInput(1);
    DigitalInput lineBreak3 = new DigitalInput(2);

    // Values
    boolean isStorageFull = false;

    public void runStorage(boolean isRunning) {
        double storageSpeed = isRunning ? storageSpeedEntry.getDouble(DEFAULT_STORAGE_SPEED) : 0;
        runStorage(storageSpeed);
    }

    public void runStorage(double speed){
        storageMotor1.set(ControlMode.PercentOutput, -speed);
        storageMotor2.set(-speed);
        storageMotor3.set(-speed);
    }

    public void runIntake(boolean isRunning)
    {
        double intakeSpeed = isRunning ? intakeSpeedEntry.getDouble(DEFAULT_INTAKE_SPEED) : 0;
        runIntake(intakeSpeed);
    }

    public void runIntake(double speed) {
        intakeMotor.set(speed);
    }
}
