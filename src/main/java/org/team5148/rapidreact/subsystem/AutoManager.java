package org.team5148.rapidreact.subsystem;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory.PathPlannerState;

import org.team5148.lib.drivetrains.Mecanum;
import org.team5148.lib.sensors.NavX;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.Timer;

/**
 * Manages autonomous routines and helpers
 */
public class AutoManager {
    
    // Constants
    private final double MAX_VELOCITY = 10.0; // m/s
    private final double MAX_ACCEL = 2.5; // m/s^2

    // Kinematics
    private Translation2d m_frontLeftLocation = new Translation2d(0.381, 0.381);
	private Translation2d m_frontRightLocation = new Translation2d(0.381, -0.381);
	private Translation2d m_backLeftLocation = new Translation2d(-0.381, 0.381);
	private Translation2d m_backRightLocation = new Translation2d(-0.381, -0.381);
	private MecanumDriveKinematics m_kinematics = new MecanumDriveKinematics(
		m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
	);

    // Path Planner
    private Trajectory m_autoPath1 = PathPlanner.loadPath("RapidReact1", MAX_VELOCITY, MAX_ACCEL);

    // Sensors
    private Timer timer = new Timer();
    private NavX navX = new NavX();
    /*private TargetCamera goalCamera = new TargetCamera(
        "GoalCamera",
        new Pose2d(),
        18,
        45,
        104
    );*/

    // Odometry
    private Pose2d robotPose = new Pose2d();
    private Rotation2d gyroAngle = new Rotation2d();
    private PIDController xController = new PIDController(0, 0, 0);
    private PIDController yController = new PIDController(0, 0, 0);
    private ProfiledPIDController zController = new ProfiledPIDController(0, 0, 0, new TrapezoidProfile.Constraints(Math.PI * 2, Math.PI));
    private HolonomicDriveController holonomicDriveController = new HolonomicDriveController(xController, yController, zController);

    public AutoManager(Mecanum mecanumDrive) {
    }

    /**
     * Gets the robot angle in degrees
     * @return Angle in degrees
     */
    public double getAngle() {
        return gyroAngle.getDegrees();
    }

    /**
     * Resets odometry and gyroscope
     */
    public void reset() {
        Pose2d initialPose = m_autoPath1.getInitialPose();
        navX.reset(initialPose.getRotation());
    }

    
    public void getPath() {

    }

    /**
     * Updates odometry model
     */
    public void update() {
        
        // Gyroscope
        gyroAngle = navX.getAngle();

        // Update Odometry
        // TODO: Odometry

        // Update Path Plan
        PathPlannerState desiredState = (PathPlannerState)m_autoPath1.sample(timer.get());
        ChassisSpeeds desiredChassisSpeed = holonomicDriveController.calculate(robotPose, desiredState, desiredState.holonomicRotation);
        MecanumDriveWheelSpeeds desiredWheelSpeed = m_kinematics.toWheelSpeeds(desiredChassisSpeed);
        desiredWheelSpeed.desaturate(MAX_VELOCITY);
    }

}
