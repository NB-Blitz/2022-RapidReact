package org.team5148.lib.motors;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;

/**
 * TalonSRX wrapper that decreases CAN bus traffic
 */
public class VictorSPX extends com.ctre.phoenix.motorcontrol.can.VictorSPX implements MotorController {
    private double m_lastSpeed = Double.NaN;

    /**
     * Initializes a VictorSPX
     * @param id - CAN ID of the VictorSPX
     */
    public VictorSPX(int id) {
        super(id);
    }

    public void set(double speed) {
        if (speed != m_lastSpeed) {
            m_lastSpeed = speed;
            super.set(VictorSPXControlMode.PercentOutput, speed);
        }
    }

    public void setRampRate(double ramp) {
        this.configOpenloopRamp(ramp);
    }
}
