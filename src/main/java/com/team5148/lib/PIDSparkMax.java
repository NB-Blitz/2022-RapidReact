package com.team5148.lib;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

public class PIDSparkMax {
    public String name;
    public int id;
    public PIDConfig pidConfig;

    private CANSparkMax motor;
    private RelativeEncoder encoder;
    private SparkMaxPIDController pidController;
    private double setVelocity = 0;
    
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
        pEntry = shuffleboardTab.add("Proportional", 0).getEntry();
        iEntry = shuffleboardTab.add("Integral", 0).getEntry();
        dEntry = shuffleboardTab.add("Derivative", 0).getEntry();
        ffEntry = shuffleboardTab.add("Feed Forward", 0).getEntry();
        minOutputEntry = shuffleboardTab.add("Min Output", 0).getEntry();
        maxOutputEntry = shuffleboardTab.add("Max Output", 0).getEntry();
    }

    public void setConfig(PIDConfig pidConfig) {
        pidController.setP(pidConfig.p);
        pidController.setI(pidConfig.i);
        pidController.setD(pidConfig.d);
        pidController.setFF(pidConfig.ff);
        pidController.setOutputRange(pidConfig.minOutput, pidConfig.maxOutput);
    }

    /**
     * Sets the velocity of the motor
     * @param velocity - Velocity in RPM
     */
    public void setVelocity(double velocity) {
        pidController.setReference(velocity, CANSparkMax.ControlType.kVelocity);
        this.setVelocity = velocity;
    }

    /**
     * Gets the current motor velocity from it's encoders
     * @return Current velocity in RPM
     */
    public double getVelocity() {
        return encoder.getVelocity();
    }

    /**
     * Uploads all values from Shuffleboard. 
     * Values are listed underneath the motor's name.
     */
    public void putDashboard() {
        setVelocityEntry.setDouble(setVelocity);
        velocityEntry.setDouble(getVelocity());
        pEntry.setDouble(pidConfig.p);
        iEntry.setDouble(pidConfig.i);
        dEntry.setDouble(pidConfig.d);
        ffEntry.setDouble(pidConfig.ff);
        minOutputEntry.setDouble(pidConfig.minOutput);
        maxOutputEntry.setDouble(pidConfig.maxOutput);
    }

    /**
     * Downloads all values from Shuffleboard. 
     * Values are listed underneath the motor's name.
     */
    public void getDashboard() {
        double velocity = setVelocityEntry.getDouble(0);
        double p = pEntry.getDouble(0);
        double i = iEntry.getDouble(0);
        double d = dEntry.getDouble(0);
        double ff = ffEntry.getDouble(0);
        double minOutput = minOutputEntry.getDouble(0);
        double maxOutput = maxOutputEntry.getDouble(0);
        
        setConfig(new PIDConfig(p, i, d, ff, minOutput, maxOutput));
        setVelocity(velocity);
    }
}
