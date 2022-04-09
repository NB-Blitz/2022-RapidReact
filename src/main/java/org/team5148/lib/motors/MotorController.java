package org.team5148.lib.motors;

public interface MotorController {

    /**
     * Sets the speed of the motor
     * @param speed - Speed of the motor [-1 - 1]
     */
    public void set(double speed);

    /**
     * Sets the ramp rate of the motor
     * @param ramp - Seconds from 0 to full throttle
     */
    public void setRampRate(double ramp);
}