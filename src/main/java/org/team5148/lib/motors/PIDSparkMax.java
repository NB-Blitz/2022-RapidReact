package org.team5148.lib.motors;

import com.revrobotics.RelativeEncoder;

import org.team5148.lib.util.PIDConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * SparkMax wrapper that controls using PID
 */
public class PIDSparkMax extends CANSparkMax {

    private PIDConfig m_pidConfig;
    private RelativeEncoder m_encoder;
    private PIDController m_pidController;
    private double m_setpoint = 0;

    // Network Tables
    private ShuffleboardTab m_shuffleboardTab;
    private NetworkTableEntry m_setValueEntry;
    private NetworkTableEntry m_velocityEntry;

    public PIDSparkMax(String shuffleboardName, int id, PIDConfig pidConfig) {
        super(id);

        m_pidController = new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD);

        m_shuffleboardTab = Shuffleboard.getTab(shuffleboardName);
        m_setValueEntry = m_shuffleboardTab.add("Set Value", 0).getEntry();
        m_velocityEntry = m_shuffleboardTab.add("Current Velocity", 0).getEntry();
        m_shuffleboardTab.add("PID Controller", m_pidController);
        
        m_encoder = this.getEncoder();
        
        setConfig(pidConfig);
    }

    /**
     * Sets the PID configuration
     * @param pidConfig - PID Configuration
     */
    public void setConfig(PIDConfig pidConfig) {
        m_pidController.setP(pidConfig.kP);
        m_pidController.setI(pidConfig.kI);
        m_pidController.setD(pidConfig.kD);

        this.m_pidConfig = pidConfig;
    }

    /**
     * Sets the speed of the motor
     * @param speed - Speed in Percent [-1 - 1]
     */
    public void setPercentage(double speed) {
        double currentVelocity = getVelocity();

        m_velocityEntry.setDouble(currentVelocity);
        m_setValueEntry.setValue(speed);
        m_setpoint = 0;
        
        set(speed);
    }

    /**
     * Sets the velocity of the motor
     * @param setVelocity - Velocity in RPM
     */
    public void setVelocity(double setVelocity) {
        double currentVelocity = getVelocity();
        double setValue = m_pidController.calculate(currentVelocity, setVelocity);
        if (setVelocity == 0)
            setValue = 0;

        m_velocityEntry.setDouble(currentVelocity);
        m_setValueEntry.setValue(setValue);
        m_setpoint = 0;
        
        set(setValue);
    }

    /**
     * Gets the current motor velocity from it's encoders
     * @return Current velocity in RPM
     */
    public double getVelocity() {
        return m_encoder.getVelocity();
    }

    /**
     * Gets whether or not the motor is up to speed
     * @param rpmRange - Range in RPM
     * @return True if the motor is up to speed. False otherwise.
     */
    public boolean getRev(double rpmRange) {
        return Math.abs(getVelocity() - m_setpoint) < rpmRange;
    }

    /**
     * Gets the PID configuration
     * @return The current PIDConfig
     */
    public PIDConfig getConfig() {
        return m_pidConfig;
    }
}
