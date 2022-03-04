package org.team5148.rapidreact.autonomous;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import org.team5148.lib.Vector3;
import org.team5148.rapidreact.NTManager;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveOdometry;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;

public class AutoManager {
    private final double BALL_CAM_FOV = 50;
    private final double ABORT_ACCEL = 10;

    // Sensors
    private Timer timer = new Timer();
    private AHRS navx = new AHRS(Port.kMXP);
    private NTManager nt = NTManager.getInstance();
    private RelativeEncoder frontLeftEnc;
    private RelativeEncoder frontRightEnc;
    private RelativeEncoder backLeftEnc;
    private RelativeEncoder backRightEnc;

    // State
    private int step = 0;
    private double maxAccel = 0;
    private double gyroAngle = 0;
    private double ballAngle = 0;
    private double goalAngle = 0;
    private double odomX = 0;
    private double odomY = 0;
    private boolean isAborted = false;

    // Odometry
    private Translation2d frontLeftLocation = new Translation2d(0.381, 0.381);
    private Translation2d frontRightLocation = new Translation2d(0.381, -0.381);
    private Translation2d backLeftLocation = new Translation2d(-0.381, 0.381);
    private Translation2d backRightLocation = new Translation2d(-0.381, -0.381);
    private MecanumDriveKinematics kinematics = new MecanumDriveKinematics(
        frontLeftLocation, frontRightLocation, backLeftLocation, backRightLocation
    );
    private MecanumDriveOdometry odometry = new MecanumDriveOdometry(kinematics, Rotation2d.fromDegrees(0));

    public AutoManager(CANSparkMax frontLeft, CANSparkMax frontRight, CANSparkMax backLeft, CANSparkMax backRight) {
        frontLeftEnc = frontLeft.getEncoder();
        frontRightEnc = frontRight.getEncoder();
        backLeftEnc = backLeft.getEncoder();
        backRightEnc = backRight.getEncoder();
    }

    /**
     * Updates all auto routines
     * @return Robot Inputs for Autonomous
     */
    public AutoInput update() {   

        // Sensors
        gyroAngle = navx.getAngle();
        maxAccel = navx.getAccelFullScaleRangeG();

        // Odometry
        odometry.update(
            Rotation2d.fromDegrees(-gyroAngle),
            new MecanumDriveWheelSpeeds(
                frontLeftEnc.getVelocity(), frontRightEnc.getVelocity(),
                backLeftEnc.getVelocity(), backRightEnc.getVelocity()
            )
        );
        odomX = odometry.getPoseMeters().getX();
        odomY = odometry.getPoseMeters().getY();

        // Abort on Strong Impact
        if (maxAccel > ABORT_ACCEL)
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

        // Network Tables
        nt.autoStep.setNumber(step);
        nt.autoGyro.setDouble(gyroAngle);
        nt.autoAccel.setDouble(maxAccel);
        nt.autoXInput.setDouble(input.move.x);
        nt.autoYInput.setDouble(input.move.y);
        nt.autoZInput.setDouble(input.move.z);
        nt.autoXPos.setDouble(odomX);
        nt.autoYPos.setDouble(odomY);
        nt.autoField.setRobotPose(odometry.getPoseMeters());

        return input;
    }

    /**
     * Resets Auto Manager
     */
    public void reset() {
        isAborted = false;
        timer.reset();
        timer.start();
        navx.reset();
        odometry.resetPosition(new Pose2d(), Rotation2d.fromDegrees(0));
    }

    /**
     * Rotates to a specified angle
     * @param angle - Angle in degrees to rotate to
     * @return Vector3 of controls
     */
    public Vector3 rotateTo(double angle) {
        double deltaAngle = gyroAngle - angle;
        double power = deltaAngle / 60;

        if (power > 1)
            power = 1;
        if (power < -1)
            power = -1;

        Vector3 output = new Vector3(0, 0, power);
        return output;
    }

