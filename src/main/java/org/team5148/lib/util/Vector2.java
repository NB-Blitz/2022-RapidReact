package org.team5148.lib.util;

/**
 * Represents a 2-axis vector
 */
public class Vector2 {
    public double x;
    public double y;

    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }
}
