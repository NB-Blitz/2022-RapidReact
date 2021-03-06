package org.team5148.lib;

public class Vector3 {
    public double x = 0;
    public double y = 0;
    public double z = 0;

    public Vector3() {}

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
