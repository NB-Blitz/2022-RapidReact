package org.team5148.rapidreact.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;

import edu.wpi.first.wpilibj.DigitalInput;

public class BallStorage {

    private CANSparkMax intakeMotor = new CANSparkMax(MotorIDs.INTAKE, MotorType.kBrushless);
    private TalonSRX feedMotor = new TalonSRX(MotorIDs.FEED);
    private TalonSRX leftStorageMotor = new TalonSRX(MotorIDs.LEFT_STORAGE);
    private TalonSRX rightStorageMotor = new TalonSRX(MotorIDs.RIGHT_STORAGE);
    
    private DigitalInput lineBreakBegin = new DigitalInput(0);
    private DigitalInput lineBreakEnd = new DigitalInput(1);
    private NTManager nt = NTManager.getInstance();

    /**
     * Runs storage motors based on the line break sensors
     */
    public void runAutomatic() {
        boolean start = !lineBreakBegin.get();
        boolean end = !lineBreakEnd.get();

        nt.lineBreak1.setBoolean(start);
        nt.lineBreak2.setBoolean(end);
        
        if (end)
            runStorage(false);
        else
            runStorage(true);
        
        runIntake(!(end && start));
    }

    /**
     * Runs storage motors based on isRunning
     * @param isRunning - Whether or not to run the storage
     */
    public void runStorage(boolean isRunning) {
        double storageSpeed = isRunning ? nt.storageSpeed.getDouble(DefaultSpeed.STORAGE) : 0;
        runStorage(storageSpeed);
    }

    /**
     * Run storage motors at a given speed
     * @param speed - Speed to run storage motors at
     */
    public void runStorage(double speed){
        leftStorageMotor.set(ControlMode.PercentOutput, speed);
        rightStorageMotor.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Runs intake motors based on isRunning
     * @param isRunning - Whether or not to run the intake
     */
    public void runIntake(boolean isRunning) {
        double intakeSpeed = isRunning ? nt.intakeSpeed.getDouble(DefaultSpeed.INTAKE) : 0;
        runIntake(intakeSpeed);
    }

    /**
     * Run intake motors at a given speed
     * @param speed - Speed to run intake motors at
     */
    public void runIntake(double speed) {
        intakeMotor.set(-speed);
    }

    /**
     * Runs launcher feed motors based on isRunning
     * @param isRunning - Whether or not to run the feed to the launcher
     */
    public void runFeed(boolean isRunning) {
        double feedSpeed = isRunning ? nt.feedSpeed.getDouble(DefaultSpeed.FEED) : 0;
        runFeed(feedSpeed);
    }

    /**
     * Run launcher feed motors at a given speed
     * @param speed - Speed to run launcher feed motors at
     */
    public void runFeed(double speed) {
        feedMotor.set(ControlMode.PercentOutput, -speed);
    }
}
