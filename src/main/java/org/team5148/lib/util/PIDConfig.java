package org.team5148.lib.util;

public class PIDConfig {
    public double p = 0;
    public double i = 0;
    public double d = 0;
    public double ff = 0;

    public double minOutput = 0;
    public double maxOutput = 0;

    /**
     * Configuration for driving a motor using PID.
     * For documentation on configuring PID, refer to
     * https://frc-pdr.readthedocs.io/en/latest/control/pid_control.html
     * 
     * @param p - Proportional [0 - 1]
     * @param i - Integral [0 - 1]
     * @param d - Derivative [0 - 1]
     * @param ff - Feed Forward [0 - 1]
     * @param minOutput - Minimum Output [-1 - 1]
     * @param maxOutput - Maximum Output [-1 - 1]
     */
    public PIDConfig(double p, double i, double d, double ff, double minOutput, double maxOutput) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.ff = ff;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
    }
}
