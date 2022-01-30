package com.team5148.rapidreact;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {

	private final double DEADBAND = 0.2;

	// Controllers
	XboxController driveController = new XboxController(0);
	XboxController manipController = new XboxController(1);

	// Motos
	CANSparkMax backLeft = new CANSparkMax(2, MotorType.kBrushless);
	CANSparkMax backRight = new CANSparkMax(4, MotorType.kBrushless);
	CANSparkMax frontLeft = new CANSparkMax(1, MotorType.kBrushless);
	CANSparkMax frontRight = new CANSparkMax(3, MotorType.kBrushless);

	// Subsystems
	AutoManager autoManager = new AutoManager();
	BallLauncher ballLauncher = new BallLauncher();
	BallStorage ballStorage = new BallStorage();

	@Override
	public void robotInit() {

	}

	/*
	 * Autonomous
	 */
	@Override
	public void autonomousInit() {
		autoManager.reset();
	}

	@Override
	public void autonomousPeriodic() {
		autoManager.processAutonomous();

		boolean isIntaking = autoManager.isIntaking;
		boolean isStoraging = autoManager.isStoraging;
		boolean isLaunching = autoManager.isLaunching;

		ballStorage.runIntake(isIntaking);
		ballStorage.runStorage(isStoraging);
		ballLauncher.runLauncher(isLaunching);

		double xInput = autoManager.xInput;
		double yInput = autoManager.yInput;
		double zInput = autoManager.zInput;

		backLeft.set(-(-xInput + yInput - zInput));
		backRight.set(xInput + yInput + zInput);
		frontLeft.set(-(xInput + yInput - zInput));
		frontRight.set(-xInput + yInput + zInput);
	}

	/*
	 * Teleoperated
	 */
	@Override
	public void teleopInit() {

	}

	@Override
	public void teleopPeriodic() {

		// Inputs
		double launcherInput = driveController.getRightTriggerAxis();
		double intakeInput = driveController.getLeftTriggerAxis();
		boolean storageInput = driveController.getXButton();
		boolean trackGoalInput = driveController.getAButton();

		double xInput = driveController.getLeftX();
		double yInput = -driveController.getLeftY();
		double zInput = -driveController.getRightX();

		// Deadband
		if (Math.abs(xInput) < DEADBAND)
			xInput = 0;
		if (Math.abs(yInput) < DEADBAND)
			yInput = 0;
		if (Math.abs(zInput) < DEADBAND)
			zInput = 0;

		// Track Goal
		if (trackGoalInput) {
			autoManager.trackGoal();
			zInput = autoManager.zInput;
		}

		// Sub-Systems
		ballLauncher.runLauncher(launcherInput);
		ballStorage.runIntake(intakeInput);
		ballStorage.runStorage(storageInput);

		// Drivetrain
		backLeft.set(-(-xInput + yInput - zInput));
		backRight.set(xInput + yInput + zInput);
		frontLeft.set(-(xInput + yInput - zInput));
		frontRight.set(-xInput + yInput + zInput);		
	}
}
