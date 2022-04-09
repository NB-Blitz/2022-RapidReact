package org.team5148.lib.controllers;

import org.team5148.lib.util.BlitzMath;
import org.team5148.lib.util.Vector3;

/**
 * Represents a 2-axis joystick connected to the driverstation
 */
public class Joystick extends edu.wpi.first.wpilibj.Joystick {
    private double m_deadband = 0;

    /**
     * Initializes a 2-axis joystick
     * @param id - Port # of the controller
     */
    public Joystick(int id) {
        super(id);
    }

    /**
     * Initializes a 2-axis Joystick with deadband
     * @param id - Port # of the controller
     * @param deadband - Deadband of the controller [0 - 1]
     */
    public Joystick(int id, double deadband) {
        super(id);
        this.m_deadband = deadband;
    }

    /**
     * Sets the controller deadband
     * @param deadband - Controller deadband [0 - 1]
     */
    public void setDeadband(double deadband) {
        this.m_deadband = deadband;
    }

    @Override
    public double getRawAxis(int axisID) {
        return BlitzMath.applyDeadband(m_deadband, super.getRawAxis(axisID));
    }

    /**
     * Gets the joystick as a Vector3.
     * If it is a 2-axis joystick then Z = 0.
     * @return Vector3 of the joystick
     */
    public Vector3 getJoystick() {
        return new Vector3(
            getX(),
            getY(),
            getZ()
        );
    }
}
