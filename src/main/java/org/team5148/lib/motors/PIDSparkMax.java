package org.team5148.lib.motors;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * SparkMax wrapper that controls using PID
 */
public class PIDSparkMax extends CANSparkMax {

    private RelativeEncoder m_encoder;
    private SparkMaxPIDController m_pidController;
    private double m_lastVelocity;
    private double m_lastPosition;
    
    private ShuffleboardTab m_shuffleboardTab;
    private NetworkTableEntry m_setVelocityEntry;
    private NetworkTableEntry m_setPositionEntry;
    private NetworkTableEntry m_setSpeedEntry;
    private NetworkTableEntry m_velocityEntry;

    /**
     * Initializes a SparkMAX that utilized PID
     * @param shuffleboardName - Shuffleboard tab to send network table values to
     * @param id - CAN ID of the SparkMAX
     */
    public PIDSparkMax(String shuffleboardTabName, int id) {
        super(id);

        m_encoder = getEncoder();
        m_pidController = getPIDController();
        m_lastVelocity = Double.NaN;
        m_lastPosition = Double.NaN;

        m_shuffleboardTab = Shuffleboard.getTab(shuffleboardTabName);
        m_setVelocityEntry = m_shuffleboardTab.add("Set Velocity", 0).getEntry();
        m_setPositionEntry = m_shuffleboardTab.add("Set Position", 0).getEntry();
        m_setSpeedEntry = m_shuffleboardTab.add("Set Speed", 0).getEntry();
        m_velocityEntry = m_shuffleboardTab.add("Current Velocity", 0).getEntry();
    }

    private void updateNT() {
        m_velocityEntry.setDouble(getVelocity());
        m_setPositionEntry.setDouble(m_lastPosition);
        m_setVelocityEntry.setDouble(m_lastVelocity);
        m_setSpeedEntry.setDouble(m_lastSpeed);
    }

    @Override
    public void set(double speed) {
        super.set(speed);
        
        m_lastVelocity = Double.NaN;
        m_lastPosition = Double.NaN;
        updateNT();
    }

    /**
     * Sets the velocity of the motor
     * @param velocity - Velocity in RPM * Conversion Factor
     */
    public void setVelocity(double velocity) {
        if (m_lastVelocity != velocity) {
            m_pidController.setReference(velocity, ControlType.kVelocity);
        }

        m_lastSpeed = Double.NaN;
        m_lastPosition = Double.NaN;
        m_lastVelocity = velocity;
        updateNT();
    }

    /**
     * Sets the position of the motor
     * @param velocity - Position in Rotations * Conversion Factor
     */
    public void setPosition(double position) {
        if (m_lastPosition != position) {
            m_pidController.setReference(position, ControlType.kPosition);
        }

        m_lastSpeed = Double.NaN;
        m_lastPosition = position;
        m_lastVelocity = Double.NaN;
        updateNT();
    }

    /**
     * Gets the current encoder velocity from it's encoders.
     * Note: Encoders have ~110ms delay built-in to the SparkMAXs.
     * @return Current velocity in RPM * Conversion
     */
    public double getVelocity() {
        return m_encoder.getVelocity();
    }

    /**
     * Gets the current encoder position from it's encoders.
     * Note: Encoders have ~110ms delay built-in to the SparkMAXs.
     * @return Current position in Rotations * Conversion
     */
    public double getPosition() {
        return m_encoder.getPosition();
    }

    /**
     * Gets whether or not the motor is up to speed
     * @param rpmRange - Range in RPM
     * @return True if the motor is up to speed. False otherwise.
     */
    public boolean getRev(double rpmRange) {
        return Math.abs(getVelocity() - m_lastVelocity) < rpmRange;
    }
}
