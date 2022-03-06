package org.team5148.rapidreact;

import org.team5148.rapidreact.config.DefaultSpeed;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
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
    public NetworkTableEntry autoBallAngle = autoTab.add("Ball Angle", 0).getEntry();
    public NetworkTableEntry autoGoalAngle = autoTab.add("Goal Angle", 0).getEntry();
    public NetworkTableEntry autoAccel = autoTab.add("Acceleration", 0).getEntry();
    public NetworkTableEntry autoMode = autoTab.add("Mode", 0).getEntry();
    public NetworkTableEntry autoStep = autoTab.add("Step", 0).getEntry();
    public NetworkTableEntry autoXInput = autoTab.add("X Input", 0).getEntry();
    public NetworkTableEntry autoYInput = autoTab.add("Y Input", 0).getEntry();
    public NetworkTableEntry autoZInput = autoTab.add("Z Input", 0).getEntry();

    // Ball
    public NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    public NetworkTable ballTable = ntinst.getTable("Ball Cam");
    public NetworkTableEntry ballVisible = ballTable.getEntry("Is Visible");
    public NetworkTableEntry ballX = ballTable.getEntry("X Position");
    public NetworkTableEntry ballY = ballTable.getEntry("Y Position");
    public NetworkTableEntry ballArea = ballTable.getEntry("Area");

    // Goal
    public NetworkTable goalTable = ntinst.getTable("Goal Cam");
    public NetworkTableEntry goalVisible = goalTable.getEntry("Is Visible");
    public NetworkTableEntry goalX = goalTable.getEntry("X Position");
    public NetworkTableEntry goalY = goalTable.getEntry("Y Position");
    public NetworkTableEntry goalArea = goalTable.getEntry("Area");

    // Launcher
    public ShuffleboardTab launcherTab = Shuffleboard.getTab("Launcher");
    public NetworkTableEntry launcherSetVel = launcherTab.add("Set Velocity", DefaultSpeed.LAUNCHER_VELOCITY).getEntry();
    public NetworkTableEntry launcherRoll = launcherTab.add("Roll Velocity", DefaultSpeed.ROLL_VELOCITY).getEntry();

    // Storage
    public ShuffleboardTab storageTab = Shuffleboard.getTab("Storage");
    public NetworkTableEntry lineBreak1 = storageTab.add("Line Break 1", false).getEntry();
    public NetworkTableEntry lineBreak2 = storageTab.add("Line Break 2", false).getEntry();
    public NetworkTableEntry storageSpeed = storageTab.add("Storage Speed", DefaultSpeed.STORAGE).getEntry();
    public NetworkTableEntry intakeSpeed = storageTab.add("Intake Speed", DefaultSpeed.INTAKE).getEntry();
    public NetworkTableEntry feedSpeed = storageTab.add("Feed Speed", DefaultSpeed.FEED).getEntry();

    // Odometry
    public Field2d autoField = new Field2d();
    public ComplexWidget autoFieldEntry = autoTab.add("Field", autoField);
    public NetworkTableEntry autoXPos = autoTab.add("X Position", 0).getEntry();
    public NetworkTableEntry autoYPos = autoTab.add("Y Position", 0).getEntry();

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
