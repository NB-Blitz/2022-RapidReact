package org.team5148.lib.motors;

public interface MotorController {

    /**
     * Sets the speed of the motor
     * @param speed - Speed of the motor [-1 - 1]
     */
    public void set(double speed);
}