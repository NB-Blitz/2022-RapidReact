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
import org.team5148.rapidreact.config.LauncherTarget;
import org.team5148.rapidreact.config.MotorIDs;
import org.team5148.rapidreact.subsystem.BallLauncher;
import org.team5148.rapidreact.subsystem.BallStorage;
import org.team5148.rapidreact.subsystem.Climber;

public class Robot extends TimedRobot {

	private final double DEADBAND = 0.2;
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

	public void initDrivetrain() {
		backLeft.setOpenLoopRampRate(RAMP);
		backRight.setOpenLoopRampRate(RAMP);
		frontLeft.setOpenLoopRampRate(RAMP);
		frontRight.setOpenLoopRampRate(RAMP);
	}

	@Override
	public void robotInit() {
		initDrivetrain();
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
		initDrivetrain();
		autoManager.initAuto();
	}

	@Override
	public void autonomousPeriodic() {
		AutoInput input = autoManager.update();
		
		// Ball Storage / Launcher
		if (input.isShooting) {
			ballLauncher.run(LauncherTarget.Tarmac);

			boolean isRev = ballLauncher.getRev();
			if (isRev)
				isFeeding = true;
			if (isFeeding) {
				ballStorage.runFeed();
				ballStorage.stopIntake();
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
		initDrivetrain();
		autoManager.initTeleop();
		climber.reset();
	}

	@Override
	public void teleopPeriodic() {
		autoManager.update();

		// Manipulator Input
		double climberLeftInput = manipController.getLeftY();
		double climberRightInput = manipController.getRightY();
		boolean forceClimbInput = manipController.getRightStickButton() || manipController.getLeftStickButton();
		boolean outakeInput = manipController.getLeftBumper();
		boolean intakeInput =  manipController.getRightBumper();
		boolean shootLowGoalInput = manipController.getYButton();
		boolean shootTarmacInput = manipController.getXButton();
		boolean shootAutoDistInput = manipController.getBButton();
		boolean shootLaunchpadInput = manipController.getAButton();
		boolean primeInput = manipController.getLeftTriggerAxis() > DEADBAND || manipController.getRightTriggerAxis() > DEADBAND;
		double povInput = manipController.getPOV();
		boolean forceIntakeInput = povInput == 0;
		boolean forceOutakeInput = povInput == 180;

		// Driver Input
		boolean reverseCtrlInput = driveController.getLeftBumper();
		boolean slowCtrlInput = driveController.getRightBumper();
		boolean alignGoalInput = driveController.getAButton();
		double xInput = driveController.getLeftX();
		double yInput = -driveController.getLeftY();
		double zInput = DefaultSpeed.ROTATE * -driveController.getRightX();

		// Deadband
		boolean isShooting = shootAutoDistInput || shootLaunchpadInput || shootLowGoalInput || shootTarmacInput;
		double speed = (slowCtrlInput || isShooting) ? DefaultSpeed.SLOW_DRIVE : DefaultSpeed.DRIVE;
		if (Math.abs(xInput) < DEADBAND)
			xInput = 0;
		if (Math.abs(yInput) < DEADBAND)
			yInput = 0;
		if (Math.abs(zInput) < DEADBAND)
			zInput = 0;
		if (Math.abs(climberLeftInput) < DEADBAND)
			climberLeftInput = 0;
		if (Math.abs(climberRightInput) < DEADBAND)
			climberRightInput = 0;
		
		// Reverse
		if (reverseCtrlInput) {
			xInput *= -1;
			yInput *= -1;
		}

		// Tracking
		if (alignGoalInput) {
			Vector3 input = autoManager.alignToGoal();
			zInput = input.z / speed;
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
		if (shootLowGoalInput)
			ballLauncher.run(LauncherTarget.LowGoal);
		else if (shootTarmacInput)
			ballLauncher.run(LauncherTarget.Tarmac);
		else if (shootLaunchpadInput)
			ballLauncher.run(LauncherTarget.Launchpad);
		else if (shootAutoDistInput)
			ballLauncher.runAuto(autoManager.getGoalDistance());
		else
			ballLauncher.stop();

		// Ball Storage
		if (forceIntakeInput) {
			ballStorage.runIntake(1);
			ballStorage.runStorage(1, 1);
			ballStorage.runFeed(1);
		}
		else if (forceOutakeInput) {
			ballStorage.runIntake(-1);
			ballStorage.runStorage(-1, -1);
			ballStorage.runFeed(-1);
		}
		else if (isShooting) {
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
			// Shooter
			if (primeInput)
				ballLauncher.run(LauncherTarget.Tarmac);
			else
				ballLauncher.stop();

			// Storage
			if (outakeInput)
				ballStorage.runStorageReverse();
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
		if (forceClimbInput) {
			climber.forceRunLeft(climberLeftInput);
			climber.forceRunRight(climberRightInput);
		} else {
			climber.runLeft(climberLeftInput);
			climber.runRight(climberRightInput);
		}

		// Drive Train
		backLeft.set(-speed * (-xInput + yInput - zInput));
		backRight.set(speed * (xInput + yInput + zInput));
		frontLeft.set(-speed * (xInput + yInput - zInput));
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
		autoManager.getGoalDistance();
	}
}
