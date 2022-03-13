package org.team5148.lib;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class PIDSparkMax {

    // Config
    public String name;
    public int id;
    public PIDConfig pidConfig;

    // Motor
    private CANSparkMax motor;
    private RelativeEncoder encoder;
    private SparkMaxPIDController pidController;
    
    // Network Tables
    private ShuffleboardTab shuffleboardTab;
    private NetworkTableEntry setVelocityEntry;
    private NetworkTableEntry velocityEntry;
    private NetworkTableEntry pEntry;
    private NetworkTableEntry iEntry;
    private NetworkTableEntry dEntry;
    private NetworkTableEntry ffEntry;
    private NetworkTableEntry minOutputEntry;
    private NetworkTableEntry maxOutputEntry;

    /**
     * Controls a SparkMAX using PID
     * @param name - Name for Network Tables
     * @param id - CAN ID
     * @param pidConfig - PID Configuration
     */
    public PIDSparkMax(String name, int id, PIDConfig pidConfig) {
        this.name = name;
        this.id = id;

        shuffleboardTab = Shuffleboard.getTab(name);
        setVelocityEntry = shuffleboardTab.add("Set Velocity", 0).getEntry();
        velocityEntry = shuffleboardTab.add("Current Velocity", 0).getEntry();
        pEntry = shuffleboardTab.add("Proportional", 0).getEntry();
        iEntry = shuffleboardTab.add("Integral", 0).getEntry();
        dEntry = shuffleboardTab.add("Derivative", 0).getEntry();
        ffEntry = shuffleboardTab.add("Feed Forward", 0).getEntry();
        minOutputEntry = shuffleboardTab.add("Min Output", 0).getEntry();
        maxOutputEntry = shuffleboardTab.add("Max Output", 0).getEntry();
        
        motor = new CANSparkMax(id, MotorType.kBrushless);
        encoder = motor.getEncoder();
        pidController = motor.getPIDController();
        motor.setOpenLoopRampRate(0.1);
        setConfig(pidConfig);
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

        pEntry.setDouble(pidConfig.p);
        iEntry.setDouble(pidConfig.i);
        dEntry.setDouble(pidConfig.d);
        ffEntry.setDouble(pidConfig.ff);
        minOutputEntry.setDouble(pidConfig.minOutput);
        maxOutputEntry.setDouble(pidConfig.maxOutput);
    }

    /**
     * Sets the speed of the motor
     * @param speed - Speed in Percent [-1 - 1]
     */
    public void setPercentage(double speed) {
        velocityEntry.setDouble(getVelocity());
        setVelocityEntry.setDouble(0);
        motor.set(speed);
    }

    /**
     * Sets the velocity of the motor
     * @param velocity - Velocity in RPM
     */
    public void setVelocity(double velocity) {
        velocityEntry.setDouble(getVelocity());
        setVelocityEntry.setDouble(velocity);
        if (velocity == 0)
            motor.stopMotor();
        else
            pidController.setReference(
                velocity,
                CANSparkMax.ControlType.kVelocity
            );
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

    /**
     * Sets the motor to be reversed
     * @param isInverted - Whether or not the motor is inverted
     */
    public void setInverted(boolean isInverted) {
        motor.setInverted(isInverted);
    }
}
