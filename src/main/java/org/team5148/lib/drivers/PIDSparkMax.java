package org.team5148.lib.drivers;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import org.team5148.lib.util.PIDConfig;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * SparkMax wrapper that controls using PID
 */
public class PIDSparkMax extends CANSparkMax {

    public PIDConfig pidConfig;

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

    public PIDSparkMax(String shuffleboardName, int id, PIDConfig pidConfig) {
        super(id, MotorType.kBrushless);

        shuffleboardTab = Shuffleboard.getTab(shuffleboardName);
        setVelocityEntry = shuffleboardTab.add("Set Velocity", 0).getEntry();
        velocityEntry = shuffleboardTab.add("Current Velocity", 0).getEntry();
        pEntry = shuffleboardTab.add("Proportional", 0).getEntry();
        iEntry = shuffleboardTab.add("Integral", 0).getEntry();
        dEntry = shuffleboardTab.add("Derivative", 0).getEntry();
        ffEntry = shuffleboardTab.add("Feed Forward", 0).getEntry();
        minOutputEntry = shuffleboardTab.add("Min Output", 0).getEntry();
        maxOutputEntry = shuffleboardTab.add("Max Output", 0).getEntry();
        
        encoder = this.getEncoder();
        pidController = this.getPIDController();
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
        this.set(speed);
    }

    /**
     * Sets the velocity of the motor
     * @param velocity - Velocity in RPM
     */
    public void setVelocity(double velocity) {
        velocityEntry.setDouble(getVelocity());
        setVelocityEntry.setDouble(velocity);
        if (velocity == 0)
            this.stopMotor();
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
}
