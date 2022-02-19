package com.team5148.rapidreact;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team5148.rapidreact.config.DefaultSpeed;
import com.team5148.rapidreact.config.MotorIDs;

public class Robot extends TimedRobot {

	private final double DEADBAND = 0.2;
	private final double RUMBLE = 0.2;

	// Controllers
	XboxController driveController = new XboxController(0);
	XboxController manipController = new XboxController(1);

	// Motos
	CANSparkMax backLeft = new CANSparkMax(MotorIDs.BACK_LEFT, MotorType.kBrushless);
	CANSparkMax backRight = new CANSparkMax(MotorIDs.BACK_RIGHT, MotorType.kBrushless);
	CANSparkMax frontLeft = new CANSparkMax(MotorIDs.FRONT_LEFT, MotorType.kBrushless);
	CANSparkMax frontRight = new CANSparkMax(MotorIDs.FRONT_RIGHT, MotorType.kBrushless);

	// Subsystems
	AutoManager autoManager = new AutoManager();
	BallLauncher ballLauncher = new BallLauncher();
	BallStorage ballStorage = new BallStorage();

	@Override
	public void robotInit() {

	}

	/*
                _                                              
     /\        | |                                             
    /  \  _   _| |_ ___  _ __   ___  _ __ ___   ___  _   _ ___ 
   / /\ \| | | | __/ _ \| '_ \ / _ \| '_ ` _ \ / _ \| | | / __|
  / ____ \ |_| | || (_) | | | | (_) | | | | | | (_) | |_| \__ \
 /_/    \_\__,_|\__\___/|_| |_|\___/|_| |_| |_|\___/ \__,_|___/
                                                                                                                    
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
  _______   _                                 _           _ 
 |__   __| | |                               | |         | |
    | | ___| | ___  ___  _ __   ___ _ __ __ _| |_ ___  __| |
    | |/ _ \ |/ _ \/ _ \| '_ \ / _ \ '__/ _` | __/ _ \/ _` |
    | |  __/ |  __/ (_) | |_) |  __/ | | (_| | ||  __/ (_| |
    |_|\___|_|\___|\___/| .__/ \___|_|  \__,_|\__\___|\__,_|
                        | |                                 
                        |_|                                 
	 */
	@Override
	public void teleopInit() {

	}

	@Override
	public void teleopPeriodic() {

		// Inputs
		double revAnalogInput = Math.max(manipController.getLeftTriggerAxis(), manipController.getRightTriggerAxis());
		boolean revDigitalInput = manipController.getLeftBumper() || manipController.getRightBumper();
		boolean shootInput = manipController.getAButton() || manipController.getBButton();
		boolean trackBallInput = manipController.getXButton();
		boolean trackGoalInput = manipController.getYButton();
		double povInput = manipController.getPOV();
		boolean forceIntakeInput = povInput == 0;
		boolean forceOutakeInput = povInput == 180;

		double xInput = driveController.getLeftX();
		double yInput = driveController.getLeftY();
		double zInput = driveController.getRightX();

		// Deadband
		if (Math.abs(xInput) < DEADBAND)
			xInput = 0;
		if (Math.abs(yInput) < DEADBAND)
			yInput = 0;
		if (Math.abs(zInput) < DEADBAND)
			zInput = 0;

		// Tracking
		if (trackGoalInput) {
			autoManager.trackGoal();
			zInput = autoManager.zInput;
		}
		if (trackBallInput) {
			autoManager.trackBall();
			zInput = autoManager.zInput;
		}

		// Ball Launcher
		if (revDigitalInput)
			ballLauncher.runLauncher(true);
		else
			ballLauncher.runLauncher(revAnalogInput);

		// Ball Storage
		if (forceIntakeInput) {
			manipController.setRumble(RumbleType.kRightRumble, RUMBLE);
			ballStorage.runIntake(1);
			ballStorage.runStorage(1);
			ballStorage.runFeed(1);
		}
		else if (forceOutakeInput) {
			manipController.setRumble(RumbleType.kLeftRumble, RUMBLE);
			ballStorage.runIntake(-1);
			ballStorage.runStorage(-1);
			ballStorage.runFeed(-1);
		}
		else if (shootInput) {
			ballStorage.runIntake(true);
			ballStorage.runStorage(true);
			ballStorage.runFeed(true);
		}
		else {
			ballStorage.runAuto();
		}

		// Drive Train
		backLeft.set(-(-xInput + yInput - zInput));
		backRight.set(xInput + yInput + zInput);
		frontLeft.set(-(xInput + yInput - zInput));
		frontRight.set(-xInput + yInput + zInput);		
	}

	/*
  _______        _   
 |__   __|      | |  
    | | ___  ___| |_ 
    | |/ _ \/ __| __|
    | |  __/\__ \ |_ 
    |_|\___||___/\__|
                     
	*/
	@Override
	public void testInit() {

	}

	@Override
	public void testPeriodic() {

		// Inputs
		double revAnalogInput = Math.max(driveController.getLeftTriggerAxis(), driveController.getRightTriggerAxis());
		boolean revDigitalInput = driveController.getLeftBumper() || driveController.getRightBumper();
		boolean intakeInput = driveController.getAButton();
		boolean storageInput = driveController.getBButton();
		boolean feedInput = driveController.getXButton() || driveController.getYButton();

		double xInput = -driveController.getLeftX();
		double yInput = -driveController.getLeftY();
		double zInput = -driveController.getRightX();

		// Deadband
		if (Math.abs(xInput) < DEADBAND)
			xInput = 0;
		if (Math.abs(yInput) < DEADBAND)
			yInput = 0;
		if (Math.abs(zInput) < DEADBAND)
			zInput = 0;
		if (Math.abs(revAnalogInput) < DEADBAND)
			revAnalogInput = 0;
		
		// Ball Launcher
		if (revDigitalInput)
			ballLauncher.runLauncher(true);
		else
			ballLauncher.runLauncher(revAnalogInput);

		// Ball Storage
		ballStorage.runStorage(storageInput);
		ballStorage.runIntake(intakeInput);
		ballStorage.runFeed(feedInput);

		// Drivetrain
		backLeft.set(-(-xInput + yInput - zInput));
		backRight.set(xInput + yInput + zInput);
		frontLeft.set(-(xInput + yInput - zInput));
		frontRight.set(-xInput + yInput + zInput);
	}
}
