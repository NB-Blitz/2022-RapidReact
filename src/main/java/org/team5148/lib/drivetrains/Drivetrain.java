package org.team5148.lib.drivetrains;

import org.team5148.lib.util.Vector2;
import org.team5148.lib.util.Vector3;

import edu.wpi.first.math.geometry.Rotation2d;

public abstract class Drivetrain {

     /**
     * Drives the drivetrain using a Vector2 input
     * @param input - 2-Axis Input
     */
    public abstract void drive(Vector2 input);

     /**
     * Drives the drivetrain using a Vector3 input
     * @param input - 3-Axis Input
     */
    public abstract void drive(Vector3 input);

     /**
     * Sets the ramp rate of all of the motors
     * @param rampRate - Seconds from 0 to full throttle
     */
    public abstract void setRampRate(double rampRate);

    /**
     * Drives the drivetrain using field-oriented drive
     * @param input - 3-Axis Input
     * @param gyroAngle - Angle of the gyroscope
     */
    public void drive(Vector3 input, Rotation2d gyroAngle) {
        input.rotate(gyroAngle.getDegrees());
        drive(input);
    }
}
