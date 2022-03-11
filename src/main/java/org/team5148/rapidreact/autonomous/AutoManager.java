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
    public static final double ABORT_ACCEL = 5;
    public static final double CAMERA_HEIGHT_METERS = Units.inchesToMeters(6);
    public static final double TARGET_HEIGHT_METERS = Units.feetToMeters(7);
    public static final double CAMERA_PITCH_RADIANS = Units.degreesToRadians(15);

    // Sensors
    private Timer timer = new Timer();
    private AHRS navx = new AHRS(Port.kMXP);
    private NTManager nt = NTManager.getInstance();
    private PhotonCamera hubCamera = new PhotonCamera("GoalCamera");

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
        double mode = nt.autoMode.getDouble(0);
        AutoInput input = new AutoInput();
        if (isAborted)
            input = new AutoInput();
        else if (mode == 0)
            input = mode0();
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
    public void reset() {
        step = 0;
        gyroAngle = 0;
        orginAngle = 0;
        isAborted = false;
        timer.reset();
        timer.start();
        navx.reset();
    }

    /**
     * Rotates to a specified angle
     * @param angle - Angle in degrees to rotate to
     * @return Vector3 of controls
     */
    public Vector3 rotateTo(double angle) {
        double deltaAngle = gyroAngle - angle;
        double power = deltaAngle / 80;

        if (power > 1)
            power = 1;
        if (power < -1)
            power = -1;

        Vector3 output = new Vector3(0, 0, power);
        return output;
    }

    /**
     * Rotates towards a goal
     * @return Vector3 of controls
     */
    public Vector3 rotateToGoal() {
        PhotonPipelineResult result = hubCamera.getLatestResult();
        if (!result.hasTargets())
            return new Vector3();
        PhotonTrackedTarget target = result.getBestTarget();
        
        double angle = gyroAngle + target.getYaw();
        double range = PhotonUtils.calculateDistanceToTargetMeters(
                CAMERA_HEIGHT_METERS,
                TARGET_HEIGHT_METERS,
                CAMERA_PITCH_RADIANS,
                Units.degreesToRadians(target.getPitch())
        );
        nt.autoGoalAngle.setDouble(angle);
        nt.autoGoalRange.setDouble(range);
        return rotateTo(angle);
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

    private AutoInput mode0() {
        AutoInput input = new AutoInput();
        switch (step) {
            case 0:
                input.move = driveTo(0, .5);
                input.move.z = rotateTo(180).z;
                if (timer.get() > 3)
                    nextStep();
                break;
            case 1:
                input.move = driveTo(0, .5);
                input.move.z = rotateTo(0).z;
                if (timer.get() > 3)
                    nextStep();
                break;
            default:
                step = 0;
                break;
        }
        return input;
    }

    private AutoInput mode1() {
        AutoInput input = new AutoInput();
        switch (step) {
            case 0:
                input.move = driveTo(.2, -.5);
                if (timer.get() > 0.3)
                    nextStep();
                break;
            case 1:
                input.move = rotateToGoal();
                input.isShooting = true;
                if (timer.get() > 2)
                    nextStep();
                break;
            case 2:
                input.move = driveTo(0, -.2);
                input.move.z = rotateTo(160).z;
                if (timer.get() > 1.2)
                    nextStep();
                break;
            case 3:
                input.move = driveTo(.4, -.2);
                input.move.z = rotateTo(270).z;
                if (timer.get() > 1.2)
                    nextStep();
                break;
            case 4:
                input.move.z = rotateTo(390).z;
                if (timer.get() > 1)
                    nextStep();
                break;
            case 5:
                input.move = rotateToGoal();
                input.isShooting = true;
                if (timer.get() > 2)
                    nextStep();
                break;
            case 6:
                input.move = driveTo(-.5, -.6);
                input.move.z = rotateTo(210).z;
                if (timer.get() > 1)
                    nextStep();
                break;
            case 7:
                if (timer.get() > 3)
                    nextStep();
                break;
            case 8:
                input.move = driveTo(-.5, -.7);
                input.move.z = rotateTo(390).z;
                if (timer.get() > 1)
                    nextStep();
                break;
            case 9:
                input.move = rotateToGoal();
                input.isShooting = true;
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
                input.move = driveTo(0, .6);
                if (timer.get() > .5)
                    nextStep();
                break;
            case 1:
                input.move = rotateTo(180);
                if (timer.get() > 1)
                    nextStep();
                break;
            case 2:
                input.move = rotateToGoal();
                input.isShooting = true;
                if (timer.get() > 2)
                    nextStep();
                break;
            case 3:
                input.move = driveTo(.51, -.1);
                input.move.z = rotateTo(285).z;
                if (timer.get() > 2)
                    nextStep();
                break;
            case 4:
                if (timer.get() > 2)
                    nextStep();
                break;
            case 5:
                input.move = driveTo(-.3, -.5);
                input.move.z = rotateTo(155).z;
                if (timer.get() > 1)
                    nextStep();
                break;
            case 6:
                input.move = rotateTo(125);
                if (timer.get() > .5)
                    nextStep();
                break;
            case 7:
                input.move = rotateToGoal();
                input.isShooting = true;
                if (timer.get() > 2)
                    nextStep();
                break;
        }
        return input;
    }
}
