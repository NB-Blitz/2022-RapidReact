package org.team5148.rapidreact.autonomous;

import com.kauailabs.navx.frc.AHRS;

import org.team5148.lib.Vector3;
import org.team5148.rapidreact.NTManager;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

public class AutoManager {
    public static final int DEFAULT_MODE = 2;
    public static final double ABORT_ACCEL = 5;
    public static final double MAX_ROTATE = 0.7;
    public static final double CAMERA_HEIGHT_METERS = Units.inchesToMeters(22);
    public static final double TARGET_HEIGHT_METERS = Units.inchesToMeters(104);
    public static final double CAMERA_PITCH_RADIANS = Units.degreesToRadians(45);

    // Sensors
    private Timer timer = new Timer();
    private AHRS navx = new AHRS(Port.kMXP);
    private NTManager nt = NTManager.getInstance();
    private PhotonCamera goalCamera = new PhotonCamera("GoalCamera");

    // State
    private int step = 0;
    private boolean isAborted = false;
    private double maxAccel = 0;
    private double gyroAngle = 0;
    private double orginAngle = 0;
    public double goalAngle = 0;

    /**
     * Updates all auto routines
     * @return Robot Inputs for Autonomous
     */
    public AutoInput update() {
        
        // Sensors
        gyroAngle = navx.getAngle();
        Vector3 accel = new Vector3(
            navx.getWorldLinearAccelX(),
            navx.getWorldLinearAccelY(), 
            navx.getWorldLinearAccelZ()
        );
        maxAccel = accel.getMagnitude();

        // Abort on Strong Impact
        if (maxAccel >= ABORT_ACCEL)
            isAborted = true;
        if (Math.abs(gyroAngle) > 1000)
            isAborted = true;

        // Modes
        double mode = nt.autoMode.getDouble(DEFAULT_MODE);
        AutoInput input = new AutoInput();
        if (isAborted)
            input = new AutoInput();
        else if (mode == 1)
            input = mode1();
        else if (mode == 2)
            input = mode2();

        // Network Tables
        nt.autoStep.setNumber(step);
        nt.autoGyro.setDouble(gyroAngle);
        nt.autoAccel.setDouble(maxAccel);
        nt.autoXInput.setDouble(input.move.x);
        nt.autoYInput.setDouble(input.move.y);
        nt.autoZInput.setDouble(input.move.z);
        nt.autoAbort.setBoolean(isAborted);

        return input;
    }

    /**
     * Resets Auto Manager
     */
    private void reset() {
        step = 0;
        gyroAngle = 0;
        orginAngle = 0;
        goalAngle = 0;
        isAborted = false;
        timer.reset();
        timer.start();
        navx.reset();
    }

    /**
     * Initializes Autonomous Mode
     */
    public void initAuto() {
        reset();
        goalCamera.setDriverMode(false);
    }

    /**
     * Initializea Teleoperated Mode
     */
    public void initTeleop() {
        goalCamera.setDriverMode(false);
    }

    /**
     * Rotates to a specified angle
     * @param angle - Angle in degrees to rotate to
     * @return Vector3 of controls
     */
    public Vector3 rotateTo(double angle) {
        double deltaAngle = gyroAngle - angle;
        double power = deltaAngle / 90;

        if (power > MAX_ROTATE)
            power = MAX_ROTATE;
        if (power < -MAX_ROTATE)
            power = -MAX_ROTATE;

        Vector3 output = new Vector3(0, 0, power);
        return output;
    }

    /**
     * Rotates towards a goal
     * @return Vector3 of controls
     */
    public Vector3 alignToGoal() {
        PhotonPipelineResult result = goalCamera.getLatestResult();
        if (result.hasTargets()) {
            PhotonTrackedTarget target = result.getBestTarget();
            goalAngle = gyroAngle + target.getYaw();
            nt.autoGoalAngle.setDouble(goalAngle);

            return new Vector3(0, 0, rotateTo(goalAngle).z);
        } else {
            return new Vector3();
        }
    }

    public double getGoalDistance() {
        PhotonPipelineResult result = goalCamera.getLatestResult();
        if (result.hasTargets()) {
            PhotonTrackedTarget target = result.getBestTarget();

            double distance = PhotonUtils.calculateDistanceToTargetMeters(
                CAMERA_HEIGHT_METERS,
                TARGET_HEIGHT_METERS,
                CAMERA_PITCH_RADIANS,
                Units.degreesToRadians(target.getPitch())
            );
            nt.autoGoalDistance.setDouble(distance);

            return distance;
        } else {
            return 0;
        }
    }

    /**
     * Drives toward a vector while accounting for rotation
     * @param xSpeed - X Speed [-1 - 1]
     * @param ySpeed - Y Speed [1 - -1]
     * @return Vector3 of controls
     */
    public Vector3 driveTo(double xSpeed, double ySpeed) {
        double deltaAngle = gyroAngle - orginAngle;
        double sin = Math.sin(Math.toRadians(deltaAngle));
        double cos = Math.cos(Math.toRadians(deltaAngle));
        Vector3 output = new Vector3(
            (cos * xSpeed) - (sin * ySpeed),
            (cos * ySpeed) + (sin * xSpeed),
            0
        );
        return output;
    }
    
    /**
     * Increments step and resets timer
     */
    private void nextStep() {
        timer.reset();
        step++;
        orginAngle = gyroAngle;
    }

    /*
    *  _____             _   _                 
    * |  __ \           | | (_)                
    * | |__) |___  _   _| |_ _ _ __   ___  ___ 
    * |  _  // _ \| | | | __| | '_ \ / _ \/ __|
    * | | \ \ (_) | |_| | |_| | | | |  __/\__ \
    * |_|  \_\___/ \__,_|\__|_|_| |_|\___||___/
    *                                                                                   
    */

    private AutoInput mode1() {
        AutoInput input = new AutoInput();
        switch (step) {
            case 0:
                input.move.y = 0.3;
                if (timer.get() > 2)
                    nextStep();
                break;
        }
        return input;
    }

    private AutoInput mode2() {
        AutoInput input = new AutoInput();
        switch (step) {
            case 0:
                input.move = rotateTo(180);
                if (timer.get() > 3)
                    nextStep();
                break;
            case 1:
                input.move = driveTo(0, 0.2);
                if (timer.get() > 1.8)
                    nextStep();
                break;
            case 2:
                input.move = rotateTo(0);
                if (timer.get() > 3)
                    nextStep();
                break;
            case 3:
                input.move = driveTo(0, 0.15);
                input.move.z = alignToGoal().z;
                if (timer.get() > 1.2)
                    nextStep();
                break;
            case 4:
                input.move = alignToGoal();
                input.isShooting = true;
                if (timer.get() > 4)
                    nextStep();
                break;
        }
        return input;
    }
}
