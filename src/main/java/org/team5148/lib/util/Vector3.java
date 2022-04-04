package org.team5148.lib.util;

/**
 * Represents a 3-axis vector
 */
public class Vector3 extends Vector2 {
    public double z = 0;

    public Vector3() {
        this.z = 0;
    }

    public Vector3(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    @Override
    public double getMagnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
