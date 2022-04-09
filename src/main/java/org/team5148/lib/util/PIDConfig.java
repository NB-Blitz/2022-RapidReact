package org.team5148.lib.util;

public class PIDConfig {
    public double kP = 0;
    public double kI = 0;
    public double kD = 0;

    /**
     * Configuration for driving a motor using PID.
     * For documentation on configuring PID, refer to
     * https://frc-pdr.readthedocs.io/en/latest/control/pid_control.html
     * 
     * @param p - Proportional [0 - 1]
     * @param i - Integral [0 - 1]
     * @param d - Derivative [0 - 1]
     * 
     * @deprecated Flash PID directly to the motor controller instead
     */
    public PIDConfig(double p, double i, double d) {
        this.kP = p;
        this.kI = i;
        this.kD = d;
    }
}
