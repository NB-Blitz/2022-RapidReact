package com.team5148.rapidreact;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {

	private final double DEADBAND = 0.2;

	XboxController driveController = new XboxController(0);
	CANSparkMax backLeft = new CANSparkMax(2, MotorType.kBrushless);
	CANSparkMax backRight = new CANSparkMax(4, MotorType.kBrushless);
	CANSparkMax frontLeft = new CANSparkMax(1, MotorType.kBrushless);
	CANSparkMax frontRight = new CANSparkMax(3, MotorType.kBrushless);

	//Launcher ballLauncher = new Launcher();

	@Override
	public void robotInit() {

	}

	/*
	 * Autonomous
	 */
	@Override
	public void autonomousInit() {

	}

	@Override
	public void autonomousPeriodic() {

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
