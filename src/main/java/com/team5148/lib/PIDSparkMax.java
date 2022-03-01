package com.team5148.lib;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class PIDSparkMax {
    public String name;
    public int id;
    public PIDConfig pidConfig;

    private CANSparkMax motor;
    private RelativeEncoder encoder;
    private SparkMaxPIDController pidController;
    
    private ShuffleboardTab shuffleboardTab;
    private NetworkTableEntry setVelocityEntry;
    private NetworkTableEntry velocityEntry;
    private NetworkTableEntry pEntry;
    private NetworkTableEntry iEntry;
    private NetworkTableEntry dEntry;
    private NetworkTableEntry ffEntry;
    private NetworkTableEntry minOutputEntry;
    private NetworkTableEntry maxOutputEntry;

    public PIDSparkMax(String name, int id, PIDConfig pidConfig) {
        this.name = name;
        this.id = id;
        this.pidConfig = pidConfig;
        
        motor = new CANSparkMax(id, MotorType.kBrushless);
        encoder = motor.getEncoder();
        pidController = motor.getPIDController();

        shuffleboardTab = Shuffleboard.getTab(name);
        setVelocityEntry = shuffleboardTab.add("Set Velocity", 0).getEntry();
        velocityEntry = shuffleboardTab.add("Current Velocity", 0).getEntry();
        pEntry = shuffleboardTab.add("Proportional", pidConfig.p).getEntry();
        iEntry = shuffleboardTab.add("Integral", pidConfig.p).getEntry();
        dEntry = shuffleboardTab.add("Derivative", pidConfig.p).getEntry();
        ffEntry = shuffleboardTab.add("Feed Forward", pidConfig.p).getEntry();
        minOutputEntry = shuffleboardTab.add("Min Output", pidConfig.minOutput).getEntry();
        maxOutputEntry = shuffleboardTab.add("Max Output", pidConfig.maxOutput).getEntry();
    }

    /**
     * Sets the PID configuration
     * @param pidConfig - PID Configuration
     */
    public void setConfig(PIDConfig pidConfig) {
        pidController.setP(pidConfig.p);
        pidController.setI(pidConfig.i);
        pidController.setD(pidConfig.d);
        pidController.setFF(pidConfig.ff);
        pidController.setOutputRange(pidConfig.minOutput, pidConfig.maxOutput);
        this.pidConfig = pidConfig;
    }

    /**
     * Sets the speed of the motor
     * @param speed - Speed in Percent [-1 - 1]
     */
    public void setPercentage(double speed) {
        setVelocityEntry.setDouble(0);
        motor.set(speed);
        update();
    }

    /**
     * Sets the velocity of the motor
     * @param velocity - Velocity in RPM
     */
    public void setVelocity(double velocity) {
        setVelocityEntry.setDouble(velocity);
        update();
    }

    /**
     * Gets the current motor velocity from it's encoders
     * @return Current velocity in RPM
     */
    public double getVelocity() {
        return encoder.getVelocity();
    }

    /**
     * Gets whether or not the motor is up to speed
     * @param rpmRange - Range in RPM
     * @return True if the motor is up to speed. False otherwise.
     */
    public boolean getRev(double rpmRange) {
        return Math.abs(getVelocity() - setVelocityEntry.getDouble(0)) < rpmRange;
    }

    private void update() {
        velocityEntry.setDouble(getVelocity());
        pidController.setReference(
            setVelocityEntry.getDouble(0),
            CANSparkMax.ControlType.kVelocity
        );

        // PID
        double kP = pEntry.getDouble(pidConfig.p);
        double kI = iEntry.getDouble(pidConfig.i);
        double kD = dEntry.getDouble(pidConfig.d);
        double kFF = ffEntry.getDouble(pidConfig.ff);
        double minOutput = minOutputEntry.getDouble(pidConfig.minOutput);
        double maxOutput = maxOutputEntry.getDouble(pidConfig.maxOutput);
        
        if (kP != pidConfig.p||
            kI != pidConfig.i ||
            kD != pidConfig.d ||
            kFF != pidConfig.ff ||
            minOutput != pidConfig.minOutput ||
            maxOutput != pidConfig.maxOutput) {

            pidConfig.p = kP;
            pidConfig.i = kI;
            pidConfig.d = kD;
            pidConfig.ff = kFF;
            pidConfig.minOutput = minOutput;
            pidConfig.maxOutput = maxOutput;

            setConfig(pidConfig);
        }
    }

    /**
     * Sets the motor to be reversed
     * @param isInverted - Whether or not the motor is inverted
     */
    public void setInverted(boolean isInverted) {
        motor.setInverted(isInverted);
    }
}
