package org.team5148.rapidreact.subsystem;

import org.team5148.lib.motors.PIDSparkMax;
import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.LauncherTarget;
import org.team5148.rapidreact.config.MotorIDs;

public class BallLauncher {
    private static final double REV_RANGE = 100;

    private PIDSparkMax topMotor = new PIDSparkMax("Top Launcher", MotorIDs.LAUNCHER_TOP);
    private PIDSparkMax bottomMotor = new PIDSparkMax("Bottom Launcher", MotorIDs.LAUNCHER_BOTTOM);

    private NTManager nt = NTManager.getInstance();

    /**
     * Runs the launcher at a target speed
     */
    public void run(LauncherTarget target) {
        double velocity = 0;
        switch (target) {
            case Launchpad:
                velocity = DefaultSpeed.LAUNCHPAD_VELOCITY;
                break;
            case Tarmac:
                velocity = DefaultSpeed.TARMAC_VELOCITY;
                break;
            case FieldWall:
                velocity = DefaultSpeed.FIELD_WALL_VELOCITY;
                break;
            case LowGoal:
                velocity = DefaultSpeed.LOW_GOAL_VELOCITY;
                break;
        }

        nt.launcherSetVel.setDouble(velocity);
        run(velocity);
    }

    /**
     * Stops the launcher motors
     */
    public void stop() {
        run(0, 0);
    }

    /**
     * Runs the launcher at a set velocity
     * @param velocity - Velocity to set to in RPM
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
