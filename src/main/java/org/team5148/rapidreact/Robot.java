package org.team5148.rapidreact;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team5148.lib.Vector3;
import org.team5148.rapidreact.autonomous.AutoInput;
import org.team5148.rapidreact.autonomous.AutoManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;
import org.team5148.rapidreact.subsystem.BallLauncher;
import org.team5148.rapidreact.subsystem.BallStorage;
import org.team5148.rapidreact.subsystem.Climber;

public class Robot extends TimedRobot {

	private final double DEADBAND = 0.15;
	private final double RUMBLE = 0.2;
	private final double RAMP = 0.3;

	private boolean isFeeding = false;

	// Controllers
	private XboxController driveController = new XboxController(0);
	private XboxController manipController = new XboxController(1);

	// Motors
	private CANSparkMax backLeft = new CANSparkMax(MotorIDs.BACK_LEFT, MotorType.kBrushless);
	private CANSparkMax backRight = new CANSparkMax(MotorIDs.BACK_RIGHT, MotorType.kBrushless);
	private CANSparkMax frontLeft = new CANSparkMax(MotorIDs.FRONT_LEFT, MotorType.kBrushless);
	private CANSparkMax frontRight = new CANSparkMax(MotorIDs.FRONT_RIGHT, MotorType.kBrushless);

	// Subsystems
	private AutoManager autoManager = new AutoManager();
	private BallLauncher ballLauncher = new BallLauncher();
	private BallStorage ballStorage = new BallStorage();
	private Climber climber = new Climber();

	@Override
	public void robotInit() {
		frontLeft.setInverted(true);
		backLeft.setInverted(true);

		backLeft.setOpenLoopRampRate(RAMP);
		backRight.setOpenLoopRampRate(RAMP);
		frontLeft.setOpenLoopRampRate(RAMP);
		frontRight.setOpenLoopRampRate(RAMP);
	}

	@Override
	public void robotPeriodic() {
		
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
		autoManager.initAuto();
	}

	@Override
	public void autonomousPeriodic() {
		AutoInput input = autoManager.update();
		
		// Ball Storage / Launcher
		if (input.isShooting) {
			ballLauncher.run();

			boolean isRev = ballLauncher.getRev();
			if (isRev)
				isFeeding = true;
			if (isFeeding) {
				ballStorage.runFeed();
				ballStorage.runIntake();
				ballStorage.runStorage();
			} else {
				ballStorage.stopFeed();
				ballStorage.runIntake();
				ballStorage.stopStorage();
			}
		} else {
			ballLauncher.run(0);
			ballStorage.runIntake();
			ballStorage.runAutomatic();
			isFeeding = false;
		}
		
		// Movement
		double xInput = input.move.x;
		double yInput = input.move.y;
		double zInput = input.move.z;
		backLeft.set(-xInput + yInput - zInput);
		backRight.set(xInput + yInput + zInput);
		frontLeft.set(xInput + yInput - zInput);
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
		autoManager.initTeleop();
	}

	@Override
	public void teleopPeriodic() {
		autoManager.update();

		// Manipulator Input
		double climberInput = manipController.getRightY();
		boolean shootInput = manipController.getBButton();
		boolean intakeInput = manipController.getAButton();
		boolean outakeInput = manipController.getXButton();
		boolean storageInput = manipController.getYButton();
		boolean stopAutoInput = manipController.getRightBumper();
		double povInput = manipController.getPOV();
		boolean forceIntakeInput = povInput == 0;
		boolean forceOutakeInput = povInput == 180;

		// Driver Input
		boolean reverseCtrlInput = driveController.getLeftBumper();
		boolean slowCtrlInput = driveController.getRightBumper();
		boolean alignGoalInput = driveController.getAButton();
		boolean alignBallInput = driveController.getYButton();
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
		if (Math.abs(climberInput) < DEADBAND)
			climberInput = 0;
		
		// Reverse
		if (reverseCtrlInput) {
			xInput *= -1;
			yInput *= -1;
			//zInput *= -1;
		}

		// Tracking
		if (alignGoalInput) {
			Vector3 input = autoManager.alignToGoal();
			zInput = input.z;
		}
		if (alignBallInput) {
			Vector3 input = autoManager.alignToBall();
			zInput = input.z;
		}

		// Manip Rumble
		if (forceIntakeInput || forceOutakeInput) {
			manipController.setRumble(RumbleType.kLeftRumble, RUMBLE);
			manipController.setRumble(RumbleType.kRightRumble, RUMBLE);
		} else {
			manipController.setRumble(RumbleType.kLeftRumble, 0);
			manipController.setRumble(RumbleType.kRightRumble, 0);
		}

		// Ball Launcher
		if (shootInput)
			ballLauncher.run();
		else
			ballLauncher.stop();

		// Ball Storage
		if (forceIntakeInput) {
			ballStorage.runIntake(1);
			ballStorage.runStorage(1);
			ballStorage.runFeed(1);
		}
		else if (forceOutakeInput) {
			ballStorage.runIntake(-1);
			ballStorage.runStorage(-1);
			ballStorage.runFeed(-1);
		}
		else if (shootInput) {
			boolean isRev = ballLauncher.getRev();
			if (isRev)
				isFeeding = true;
			if (isFeeding) {
				ballStorage.runIntake();
				ballStorage.runStorage();
				ballStorage.runFeed();
			} else {
				ballStorage.stopIntake();
				ballStorage.stopStorage();
				ballStorage.stopFeed();
			}
		}
		else {
			// Storage
			if (storageInput)
				ballStorage.runStorage();
			else if (outakeInput)
				ballStorage.runStorageReverse();
			else if (stopAutoInput)
				ballStorage.stopStorage();
			else
				ballStorage.runAutomatic();

			// Intake
			if (intakeInput)
				ballStorage.runIntake();
			else if (outakeInput)
				ballStorage.runIntakeReverse();
			else
				ballStorage.stopIntake();
			
			// Feed
			if (outakeInput)
				ballStorage.runFeedReverse();
			else
				ballStorage.stopFeed();
			isFeeding = false;
		}

		// Climber
		climber.run(climberInput);

		// Drive Train
		double speed = slowCtrlInput ? DefaultSpeed.SLOW_DRIVE : DefaultSpeed.DRIVE;
		backLeft.set(speed * (-xInput + yInput - zInput));
		backRight.set(speed * (xInput + yInput + zInput));
		frontLeft.set(speed * (xInput + yInput - zInput));
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

	}
}
