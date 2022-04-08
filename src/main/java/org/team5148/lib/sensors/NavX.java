package org.team5148.lib.sensors;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;

/**
 * NavX wrapper
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
        this.m_navx.reset();
        this.m_navx.setAngleAdjustment(gyroAngle.getDegrees());
    }
}
