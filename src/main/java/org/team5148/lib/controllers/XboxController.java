package org.team5148.lib.controllers;

/**
 * Represents an Xbox Controller connected to the Driver Station
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
        double value = super.getRawAxis(axisID);
        if (Math.abs(value) < m_deadband)
            return 0;
        return (value - ((m_deadband * Math.abs(value)) / value)) / (1 - m_deadband);
    }
}
