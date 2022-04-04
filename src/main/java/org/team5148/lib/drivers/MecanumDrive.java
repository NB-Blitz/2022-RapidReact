package org.team5148.lib.drivers;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team5148.lib.util.Vector3;

/**
 * Controls a mecanum drivetrain using brushless SparkMaxes.
 * CAN IDs must be 1 - 4
 */
public class MecanumDrive {
    
    public LazySparkMax m_frontLeftMotor;
    public LazySparkMax m_frontRightMotor;
    public LazySparkMax m_backLeftMotor;
    public LazySparkMax m_backRightMotor;

    public MecanumDrive(double pm_rampRate) {
        m_frontLeftMotor = new LazySparkMax(1, MotorType.kBrushless);
        m_frontRightMotor = new LazySparkMax(2, MotorType.kBrushless);
        m_backLeftMotor = new LazySparkMax(3, MotorType.kBrushless);
        m_backRightMotor = new LazySparkMax(4, MotorType.kBrushless);

        m_frontLeftMotor.setOpenLoopRampRate(pm_rampRate);
        m_frontRightMotor.setOpenLoopRampRate(pm_rampRate);
        m_backLeftMotor.setOpenLoopRampRate(pm_rampRate);
        m_backRightMotor.setOpenLoopRampRate(pm_rampRate);
    }

    /**
     * Drives the mecanum drivetrain using a Vector3 input
     * @param input - 3-Axis Input
     */
    public void drive(Vector3 pm_input) {
        m_backLeftMotor.set(-pm_input.x + pm_input.y - pm_input.z);
		m_backRightMotor.set(pm_input.x + pm_input.y + pm_input.z);
		m_frontLeftMotor.set(pm_input.x + pm_input.y - pm_input.z);
		m_frontRightMotor.set(-pm_input.x + pm_input.y + pm_input.z);	
    }
}
