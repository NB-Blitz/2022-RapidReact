package org.team5148.lib.auto;

import com.revrobotics.RelativeEncoder;

import org.team5148.lib.drivers.MecanumDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveOdometry;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * Manages a mecanum drivetrain odometry model.
 */
public class MecanumDriveOdom {
    private Pose2d m_robotPose = new Pose2d(0, 0, Rotation2d.fromDegrees(0));
    private RelativeEncoder m_frontLeftEnc;
    private RelativeEncoder m_frontRightEnc;
    private RelativeEncoder m_backLeftEnc;
    private RelativeEncoder m_backRightEnc;
    private MecanumDriveOdometry m_odometry;

    private ShuffleboardTab m_shuffleboardTab;
    private NetworkTableEntry m_xEntry;
    private NetworkTableEntry m_yEntry;
    private NetworkTableEntry m_zEntry;
    private NetworkTableEntry m_poseEntry;

    public MecanumDriveOdom(MecanumDrive pm_mecanumDrive, MecanumDriveKinematics pm_kinematics) {
        m_frontLeftEnc = pm_mecanumDrive.m_frontLeftMotor.getEncoder();
        m_frontRightEnc = pm_mecanumDrive.m_frontRightMotor.getEncoder();
        m_backLeftEnc = pm_mecanumDrive.m_backLeftMotor.getEncoder();
        m_backRightEnc = pm_mecanumDrive.m_backRightMotor.getEncoder();
        m_odometry = new MecanumDriveOdometry(pm_kinematics, Rotation2d.fromDegrees(0));

        m_shuffleboardTab = Shuffleboard.getTab("Odometry");
        m_xEntry = m_shuffleboardTab.add("X Value", 0).getEntry();
        m_yEntry = m_shuffleboardTab.add("Y Value", 0).getEntry();
        m_zEntry = m_shuffleboardTab.add("Z Value", Rotation2d.fromDegrees(0)).getEntry();
        m_poseEntry = m_shuffleboardTab.add("Pose", new Pose2d()).getEntry();
    }

    /**
     * Updates mecanum odometry model
     * @param pm_gyroAngle Gyroscope angle in degrees
     */
    public void update(Rotation2d pm_gyroAngle) {
        m_robotPose = m_odometry.update(
            pm_gyroAngle,
            new MecanumDriveWheelSpeeds(
                m_frontLeftEnc.getVelocity(),
                m_frontRightEnc.getVelocity(),
                m_backLeftEnc.getVelocity(),
                m_backRightEnc.getVelocity()
            )
        );
        updateNT();
    }

    /**
     * Resets or updates the mecanum odometry model
     * @param pm_pose - Robot x, y, and rotational pose
     */
    public void reset(Pose2d pm_pose) {
        m_robotPose = pm_pose;
        m_odometry.resetPosition(pm_pose, pm_pose.getRotation());
        updateNT();
    }

    /**
     * Gets the robot pose
     * @return Pose2d of the approximate robot pose
     */
    public Pose2d getPose() {
        return m_robotPose;
    }

    private void updateNT() {
        m_xEntry.setValue(m_robotPose.getX());
        m_yEntry.setValue(m_robotPose.getY());
        m_zEntry.setValue(m_robotPose.getRotation());
        m_poseEntry.setValue(m_robotPose);
    }
}
