package org.team5148.rapidreact.subsystem;

import org.team5148.lib.drivetrains.MecanumPID;
import org.team5148.lib.kinematics.MecanumOdom;
import org.team5148.rapidreact.NTManager;

import edu.wpi.first.math.geometry.Pose2d;

/**
 * Manages autonomous routines and helpers
 */
public class AutoManager {

    // Odometry
    private MecanumOdom mecanumOdom;

    // Other
    NTManager nt = NTManager.getInstance();

    public AutoManager(MecanumPID mecanumDrive) {
        // TODO: Test odometry & Implement path planner
        mecanumOdom = new MecanumOdom(mecanumDrive);
    }

    public void reset() {
        mecanumOdom.reset();
    }

    public void update() {
        Pose2d robotPose = mecanumOdom.update();
        nt.simField.setRobotPose(robotPose);
    }
}
