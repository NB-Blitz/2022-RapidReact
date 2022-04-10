package org.team5148.rapidreact.subsystem;

import org.team5148.lib.drivetrains.MecanumPID;
import org.team5148.lib.kinematics.MecanumOdom;
import org.team5148.rapidreact.NTManager;

import edu.wpi.first.math.geometry.Pose2d;
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
	private MecanumDriveKinematics mecanumKinematics = new MecanumDriveKinematics(
		m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
	);

    // Odometry
    private MecanumOdom mecanumOdom;

    // Other
    NTManager nt = NTManager.getInstance();

    public AutoManager(MecanumPID mecanumDrive) {
        // TODO: Test odometry & Implement path planner
        mecanumOdom = new MecanumOdom(mecanumKinematics, mecanumDrive);
    }

    public void reset() {
        mecanumOdom.reset();
    }

    public void update() {
        Pose2d robotPose = mecanumOdom.update();
        nt.simField.setRobotPose(robotPose);
    }
}
