package com.team5148.rapidreact;

import com.team5148.rapidreact.config.DefaultSpeed;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * Network Tables Singleton
 */
public class NTManager {
    public static NTManager instance;

    // Autonomous
    public ShuffleboardTab storageTab = Shuffleboard.getTab("Storage");
    public NetworkTableEntry lineBreak1 = storageTab.add("Line Break 1", false).getEntry();
    public NetworkTableEntry lineBreak2 = storageTab.add("Line Break 2", false).getEntry();
    public NetworkTableEntry storageSpeed = storageTab.add("Storage Speed", DefaultSpeed.STORAGE).getEntry();
    public NetworkTableEntry intakeSpeed = storageTab.add("Intake Speed", DefaultSpeed.INTAKE).getEntry();
    public NetworkTableEntry feedSpeed = storageTab.add("Feed Speed", DefaultSpeed.FEED).getEntry();

    // Launcher
    public ShuffleboardTab launcherTab = Shuffleboard.getTab("Launcher");
    public NetworkTableEntry launcherSetVel = launcherTab.add("Set Velocity", DefaultSpeed.LAUNCHER_VELOCITY).getEntry();
    public NetworkTableEntry launcherRoll = launcherTab.add("Roll", DefaultSpeed.ROLL).getEntry();

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
