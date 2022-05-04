package org.team5148.rapidreact;

import org.team5148.rapidreact.autonomous.AutoManager;
import org.team5148.rapidreact.config.DefaultSpeed;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

/**
 * Network Tables Singleton
 */
public class NTManager {
    public static NTManager instance;

    // Autonomous
    public ShuffleboardTab autoTab = Shuffleboard.getTab("Autonomous");
    public NetworkTableEntry autoAbort = autoTab.add("Is Aborted", false).getEntry();
    public NetworkTableEntry autoGyro = autoTab.add("Gyro Angle", 0).getEntry();
    public NetworkTableEntry autoGoalAngle = autoTab.add("Goal Angle", 0).getEntry();
    public NetworkTableEntry autoGoalDistance = autoTab.add("Goal Distance", 0).getEntry();
    public NetworkTableEntry autoBallAngle = autoTab.add("Ball Angle", 0).getEntry();
    public NetworkTableEntry autoAccel = autoTab.add("Acceleration", 0).getEntry();
    public NetworkTableEntry autoMode = autoTab.add("Mode", AutoManager.DEFAULT_MODE).getEntry();
    public NetworkTableEntry autoStep = autoTab.add("Step", 0).getEntry();
    public NetworkTableEntry autoXInput = autoTab.add("X Input", 0).getEntry();
    public NetworkTableEntry autoYInput = autoTab.add("Y Input", 0).getEntry();
    public NetworkTableEntry autoZInput = autoTab.add("Z Input", 0).getEntry();

    // Launcher
    public ShuffleboardTab launcherTab = Shuffleboard.getTab("Launcher");
    public NetworkTableEntry launcherSetVel = launcherTab.add("Set Velocity", 0).getEntry();
    public NetworkTableEntry launcherRoll = launcherTab.add("Roll Velocity", DefaultSpeed.ROLL_VELOCITY).getEntry();
    public NetworkTableEntry launcherRev = launcherTab.add("Is Rev", false).getEntry();

    // Storage
    public ShuffleboardTab storageTab = Shuffleboard.getTab("Storage");
    public NetworkTableEntry lineBreak1 = storageTab.add("Line Break 1", false).getEntry();
    public NetworkTableEntry lineBreak2 = storageTab.add("Line Break 2", false).getEntry();
    public NetworkTableEntry leftStorageSpeed = storageTab.add("Left Storage Speed", DefaultSpeed.LEFT_STORAGE).getEntry();
    public NetworkTableEntry rightStorageSpeed = storageTab.add("Right Storage Speed", DefaultSpeed.RIGHT_STORAGE).getEntry();
    public NetworkTableEntry intakeSpeed = storageTab.add("Intake Speed", DefaultSpeed.INTAKE).getEntry();
    public NetworkTableEntry feedSpeed = storageTab.add("Feed Speed", DefaultSpeed.FEED).getEntry();
    public NetworkTableEntry outakeSpeed = storageTab.add("Outake Speed", DefaultSpeed.OUTAKE).getEntry();

    // Climber
    public ShuffleboardTab climberTab = Shuffleboard.getTab("Climber");
    public NetworkTableEntry climberSpeed = climberTab.add("Speed", DefaultSpeed.CLIMBER).getEntry();
    public NetworkTableEntry climberLeftPos = climberTab.add("Left Position", 0).getEntry();
    public NetworkTableEntry climberRightPos = climberTab.add("Right Position", 0).getEntry();

    // Simulation
    public ShuffleboardTab simTab = Shuffleboard.getTab("Simulation");
    public Field2d simField = new Field2d();
    public ComplexWidget simFieldEntry = simTab.add("Field", simField);

    /**
     * Gets the current instance of NTManager
     * @return Current NTManager Singleton
     */
    public static NTManager getInstance() {
        if (instance == null)
            instance = new NTManager();
        return instance;
    }
}
