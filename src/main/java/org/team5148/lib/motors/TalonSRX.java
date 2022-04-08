package org.team5148.lib.motors;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;

/**
 * TalonSRX wrapper that decreases CAN bus traffic
 */
public class TalonSRX extends com.ctre.phoenix.motorcontrol.can.TalonSRX implements MotorController {
    private double m_lastSpeed = Double.NaN;

    public TalonSRX(int id) {
        super(id);
    }

    public void set(double speed) {
        if (speed != m_lastSpeed) {
            m_lastSpeed = speed;
            super.set(TalonSRXControlMode.PercentOutput, speed);
        }
    }

    public void setRampRate(double ramp) {
        this.configOpenloopRamp(ramp);
    }
}
