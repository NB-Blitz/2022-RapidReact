package org.team5148.lib.sensors;

import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.util.Units;

/**
 * PhotonCamera wrapper that tracks stationary targets
 */
public class TargetCamera extends ObjectCamera {
    private Pose2d m_targetPose;
    private double m_cameraHeightMeters;
    private double m_cameraPitchRadians;
    private double m_targetHeightMeters;

    /**
     * Initializes a PhotonCamera that tracks field targets
     * @param cameraName - Name of the PhotonVision camera
     * @param targetPose - Position of the target relative to the field
     * @param cameraHeightInches - Height of the camera on the robot in inches
     * @param cameraPitchDegrees - Angle of the camera on the robot in degrees
     * @param targetHeightInches - Height of the target on the field in inches
     */
    public TargetCamera(
        String cameraName,
        Pose2d targetPose,
        double cameraHeightInches,
        double cameraPitchDegrees,
        double targetHeightInches) {
            
        super(cameraName);
        this.m_targetPose = targetPose;
        this.m_cameraHeightMeters = Units.inchesToMeters(cameraHeightInches);
        this.m_cameraPitchRadians = Units.degreesToRadians(cameraPitchDegrees);
        this.m_targetHeightMeters = Units.inchesToMeters(targetHeightInches);
    }

    /**
     * Gets the distance from the active target
     * @return Distance from target in meters. -1 if none is found.
     */
    public double getTargetDistance() {
        PhotonPipelineResult lv_photonResult = this.getLatestResult();
        if (lv_photonResult.hasTargets()) {
            PhotonTrackedTarget lv_photonTarget = lv_photonResult.getBestTarget();
            return PhotonUtils.calculateDistanceToTargetMeters(
                m_cameraHeightMeters,
                m_targetHeightMeters,
                m_cameraPitchRadians,
                lv_photonTarget.getPitch()
            );
        }
        return -1;
    }

    /**
     * Gets the estimated robot pose
     * @return Global robot pose. Null if none is found. 
     */
    public Pose2d getRobotPose(Rotation2d gyroAngle) {
        PhotonPipelineResult lv_photonResult = this.getLatestResult();
        if (lv_photonResult.hasTargets()) {
            PhotonTrackedTarget lv_photonTarget = lv_photonResult.getBestTarget();
            return PhotonUtils.estimateFieldToRobot(
                m_cameraHeightMeters,
                m_targetHeightMeters,
                m_cameraPitchRadians,
                lv_photonTarget.getPitch(),
                Rotation2d.fromDegrees(lv_photonTarget.getYaw()),
                gyroAngle,
                m_targetPose,
                new Transform2d()
            );
        }
        return null;
    }

}
