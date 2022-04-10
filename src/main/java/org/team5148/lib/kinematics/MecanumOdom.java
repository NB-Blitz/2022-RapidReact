package org.team5148.lib.kinematics;

import org.team5148.lib.drivetrains.MecanumPID;
import org.team5148.lib.sensors.NavX;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.MecanumDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.WPIUtilJNI;

/**
 * Maintains an odometry model using mecanum drive
 */
public class MecanumOdom {

    /**
     * Standard deviations of model states. Increase these numbers to trust your
     * model's state estimates less. This matrix is in the form [x, y, theta]ᵀ, with units in
     * meters and radians.
     */
    private final Matrix<N3, N1> k_stateStdDevs = VecBuilder.fill(0.05, 0.05, Units.degreesToRadians(5)); // State measurement standard deviations. X, Y, theta.

    /**
     * Standard deviations of the encoder and gyro measurements.
     * Increase these numbers to trust sensor readings from encoders and gyros less. This matrix
     * is in the form [theta], with units in radians
     */
    private final Matrix<N1, N1> k_localMeasurementStdDevs = VecBuilder.fill(0.01);

    /**
     * Standard deviations of the vision measurements. Increase these
     * numbers to trust global measurements from vision less. This matrix is in
     * the form [x, y, theta]ᵀ, with units in meters and radians.
     */
    private final Matrix<N3, N1> k_visionMeasurementStdDevs = VecBuilder.fill(0.01, 0.01, Units.degreesToRadians(0.1));

    /**
     * The positions of each mecanum wheel relative to the center of the robot.
     * 
     */
	private final MecanumDriveKinematics k_mecanumKinematics = new MecanumDriveKinematics(
		new Translation2d(0.381, 0.381), // Front Left
        new Translation2d(0.381, -0.381), // Front Right
        new Translation2d(-0.381, 0.381), // Back Left
        new Translation2d(-0.381, -0.381) // Back Right
	);

    private MecanumDrivePoseEstimator m_poseEstimator;
    private MecanumPID m_drivetrain;
    private NavX m_navX;
    
    /**
     * Creates and maintains a model for mecanum odometry
     * @param kinematics - Wheel positions in the form of MecanumDriveKinematics
     * @param drivetrain - The mecanum drivetrain in the form of MecanumPID
     */
    public MecanumOdom(MecanumPID drivetrain) {
        m_poseEstimator = new MecanumDrivePoseEstimator(
            new Rotation2d(),
            new Pose2d(),
            k_mecanumKinematics,
            k_stateStdDevs,
            k_localMeasurementStdDevs,
            k_visionMeasurementStdDevs
        );
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
        Pose2d pose = m_poseEstimator.update(angle, wheelSpeeds);

        return pose;
    }

    /**
     * Resets the current odometry pose model
     */
    public void reset() {
        m_poseEstimator.resetPosition(new Pose2d(), m_navX.getAngle());
    }

    /**
     * Applys robot pose aquired from external camera vision
     * @param visionPose - Robot pose aquied from external camera vision
     */
    public void addVisionPose(Pose2d visionPose) {
        m_poseEstimator.addVisionMeasurement(visionPose, WPIUtilJNI.now() * 1.0e-6);
    }
}