    /**
     * Rotates towards a ball
     * @return Vector3 of controls
     */
    public Vector3 rotateToBall() {
        double ballX = nt.ballX.getDouble(0);
        if (ballX != 0) {
            ballAngle = (ballX * (BALL_CAM_FOV / 2)) + gyroAngle;
            nt.ballX.setDouble(0);
            nt.autoBallAngle.setDouble(ballAngle);
        }
        Vector3 input = rotateTo(ballAngle);
        return input;
    }

    /**
     * Rotates towards a goal
     * @return Vector3 of controls
     */
    public Vector3 rotateToGoal() {
        double goalX = nt.goalX.getDouble(0);
        if (goalX != 0) {
            goalAngle = (goalX * (BALL_CAM_FOV / 2)) + gyroAngle;
            nt.goalX.setDouble(0);
            nt.autoGoalAngle.setDouble(goalAngle);
        }
        Vector3 input = rotateTo(goalAngle);
        return input;
    }

    /**
     * Drives toward a ball
     * @param x - Approximate x position of the ball
     * @param y - Approximate y position of the ball
     * @param z - Approximate z rotation of the ball
     * @return Vector3 of controls
     */
    public Vector3 driveToBall(double x, double y, double z, double speed) {
        boolean isVisible = nt.ballVisible.getBoolean(false);
        Vector3 input = new Vector3();
        if (isVisible) {
            input.y = speed;
            input.z = rotateToBall().z;

        } else {
            input = driveTo(x, y, speed);
            input.z = rotateTo(z).z;
        }
        return input;
    }

   /**
     * Drives toward a goal
     * @param x - Approximate x position of the ball
     * @param y - Approximate y position of the ball
     * @param z - Approximate z rotation of the ball
     * @return Vector3 of controls
     */
    public Vector3 driveToGoal(double x, double y, double z, double speed) {
        boolean isVisible = nt.goalVisible.getBoolean(false);
        Vector3 input = new Vector3();
        if (isVisible) {
            input.y = speed;
            input.z = rotateToGoal().z;

        } else {
            input = driveTo(x, y, speed);
            input.z = rotateTo(z).z;
        }
        return input;
    }

    public Vector3 driveTo(double x, double y, double speed) {
        double deltaAngle = gyroAngle - Math.atan2(odomY - y, odomY - x);
        Vector3 output = new Vector3(
            Math.sin(Math.toRadians(deltaAngle)) * speed,
            Math.cos(Math.toRadians(deltaAngle)) * speed,
            0
        );
        return output;
    }
    
    private void nextStep() {
        odometry.resetPosition(new Pose2d(), Rotation2d.fromDegrees(-gyroAngle));
        timer.reset();
        step++;
    }

    public AutoInput mode0() {
        AutoInput input = new AutoInput();
        switch (step) {
            case 0:
                input.move = driveToBall(0, -1, 180, 0.2);
                break;
        }
        return input;
    }

    public AutoInput mode1() {
        AutoInput input = new AutoInput();
        switch (step) {
            case 0: // Toward Ball
                input.move = rotateToBall();
                input.move.y += 0.4;
                if (timer.get() > 1)
                    nextStep();
                break;
            case 1:
                input.move = rotateTo(180);
                if (gyroAngle > 170)
                    nextStep();
                break;
            case 2:
                input.move = rotateToGoal();
                input.isShooting = true;
                if (timer.get() > 2)
                    nextStep();
                break;
            case 3:
                input.move = rotateTo(90);
                if (gyroAngle < 100)
                    nextStep();
                break;
            case 4:
                input.move = rotateToBall();
                input.move.y += 0.4;
                if (timer.get() < 2)
                    nextStep();
                break;
            case 5:
                input.move = rotateTo(220);
                if (gyroAngle > 210)
                    nextStep();
                break;
            case 6:
                input.move = rotateToGoal();
                input.move.y += 0.4;
                if (timer.get() < 2)
                    nextStep();
                break;
            case 7:
                input.isShooting = true;
                if (timer.get() > 2)
                    nextStep();
                break;
        }
        return input;
    }
}
