package org.team5148.lib.util;

import edu.wpi.first.math.util.Units;

/**
 * Represents a 2-axis vector
 */
public class Vector2 {
    public double x = 0;
    public double y = 0;

    public Vector2() {}
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Rotates the Vector around an angle
     * @param angle - Rotation in degrees
     */
    public void rotate(double angle) {
        double cosA = Math.cos(Units.degreesToRadians(angle));
        double sinA = Math.sin(Units.degreesToRadians(angle));
        double tempX = x * cosA - y * sinA;
        double tempY = x * sinA + y * cosA;
        x = tempX;
        y = tempY;
    }

    /**
     * Gets the total magnitude of the vector
     * @return Vector magnitude
     */
    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }
}
