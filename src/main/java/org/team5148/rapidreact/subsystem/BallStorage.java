package org.team5148.rapidreact.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;

import edu.wpi.first.wpilibj.DigitalInput;

public class BallStorage {

    private CANSparkMax intakeMotor = new CANSparkMax(MotorIDs.INTAKE, MotorType.kBrushless);
    private TalonSRX feedMotor = new TalonSRX(MotorIDs.FEED);
    private VictorSPX leftStorageMotor = new VictorSPX(MotorIDs.LEFT_STORAGE);
    private TalonSRX rightStorageMotor = new TalonSRX(MotorIDs.RIGHT_STORAGE);
    
    private DigitalInput lineBreakBegin = new DigitalInput(0);
    private DigitalInput lineBreakEnd = new DigitalInput(1);
    private NTManager nt = NTManager.getInstance();

    private boolean beginValue = false;
    private boolean endValue = false;

    public BallStorage() {
        leftStorageMotor.configOpenloopRamp(0.3);
        rightStorageMotor.configOpenloopRamp(0.3);
        feedMotor.configOpenloopRamp(0.3);
    }

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
        runStorage(0, 0);
    }

    /**
     * Runs storage motor
     */
    public void runStorage() {
        double leftStorageSpeed = nt.leftStorageSpeed.getDouble(DefaultSpeed.LEFT_STORAGE);
        double rightStorageSpeed = nt.rightStorageSpeed.getDouble(DefaultSpeed.RIGHT_STORAGE);
        runStorage(leftStorageSpeed, rightStorageSpeed);
    }

    /**
     * Runs storage motor
     */
    public void runStorageReverse() {
        double outakeSpeed = nt.outakeSpeed.getDouble(DefaultSpeed.OUTAKE);
        runStorage(outakeSpeed, outakeSpeed);
    }

    /**
     * Run storage motors at a given speed
     * @param speed - Speed to run storage motors at
     */
    public void runStorage(double leftSpeed, double rightSpeed){
        leftStorageMotor.set(ControlMode.PercentOutput, leftSpeed);
        rightStorageMotor.set(ControlMode.PercentOutput, rightSpeed);
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
        double outakeSpeed = nt.outakeSpeed.getDouble(DefaultSpeed.OUTAKE);
        runIntake(outakeSpeed);
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
        double outakeSpeed = nt.outakeSpeed.getDouble(DefaultSpeed.OUTAKE);
        runFeed(outakeSpeed);
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
