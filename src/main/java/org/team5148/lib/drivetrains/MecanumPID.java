package org.team5148.lib.drivetrains;

import org.team5148.lib.motors.PIDSparkMax;

import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;

public class MecanumPID extends Mecanum {
    public PIDSparkMax m_frontLeftMotor;
    public PIDSparkMax m_frontRightMotor;
    public PIDSparkMax m_backLeftMotor;
    public PIDSparkMax m_backRightMotor;

    public MecanumPID() {
        super(
            new PIDSparkMax("Front Left", 1),
            new PIDSparkMax("Front Right", 2),
            new PIDSparkMax("Back Left", 3),
            new PIDSparkMax("Back Right", 4)
        );

        m_frontLeftMotor = (PIDSparkMax)super.m_frontLeftMotor;
        m_frontRightMotor = (PIDSparkMax)super.m_frontRightMotor;
        m_backLeftMotor = (PIDSparkMax)super.m_backLeftMotor;
        m_backRightMotor = (PIDSparkMax)super.m_backRightMotor;
    }

    /**
     * Gets the current mecanum wheel speeds
     * @return MecanumDriveWheelSpeeds with current velocities
     */
    public MecanumDriveWheelSpeeds getWheelSpeeds() {
		return new MecanumDriveWheelSpeeds(
			m_frontLeftMotor.getVelocity(),
			m_frontRightMotor.getVelocity(),
			m_backLeftMotor.getVelocity(),
			m_backRightMotor.getVelocity());
	}

    /**
     * Drives the mecanum drivetrain using a set of wheel speeds
     * @param speeds - MecanumDriveWheelSpeeds containing desired wheel speeds
     */
    public void drive(MecanumDriveWheelSpeeds speeds) {
        m_frontLeftMotor.setVelocity(speeds.frontLeftMetersPerSecond);
        m_frontRightMotor.setVelocity(speeds.frontRightMetersPerSecond);
        m_backLeftMotor.setVelocity(speeds.rearLeftMetersPerSecond);
        m_backRightMotor.setVelocity(speeds.rearRightMetersPerSecond);
    }
}
