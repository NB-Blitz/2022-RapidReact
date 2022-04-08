package org.team5148.rapidreact;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * Network Tables Singleton
 */
public class NTManager {
    public static NTManager instance;

    // Autonomous
    public ShuffleboardTab autoTab = Shuffleboard.getTab("Autonomous");
    public NetworkTableEntry autoAbort = autoTab.add("Is Aborted", false).getEntry();

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
