package org.team5148.lib.drivers;

import com.revrobotics.CANSparkMax;

/**
 * SparkMax wrapper that decreases CAN bus traffic
 */
public class LazySparkMax extends CANSparkMax {
    private double lastSpeed = Double.NaN;

    public LazySparkMax(int id, MotorType motorType) {
        super(id, motorType);
    }

    @Override
    public void set(double speed) {
        if (speed != lastSpeed) {
            lastSpeed = speed;
            super.set(speed);
        }
    }
}
