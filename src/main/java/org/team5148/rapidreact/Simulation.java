package org.team5148.rapidreact;

import org.team5148.lib.Vector3;

import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.hal.simulation.SimDeviceDataJNI;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class Simulation {
    private static final double X_SPEED = 0.01;
    private static final double Y_SPEED = 0.03;
    private static final double Z_SPEED = 1.8;

    private static final double X_DAMP = 0.7;
    private static final double Y_DAMP = 0.7;
    private static final double Z_DAMP = 0.8;

    private static final double JITTER = 0.3;

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
        double xSpeed = X_SPEED * (Math.random() * JITTER + 1 - JITTER / 2);
        double ySpeed = Y_SPEED * (Math.random() * JITTER + 1 - JITTER / 2);

        // Physics
        double cos = Math.cos(Math.toRadians(pos.z));
        double sin = Math.sin(Math.toRadians(pos.z));
        Vector3 accel = new Vector3(
            ((cos * ySpeed * input.y) + (sin * xSpeed * input.x)),
            ((sin * ySpeed * input.y) - (cos * xSpeed * input.x)),
            Z_SPEED * input.z
        );
        vel = new Vector3(
            (vel.x * X_DAMP) + accel.x,
            (vel.y * Y_DAMP) + accel.y,
            (vel.z * Z_DAMP) + accel.z
        );
        pos = new Vector3(
            pos.x + vel.x,
            pos.y + vel.y,
            pos.z + vel.z
        );

        // Sim Devices
        simAngle.set(orginAngle - pos.z);
        simAccelX.set(accel.x);
        simAccelY.set(accel.y);
        nt.simField.setRobotPose(new Pose2d(pos.x, pos.y, Rotation2d.fromDegrees(pos.z)));
    }

    public void reset() {
        pos = new Vector3(
            8.2,
            5.4,
            248
        );
        vel = new Vector3();
        orginAngle = pos.z;
    }
}
