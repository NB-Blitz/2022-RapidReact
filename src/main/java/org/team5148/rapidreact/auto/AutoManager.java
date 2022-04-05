package org.team5148.rapidreact.auto;

import org.team5148.lib.auto.MecanumDriveOdom;
import org.team5148.lib.drivers.MecanumDrive;
import org.team5148.lib.drivers.NavX;
import org.team5148.lib.vision.FieldCamera;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;

/**
 * Manages autonomous routines and helpers
 */
public class AutoManager {
    
    // Kinematics
    private Translation2d m_frontLeftLocation = new Translation2d(0.381, 0.381);
	private Translation2d m_frontRightLocation = new Translation2d(0.381, -0.381);
	private Translation2d m_backLeftLocation = new Translation2d(-0.381, 0.381);
	private Translation2d m_backRightLocation = new Translation2d(-0.381, -0.381);
	private MecanumDriveKinematics m_kinematics = new MecanumDriveKinematics(
		m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
	);

    // Sensors
    private NavX navX = new NavX();
    private MecanumDriveOdom mecanumDriveOdom;
    private FieldCamera goalCamera = new FieldCamera(
        "GoalCamera",
        new Pose2d(),
        18,
        45,
        104
    );

    public AutoManager(MecanumDrive mecanumDrive) {
        mecanumDriveOdom = new MecanumDriveOdom(mecanumDrive, m_kinematics);
    }

    /**
     * Resets odometry and gyroscope
     */
    public void reset() {
        navX.reset(0);
        mecanumDriveOdom.reset(new Pose2d());
    }

    /**
     * Updates odometry model
     */
    public void update() {
        Rotation2d gyroAngle = navX.getAngle();

        Pose2d robotPose = goalCamera.getRobotPose(gyroAngle);
        if (robotPose != null)
            mecanumDriveOdom.reset(robotPose);
        mecanumDriveOdom.update(gyroAngle);
    }

}
