package org.team5148.rapidreact.subsystem;

import org.team5148.lib.PIDConfig;
import org.team5148.lib.PIDSparkMax;
import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.MotorIDs;

public class BallLauncher {
    private static final double REV_RANGE = 100;

    private PIDConfig pidConfig = new PIDConfig(0.0003, 0.0000005, 0.0001, 0.0000001, -1, 1);
    private PIDSparkMax topMotor = new PIDSparkMax("Top Launcher", MotorIDs.LAUNCHER_TOP, pidConfig);
    private PIDSparkMax bottomMotor = new PIDSparkMax("Bottom Launcher", MotorIDs.LAUNCHER_BOTTOM, pidConfig);

    private NTManager nt = NTManager.getInstance();

    /**
     * Runs the launcher at the default speed using PID
     * @param isRunning - Whether or not the launcher is running
     */
    public void run(boolean isRunning) {
        double velocity = nt.launcherSetVel.getDouble(DefaultSpeed.LAUNCHER_VELOCITY);
        double speed = isRunning ? velocity : 0;

        run(speed);
    }

    /**
     * Runs the launcher at a set velocity
     * @param speed - Velocity to set to in RPM
     */
    public void run(double velocity){
        double roll = nt.launcherRoll.getDouble(DefaultSpeed.ROLL_VELOCITY);
        run(velocity + roll, velocity - roll);
    }
    
    /**
     * Runs each launcher motor using PID
     * @param topVelocity - Velocity of the top motor in RPM
     * @param bottomVelocity - Velocity of the bottom motor in RPM
     */
    public void run(double topVelocity, double bottomVelocity) {
        topMotor.setVelocity(topVelocity);
        bottomMotor.setVelocity(-bottomVelocity);
    }

    /**
     * Gets whether or not the launcher is up to speed
     * @return True if the launcher is up to speed. False otherwise.
     */
    public boolean getRev() {
        boolean isRev = topMotor.getRev(REV_RANGE) && bottomMotor.getRev(REV_RANGE);
        nt.launcherRev.setBoolean(isRev);
        return isRev;
    }
}
