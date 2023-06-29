package org.team5148.rapidreact.subsystem;

import org.team5148.lib.PIDConfig;
//import org.team5148.lib.PIDSparkMax;
import org.team5148.rapidreact.NTManager;
import org.team5148.rapidreact.config.DefaultSpeed;
import org.team5148.rapidreact.config.LauncherTarget;
import org.team5148.rapidreact.config.MotorIDs;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class BallLauncher {
    private static final double REV_RANGE = 100;

    //private PIDConfig pidConfig = new PIDConfig(0.0002, 0.000001, 0.00001, 0.0000001, -1, 1);
    private CANSparkMax topMotor = new CANSparkMax(10, MotorType.kBrushless);
    private CANSparkMax bottomMotor = new CANSparkMax(9, MotorType.kBrushless);

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

        //nt.launcherSetVel.setDouble(velocity);
        run(velocity);
    }

    /*public void runAuto(double goalDistance) {
        double lastVelocity = nt.launcherSetVel.getDouble(0);
        double velocity = goalDistance == 0 ? lastVelocity : ((4.7 * goalDistance) + 2218);
        nt.launcherSetVel.setDouble(velocity);
        run(velocity);
    }*/

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
        double roll = -0.08;
        run(velocity * 0.5 + roll, velocity - roll);
    }
    
    /**
     * Runs each launcher motor using PID
     * @param topVelocity - Velocity of the top motor in RPM
     * @param bottomVelocity - Velocity of the bottom motor in RPM
     */
    public void run(double topVelocity, double bottomVelocity) {
        topMotor.set(topVelocity);
        bottomMotor.set(-bottomVelocity);
        //topMotor.set(0.5);
        //bottomMotor.set(-0.5);
    }

    /**
     * Gets whether or not the launcher is up to speed
     * @return True if the launcher is up to speed. False otherwise.
     */
    public boolean getRev() {
        boolean isRev;
        if (topMotor.get() > 0 && bottomMotor.get() < 0) {
            isRev = true;
        }
        else {
            isRev = false;
        }
        //nt.launcherRev.setBoolean(isRev);
        return isRev;
    }
}
