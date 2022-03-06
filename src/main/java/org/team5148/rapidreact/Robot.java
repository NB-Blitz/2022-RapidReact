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
	private Simulation sim = new Simulation();

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
		autoManager.reset();
		sim.reset();
	}

	@Override
	public void autonomousPeriodic() {
		AutoInput input = autoManager.update();
		
		// Ball Storage / Launcher
		if (input.isShooting) {
			ballLauncher.runVelocity(true);

			boolean isRev = ballLauncher.getRev();
			ballStorage.runFeed(isRev);
			ballStorage.runIntake(isRev);
			ballStorage.runStorage(isRev);
		} else {
			ballLauncher.runVelocity(0);
			ballStorage.runAutomatic();
		}
		
		// Movement
		double xInput = input.move.x;
		double yInput = input.move.y;
		double zInput = input.move.z;
		backLeft.set(-xInput + yInput - zInput);
		backRight.set(xInput + yInput + zInput);
		frontLeft.set(xInput + yInput - zInput);
		frontRight.set(-xInput + yInput + zInput);

		// Simulation
		sim.drive(input.move);
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
		sim.reset();
	}

	@Override
	public void teleopPeriodic() {
		autoManager.update();

		// Manipulator Input
		double climberInput = manipController.getRightY();
		boolean revDigitalInput = manipController.getLeftBumper() || manipController.getRightBumper();
		boolean shootInput = manipController.getAButton() || manipController.getBButton();
		boolean stopAutoInput = manipController.getXButton() || manipController.getYButton();
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
		if (Math.abs(climberInput) < DEADBAND)
			climberInput = 0;

		// Tracking
		if (alignGoalInput) {
			driveController.setRumble(RumbleType.kLeftRumble, RUMBLE);
			Vector3 input = autoManager.rotateToGoal();
			zInput = input.z;
		}
		else if (alignBallInput) {
			driveController.setRumble(RumbleType.kRightRumble, RUMBLE);
			Vector3 input = autoManager.rotateToBall();
			zInput = input.z;
		}
		else {
			driveController.setRumble(RumbleType.kLeftRumble, 0);
			driveController.setRumble(RumbleType.kRightRumble, 0);
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
		if (revDigitalInput || shootInput)
			ballLauncher.runVelocity(true);
		else
			ballLauncher.runVelocity(false);

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
			ballStorage.runIntake(isFeeding);
			ballStorage.runStorage(isFeeding);
			ballStorage.runFeed(isFeeding);
		}
		else if (stopAutoInput) {
			ballStorage.runStorage(false);
			ballStorage.runFeed(false);
			ballStorage.runIntake(false);
		}
		else {
			ballStorage.runAutomatic();
			isFeeding = false;
		}

		// Climber
		climber.run(climberInput);

		// Drive Train
		double speed = slowInput ? DefaultSpeed.SLOW_DRIVE : DefaultSpeed.DRIVE;
		backLeft.set(speed * (-xInput + yInput - zInput));
		backRight.set(speed * (xInput + yInput + zInput));
		frontLeft.set(speed * (xInput + yInput - zInput));
		frontRight.set(speed * (-xInput + yInput + zInput));		

		// Simulation
		sim.drive(new Vector3(xInput, yInput, zInput));
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
