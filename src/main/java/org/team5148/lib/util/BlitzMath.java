package org.team5148.lib.util;

/**
 * Class for computing mathematical calulations across BlitzLib
 */
public class BlitzMath {
    private BlitzMath() {}

    public static double applyDeadband(double deadband, double value) {
        if (Math.abs(value) < deadband)
            return 0;
        return (value - ((deadband * Math.abs(value)) / value)) / (1 - deadband);
    }
}
