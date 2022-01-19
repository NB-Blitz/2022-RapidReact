package com.team5148.rapidreact;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.I2C.Port;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {

	private final double DEADBAND = 0.2;
	private final double LAUNCHER_SPEED = 0.5;

	// Controllers
	XboxController driveController = new XboxController(0);
	XboxController manipController = new XboxController(1);

	// Motos
	CANSparkMax backLeft = new CANSparkMax(2, MotorType.kBrushless);
	CANSparkMax backRight = new CANSparkMax(4, MotorType.kBrushless);
	CANSparkMax frontLeft = new CANSparkMax(1, MotorType.kBrushless);
	CANSparkMax frontRight = new CANSparkMax(3, MotorType.kBrushless);

	// Sensors
	

	// Subsystems
	AutoManager autoManager = new AutoManager();
	//BallLauncher ballLauncher = new BallLauncher();
	//BallStorage ballStorage = new BallStorage();

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
		autoManager.calculate();

		double leftSpeed = autoManager.leftSpeed;
		double rightSpeed = autoManager.rightSpeed;

		frontLeft.set(-leftSpeed);
		backLeft.set(-leftSpeed);
		frontRight.set(rightSpeed);
		backRight.set(rightSpeed);
	}

	/*
	 * Teleoperated
	 */
	@Override
	public void teleopInit() {

	}

	@Override
	public void teleopPeriodic() {
		double xInput = driveController.getLeftX();
		double yInput = -driveController.getLeftY();
		double zInput = -driveController.getRightX();

		if (Math.abs(xInput) < DEADBAND) {
			xInput = 0;
		}
		if (Math.abs(yInput) < DEADBAND) {
			yInput = 0;
		}
		if (Math.abs(zInput) < DEADBAND) {
			zInput = 0;
		}

		backLeft.set(-(-xInput + yInput - zInput));
		backRight.set(xInput + yInput + zInput);
		frontLeft.set(-(xInput + yInput - zInput));
		frontRight.set(-xInput + yInput + zInput);

		//ballLauncher.rev(0.8);
	}
}
