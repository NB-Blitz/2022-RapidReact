package org.team5148.lib.kinematics;

import org.team5148.lib.drivetrains.MecanumPID;
import org.team5148.lib.sensors.NavX;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveOdometry;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;

/**
 * Maintains an odometry model using mecanum drive
 */
public class MecanumOdom {
    private MecanumDriveOdometry m_odometry;
    private MecanumPID m_drivetrain;
    private NavX m_navX;
    
    /**
     * Creates and maintains a model for mecanum odometry
     * @param kinematics - Wheel positions in the form of MecanumDriveKinematics
     * @param drivetrain - The mecanum drivetrain in the form of MecanumPID
     */
    public MecanumOdom(MecanumDriveKinematics kinematics, MecanumPID drivetrain) {
        m_odometry = new MecanumDriveOdometry(kinematics, new Rotation2d());
        m_drivetrain = drivetrain;
        m_navX = NavX.getInstance();
    }

    /**
     * Updates the current odometry pose model
     * @return Current robot pose in a Pose2d
     */
    public Pose2d update() {
        Rotation2d angle = m_navX.getAngle();
        MecanumDriveWheelSpeeds wheelSpeeds = m_drivetrain.getWheelSpeeds();
        Pose2d pose = m_odometry.update(angle, wheelSpeeds);

        return pose;
    }
}
