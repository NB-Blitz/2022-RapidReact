package com.nbblitz.rapidreact;

import com.nbblitz.lib.control.XboxController;
import com.nbblitz.lib.drive.Mecanum;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {

	Mecanum drivetrain;
	XboxController driveController;

	@Override
	public void robotInit() {
		drivetrain = new Mecanum();
		driveController = new XboxController(0);
	}

	/*
		Autonomous
	*/
	@Override
	public void autonomousInit() {
		
	}

	@Override
	public void autonomousPeriodic() {

	}

	/*
		Teleoperated
	*/
	@Override
	public void teleopInit() {

	}

	@Override
	public void teleopPeriodic() {
		double x = driveController.LeftXAxis.get();
		double y = driveController.LeftYAxis.get();
		double z = driveController.RightXAxis.get();

		drivetrain.drive(x, y, z);
	}

	/*
		Test
	*/
	@Override
	public void testInit() {
		
	}

	@Override
	public void testPeriodic() {

	}
}
