package org.team5148.rapidreact.subsystem;

import org.team5148.lib.PIDConfig;
import org.team5148.lib.PIDSparkMax;
import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;

public class BallLauncher {
    private static final double REV_RANGE = 100;

    private PIDConfig pidConfig = new PIDConfig(0.00001, 0.0000001, 0.000025, 0.00001, -1, 1);
    private PIDSparkMax topMotor = new PIDSparkMax("Top Launcher", MotorIDs.LAUNCHER_TOP, pidConfig);
    private PIDSparkMax bottomMotor = new PIDSparkMax("Bottom Launcher", MotorIDs.LAUNCHER_BOTTOM, pidConfig);

    private NTManager nt = NTManager.getInstance();

    /**
     * Runs the launcher at the default speed using percentage
     * @param isRunning - Whether or not the launcher is running
     */
    public void runPercentage(boolean isRunning) {
        runPercentage(isRunning ? DefaultSpeed.LAUNCHER_PERCENT : 0);
    }

    /**
     * Runs the launcher at a set speed
     * @param speed - Speed to set to [-1 - 1]
     */
    public void runPercentage(double speed){
        double roll = nt.launcherRoll.getDouble(DefaultSpeed.ROLL);
        runPercentage(speed + roll, speed - roll);
    }

    /**
     * Runs each launcher motor independently
     * @param topSpeed - Speed of the top motor [-1 - 1]
     * @param bottomSpeed - Speed of the bottom motor [-1 - 1]
     */
    public void runPercentage(double topSpeed, double bottomSpeed) {
        topMotor.setPercentage(topSpeed);
        bottomMotor.setPercentage(bottomSpeed);
    }

    /**
     * Runs the launcher at the default speed using PID
     * @param isRunning - Whether or not the launcher is running
     */
    public void runVelocity(boolean isRunning) {
        double velocity = nt.launcherSetVel.getDouble(DefaultSpeed.LAUNCHER_VELOCITY);
        double roll = nt.launcherRoll.getDouble(DefaultSpeed.ROLL);

        double topSpeed = isRunning ? velocity + roll : 0;
        double bottomSpeed = isRunning ? velocity - roll : 0;

        runVelocity(topSpeed, bottomSpeed);
    }

    /**
     * Runs the launcher at a set velocity
     * @param speed - Velocity to set to in RPM
     */
    public void runVelocity(double velocity){
        double roll = nt.launcherRoll.getDouble(DefaultSpeed.ROLL);
        runVelocity(velocity + roll, velocity - roll);
    }
    
    /**
     * Runs each launcher motor using PID
     * @param topVelocity - Velocity of the top motor in RPM
     * @param bottomVelocity - Velocity of the bottom motor in RPM
     */
    public void runVelocity(double topVelocity, double bottomVelocity) {
        topMotor.setVelocity(topVelocity);
        bottomMotor.setVelocity(bottomVelocity);
    }

    /**
     * Gets whether or not the launcher is up to speed
     * @return True if the launcher is up to speed. False otherwise.
     */
    public boolean getRev() {
        return topMotor.getRev(REV_RANGE) && bottomMotor.getRev(REV_RANGE);
    }
}
