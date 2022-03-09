package org.team5148.rapidreact;

import org.team5148.lib.Vector3;

import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.hal.simulation.SimDeviceDataJNI;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class Simulation {
    private static final Vector3 GOAL = new Vector3(315 * 0.0254, 162 * 0.0254, 0);
    private static final Vector3 POSA_R = new Vector3(8.4, 5.4, 248);
    private static final Vector3 POSB_R = new Vector3(9.4, 3, 335);
    private static final Vector3 POSA_B = new Vector3(GOAL.x + GOAL.x - POSA_R.x, GOAL.y + GOAL.y - POSA_R.y, POSA_R.z - 180);

    private static final Vector3 SPEED = new Vector3(0.02, 0.035, 1.8);
    private static final Vector3 DAMP = new Vector3(0.7, 0.7, 0.8);
    private static final double JITTER = 0.3;
    private static final double DEADBAND = 0.05;
    private static final double G_CONST = 9.8066;

    // NavX Sim
    private int simDevice = SimDeviceDataJNI.getSimDeviceHandle("navX-Sensor[0]");
    private SimDouble simAngle = new SimDouble(SimDeviceDataJNI.getSimValueHandle(simDevice, "Yaw"));
    private SimDouble simAccelX = new SimDouble(SimDeviceDataJNI.getSimValueHandle(simDevice, "LinearWorldAccelX"));
    private SimDouble simAccelY = new SimDouble(SimDeviceDataJNI.getSimValueHandle(simDevice, "LinearWorldAccelY"));
    private Vector3 pos = new Vector3();
    private Vector3 vel = new Vector3();
    private double orginAngle = 0;

    private NTManager nt = NTManager.getInstance();
    
    public void drive(Vector3 input) {

        // Random
        double xSpeed = SPEED.x * (Math.random() * JITTER + 1 - JITTER / 2);
        double ySpeed = SPEED.y * (Math.random() * JITTER + 1 - JITTER / 2);

        // Deadband
        if (Math.abs(input.x) < DEADBAND)
            input.x = 0;
        if (Math.abs(input.y) < DEADBAND)
            input.y = 0;
        if (Math.abs(input.z) < DEADBAND)
            input.z = 0;

        // Physics
        double cos = Math.cos(Math.toRadians(pos.z));
        double sin = Math.sin(Math.toRadians(pos.z));
        Vector3 accel = new Vector3(
            ((cos * ySpeed * input.y) + (sin * xSpeed * input.x)),
            ((sin * ySpeed * input.y) - (cos * xSpeed * input.x)),
            SPEED.z * input.z
        );
        vel = new Vector3(
            (vel.x * DAMP.x) + accel.x,
            (vel.y * DAMP.y) + accel.y,
            (vel.z * DAMP.z) + accel.z
        );
        pos = new Vector3(
            pos.x + vel.x,
            pos.y + vel.y,
            pos.z + vel.z
        );

        // Sim Devices
        simAngle.set(orginAngle - pos.z);
        simAccelX.set(accel.x * G_CONST);
        simAccelY.set(accel.y * G_CONST);
        nt.simField.setRobotPose(new Pose2d(pos.x, pos.y, Rotation2d.fromDegrees(pos.z)));
    }

    public double getGoalAngle() {
        double angle = orginAngle - Math.toDegrees(Math.atan2(GOAL.y - pos.y, GOAL.x - pos.x));
        nt.goalVisible.setBoolean(true);
        nt.autoGoalAngle.setDouble(angle);
        return angle;
    }

    public void reset() {
        pos = POSA_B;
        vel = new Vector3();
        orginAngle = pos.z;
        nt.simField.getObject("Goal").setPose(new Pose2d(GOAL.x, GOAL.y, Rotation2d.fromDegrees(0)));
    }
}
