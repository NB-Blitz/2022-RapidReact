package org.team5148.lib.sensors;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * Represents the Field Management System or FMS
 */
public class FMS {
    private static FMS m_instance;

    private FMS() {}

    /**
     * Gets the FMS singleton instance
     * @return FMS Singleton
     */
    public static FMS getInstance() {
        if (m_instance == null)
            m_instance = new FMS();
        return m_instance;
    }

    /**
     * Gets the current alliance color
     * @return Current alliance color
     */
    public Alliance getAlliance() {
        return DriverStation.getAlliance();
    }
}
