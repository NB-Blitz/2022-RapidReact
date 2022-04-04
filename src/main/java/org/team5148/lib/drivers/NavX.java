package org.team5148.lib.drivers;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;

/**
 * NavX wrapper that caches RoboRIO kMXP calls
 */
public class NavX {
    private AHRS m_navx;

    public NavX() {
        this.m_navx = new AHRS(SPI.Port.kMXP);
    }

    /**
     * Gets the angle from the NavX as a Rotation2d
     * @return
     */
    public Rotation2d getAngle() {
        return Rotation2d.fromDegrees(m_navx.getAngle());
    }

    /**
     * Resets gyro angle to set value
     * @param pm_robotAngle - Robot angle in degrees
     */
    public void reset(double pm_robotAngle) {
        this.m_navx.reset();
        this.m_navx.setAngleAdjustment(pm_robotAngle);
    }
}
