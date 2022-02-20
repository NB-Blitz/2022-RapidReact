package com.team5148.rapidreact;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.team5148.rapidreact.config.DefaultSpeed;
import com.team5148.rapidreact.config.MotorIDs;

public class Robot extends TimedRobot {

	private final double DEADBAND = 0.15;
	private final double RUMBLE = 0.2;
	private final double RAMP = 0.4;

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
		boolean isFeeding = autoManager.isFeeding;
		boolean isLaunching = autoManager.isLaunching;

		ballStorage.runIntake(isIntaking);
		ballStorage.runStorage(isStoraging);
		ballStorage.runFeed(isFeeding);
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
		backLeft.setOpenLoopRampRate(RAMP);
		backRight.setOpenLoopRampRate(RAMP);
		frontLeft.setOpenLoopRampRate(RAMP);
		frontRight.setOpenLoopRampRate(RAMP);
	}

	@Override
	public void teleopPeriodic() {

		// Manipulator Input
		double revAnalogInput = Math.max(manipController.getLeftTriggerAxis(), manipController.getRightTriggerAxis());
		boolean revDigitalInput = manipController.getLeftBumper() || manipController.getRightBumper();
		boolean shootInput = manipController.getYButton() || manipController.getBButton();
		boolean intakeInput = manipController.getXButton() || manipController.getAButton();
		double povInput = manipController.getPOV();
		boolean forceIntakeInput = povInput == 0;
		boolean forceOutakeInput = povInput == 180;

		// Driver Input
		boolean slowInput = driveController.getLeftBumper() || driveController.getRightBumper();
		boolean alignBallInput = driveController.getXButton() || driveController.getAButton();
		boolean alignGoalInput = driveController.getYButton() || driveController.getBButton();
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
		if (Math.abs(revAnalogInput) < DEADBAND)
			revAnalogInput = 0;

		// Tracking
		if (alignGoalInput) {
			manipController.setRumble(RumbleType.kLeftRumble, RUMBLE);

			// TODO: Test/Run Vision Code
			//autoManager.trackGoal();
			//zInput = autoManager.zInput;
		}
		else if (alignBallInput) {
			manipController.setRumble(RumbleType.kRightRumble, RUMBLE);

			// TODO: Test/Run Vision Code
			//autoManager.trackBall();
			//zInput = autoManager.zInput;
		}
		else {
			manipController.setRumble(RumbleType.kLeftRumble, 0);
			manipController.setRumble(RumbleType.kRightRumble, 0);
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
			//ballStorage.runAuto();
			ballStorage.runIntake(intakeInput);
			ballStorage.runFeed(false);
			ballStorage.runStorage(false);
		}

		// Drive Train
		double speed = slowInput ? DefaultSpeed.SLOW_DRIVE : DefaultSpeed.DRIVE;
		backLeft.set(speed * (-(-xInput + yInput - zInput)));
		backRight.set(speed * (xInput + yInput + zInput));
		frontLeft.set(speed * (-(xInput + yInput - zInput)));
		frontRight.set(speed * (-xInput + yInput + zInput));		
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

		double leftInput = driveController.getLeftY();
		double rightInput = -driveController.getRightY();

		// Deadband
		if (Math.abs(leftInput) < DEADBAND)
			leftInput = 0;
		if (Math.abs(rightInput) < DEADBAND)
			rightInput = 0;
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
		frontLeft.set(leftInput);
		backLeft.set(leftInput);
		frontRight.set(rightInput);
		backRight.set(rightInput);

	}
}
