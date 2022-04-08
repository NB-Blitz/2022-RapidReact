package org.team5148.lib.sensors;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;

/**
 * NavX Singleton Wrapper
 */
public class NavX {
    private AHRS m_ahrs;
    private static NavX m_instance;

    private NavX() {
        this.m_ahrs = new AHRS(SPI.Port.kMXP);
    }

    /**
     * Gets the NavX singleton instance
     * @return NavX Singleton
     */
    public static NavX getInstance() {
        if (m_instance == null)
            m_instance = new NavX();
        return m_instance;
    }

    /**
     * Gets the angle from the NavX as a Rotation2d
     * @return
     */
    public Rotation2d getAngle() {
        return Rotation2d.fromDegrees(m_ahrs.getAngle());
    }

    /**
     * Resets gyro angle to 0
     */
    public void reset() {
        reset(new Rotation2d());
    }

    /**
     * Resets gyro angle to set value
     * @param gyroAngle - Robot angle in degrees
     */
    public void reset(Rotation2d gyroAngle) {
        this.m_ahrs.reset();
        this.m_ahrs.setAngleAdjustment(gyroAngle.getDegrees());
    }
}
