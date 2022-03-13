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

    private boolean beginValue = false;
    private boolean endValue = false;

    /**
     * Runs storage motors based on the line break sensors
     */
    public void runAutomatic() {
        update();
        
        if (!endValue)
            runStorage();
        else
            stopStorage();

        stopFeed();
    }

    /**
     * Stops the storage motor
     */
    public void stopStorage() {
        runStorage(0);
    }

    /**
     * Runs storage motor
     */
    public void runStorage() {
        double storageSpeed = nt.storageSpeed.getDouble(DefaultSpeed.STORAGE);
        runStorage(storageSpeed);
    }

    /**
     * Runs storage motor
     */
    public void runStorageReverse() {
        double storageSpeed = -nt.storageSpeed.getDouble(DefaultSpeed.STORAGE) * 0.5;
        runStorage(storageSpeed);
    }

    /**
     * Run storage motors at a given speed
     * @param speed - Speed to run storage motors at
     */
    public void runStorage(double speed){
        leftStorageMotor.set(ControlMode.PercentOutput, speed);
        rightStorageMotor.set(ControlMode.PercentOutput, speed);
        update();
    }

    /**
     * Stops the intake motor
     */
    public void stopIntake() {
        runIntake(0);
    }

    /**
     * Runs the intake motor
     */
    public void runIntake() {
        double intakeSpeed = nt.intakeSpeed.getDouble(DefaultSpeed.INTAKE);
        runIntake(intakeSpeed);
    }

    /**
     * Runs the intake motor in reverse
     */
    public void runIntakeReverse() {
        double intakeSpeed = -nt.intakeSpeed.getDouble(DefaultSpeed.INTAKE);
        runIntake(intakeSpeed);
    }

    /**
     * Runs the intake motor at a given speed
     * @param speed - Speed to run intake motor at
     */
    public void runIntake(double speed) {
        intakeMotor.set(-speed);
    }

    /**
     * Stops the feed motor
     */
    public void stopFeed() {
        runFeed(0);
    }

    /**
     * Runs the feed motor
     */
    public void runFeed() {
        double feedSpeed = nt.feedSpeed.getDouble(DefaultSpeed.FEED);
        runFeed(feedSpeed);
    }

    /**
     * Runs the feed motor in reverse
     */
    public void runFeedReverse() {
        double feedSpeed = -nt.feedSpeed.getDouble(DefaultSpeed.FEED) * 0.5;
        runFeed(feedSpeed);
    }

    /**
     * Run launcher feed motors at a given speed
     * @param speed - Speed to run launcher feed motors at
     */
    public void runFeed(double speed) {
        feedMotor.set(ControlMode.PercentOutput, -speed);
    }

    private void update() {
        beginValue = !lineBreakBegin.get();
        endValue = !lineBreakEnd.get();

        nt.lineBreak1.setBoolean(beginValue);
        nt.lineBreak2.setBoolean(endValue);
    }
}
