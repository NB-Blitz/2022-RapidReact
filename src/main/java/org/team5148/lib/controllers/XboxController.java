package org.team5148.lib.controllers;

import org.team5148.lib.util.BlitzMath;
import org.team5148.lib.util.Vector2;

/**
 * Represents an Xbox Controller connected to the DriverStation
 */
public class XboxController extends edu.wpi.first.wpilibj.XboxController {
    private double m_deadband = 0;

    /**
     * Initializes an Xbox Controller
     * @param id - Port # of the controller
     */
    public XboxController(int id) {
        super(id);
    }

    /**
     * Initializes an Xbox Controller with deadband
     * @param id - Port # of the controller
     * @param deadband - Deadband of the controller [0 - 1]
     */
    public XboxController(int id, double deadband) {
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
     * Gets the left joystick position
     * @return Left joystick as a Vector2
     */
    public Vector2 getLeftJoystick() {
        return new Vector2(
            getLeftX(),
            getLeftY()
        );
    }

    /**
     * Gets the right joystick position
     * @return Right joystick as a Vector2
     */
    public Vector2 getRightJoystick() {
        return new Vector2(
            getRightX(),
            getRightY()
        );
    }
}
