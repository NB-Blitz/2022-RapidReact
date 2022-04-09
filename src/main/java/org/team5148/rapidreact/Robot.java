package org.team5148.rapidreact;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveOdometry;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.I2C.Port;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team5148.rapidreact.config.MotorIDs;

public class Robot extends TimedRobot {

	// Constants
	private final double DEADBAND = 0.2;
	private final double RAMP = 0.3;

	// Controllers
	private XboxController m_driveController = new XboxController(0);

	// Motors
	private CANSparkMax m_frontLeft = new CANSparkMax(MotorIDs.FRONT_LEFT, MotorType.kBrushless);
	private CANSparkMax m_frontRight = new CANSparkMax(MotorIDs.FRONT_RIGHT, MotorType.kBrushless);
	private CANSparkMax m_backLeft = new CANSparkMax(MotorIDs.BACK_LEFT, MotorType.kBrushless);
	private CANSparkMax m_backRight = new CANSparkMax(MotorIDs.BACK_RIGHT, MotorType.kBrushless);

	// Encoders
	private RelativeEncoder m_frontLeftEncoder = m_frontLeft.getEncoder();
	private RelativeEncoder m_frontRightEncoder = m_frontRight.getEncoder();
	private RelativeEncoder m_backLeftEncoder = m_backLeft.getEncoder();
	private RelativeEncoder m_backRightEncoder = m_backRight.getEncoder();

	// Kinematics
	private Translation2d m_frontLeftLocation = new Translation2d(0.381, 0.381);
	private Translation2d m_frontRightLocation = new Translation2d(0.381, -0.381);
	private Translation2d m_backLeftLocation = new Translation2d(-0.381, 0.381);
	private Translation2d m_backRightLocation = new Translation2d(-0.381, -0.381);
	private MecanumDriveKinematics m_kinematics = new MecanumDriveKinematics(
		m_frontLeftLocation,
		m_frontRightLocation,
		m_backLeftLocation,
		m_backRightLocation);
	private MecanumDriveOdometry m_odometry = new MecanumDriveOdometry(m_kinematics, Rotation2d.fromDegrees(0));

	// Systems
	private NTManager m_nt = NTManager.getInstance();
	private AHRS m_navX = new AHRS(Port.kMXP);

	public void reset() {
		m_backLeft.setOpenLoopRampRate(RAMP);
		m_backRight.setOpenLoopRampRate(RAMP);
		m_frontLeft.setOpenLoopRampRate(RAMP);
		m_frontRight.setOpenLoopRampRate(RAMP);
		m_frontLeft.setInverted(true);
    	m_backLeft.setInverted(true);
		m_navX.reset();
		m_odometry.resetPosition(new Pose2d(), new Rotation2d());
	}

	public MecanumDriveWheelSpeeds getWheelSpeeds() {
		return new MecanumDriveWheelSpeeds(
			m_frontLeftEncoder.getVelocity(),
			m_frontRightEncoder.getVelocity(),
			m_backLeftEncoder.getVelocity(),
			m_backRightEncoder.getVelocity());
	}
	

	@Override
	public void teleopInit() {
		reset();
	}

	@Override
	public void teleopPeriodic() {
		// Odometry
		Pose2d robotPose2d = m_odometry.update(Rotation2d.fromDegrees(m_navX.getAngle()), getWheelSpeeds());
		m_nt.autoField2d.setRobotPose(robotPose2d);

		// Control
		double xInput = m_driveController.getLeftX();
		double yInput = -m_driveController.getLeftY();
		double zInput = -m_driveController.getRightX();

		if (Math.abs(xInput) < DEADBAND)
			xInput = 0;
		if (Math.abs(yInput) < DEADBAND)
			yInput = 0;
		if (Math.abs(zInput) < DEADBAND)
			zInput = 0;
		
		m_backLeft.set(-xInput + yInput - zInput);
		m_backRight.set(xInput + yInput + zInput);
		m_frontLeft.set(xInput + yInput - zInput);
		m_frontRight.set(-xInput + yInput + zInput);		
	}
}
