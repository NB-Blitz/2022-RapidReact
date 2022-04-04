package org.team5148.rapidreact;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

import org.team5148.lib.auto.MecanumDriveOdom;
import org.team5148.lib.drivers.MecanumDrive;
import org.team5148.lib.drivers.NavX;
import org.team5148.lib.util.Vector3;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.LauncherTarget;
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

	// Kinematics
	Translation2d m_frontLeftLocation = new Translation2d(0.381, 0.381);
	Translation2d m_frontRightLocation = new Translation2d(0.381, -0.381);
	Translation2d m_backLeftLocation = new Translation2d(-0.381, 0.381);
	Translation2d m_backRightLocation = new Translation2d(-0.381, -0.381);
	MecanumDriveKinematics m_kinematics = new MecanumDriveKinematics(
		m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
	);

	// Motors
	private NavX navX = new NavX();
	private MecanumDrive mecanumDrive = new MecanumDrive(RAMP);
	private MecanumDriveOdom mecanumDriveOdom = new MecanumDriveOdom(mecanumDrive, m_kinematics);

	// Subsystems
	private BallLauncher ballLauncher = new BallLauncher();
	private BallStorage ballStorage = new BallStorage();
	private Climber climber = new Climber();

	@Override
	public void robotInit() {}

	@Override
	public void robotPeriodic() {}

	/*
                _                                              
     /\        | |                                             
    /  \  _   _| |_ ___  _ __   ___  _ __ ___   ___  _   _ ___ 
   / /\ \| | | | __/ _ \| '_ \ / _ \| '_ ` _ \ / _ \| | | / __|
  / ____ \ |_| | || (_) | | | | (_) | | | | | | (_) | |_| \__ \
 /_/    \_\__,_|\__\___/|_| |_|\___/|_| |_| |_|\___/ \__,_|___/
                                                                                                                    
	 */

	@Override
	public void autonomousInit() {}

	@Override
	public void autonomousPeriodic() {}

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
		climber.reset();
		navX.reset(0);
		mecanumDriveOdom.reset(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
	}

	@Override
	public void teleopPeriodic() {

		// Manipulator Input
		double climberLeftInput = manipController.getLeftY();
		double climberRightInput = manipController.getRightY();
		boolean forceClimbInput = manipController.getRightStickButton() || manipController.getLeftStickButton();
		boolean outakeInput = manipController.getLeftBumper();
		boolean intakeInput =  manipController.getRightBumper();
		boolean shootLowGoalInput = manipController.getYButton();
		boolean shootTarmacInput = manipController.getXButton();
		boolean shootFieldWallInput = manipController.getBButton();
		boolean shootLaunchpadInput = manipController.getAButton();
		boolean primeInput = manipController.getLeftTriggerAxis() > DEADBAND || manipController.getRightTriggerAxis() > DEADBAND;
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
		double zInput = DefaultSpeed.ROTATE * -driveController.getRightX();

		// Deadband
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
		mecanumDriveOdom.update(navX.getAngle());
		if (alignGoalInput) {
			// TODO: Align to Goal
		}
		if (alignBallInput) {
			// TODO: Align to Ball
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
		boolean isShooting = shootFieldWallInput || shootLaunchpadInput || shootLowGoalInput || shootTarmacInput;
		if (shootLowGoalInput)
			ballLauncher.run(LauncherTarget.LowGoal);
		else if (shootTarmacInput)
			ballLauncher.run(LauncherTarget.Tarmac);
		else if (shootLaunchpadInput)
			ballLauncher.run(LauncherTarget.Launchpad);
		else if (shootFieldWallInput)
			ballLauncher.run(LauncherTarget.FieldWall);
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
		double speed = slowCtrlInput ? DefaultSpeed.SLOW_DRIVE : DefaultSpeed.DRIVE;
		mecanumDrive.drive(new Vector3(
			xInput * speed,
			yInput * speed,
			zInput * speed
		));	
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
	public void testInit() {}

	@Override
	public void testPeriodic() {}
}
