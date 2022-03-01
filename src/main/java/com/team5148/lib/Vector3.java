package com.team5148.lib;

public class Vector3 {
    public double x = 0;
    public double y = 0;
    public double z = 0;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Tokyo drifts the robot towards a Vector3 target.
     * (Only works on mecanum drive)
     * (X: [-1 - 1], Y: [-1 - 1], Z: [-pi - pi])
     * @param currentRotation - Current Rotation in Radians
     * @return Vector3 of joystick controls
     */
    public Vector3 interpolate(double currentRotation) {
        double deltaAngle = Math.atan2(y, x) - currentRotation;
        return new Vector3(
            Math.sin(deltaAngle) * x,
            Math.cos(deltaAngle) * x, 
            currentRotation - z
        );
    }
}
