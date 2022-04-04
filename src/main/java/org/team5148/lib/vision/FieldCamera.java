package org.team5148.lib.vision;

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
public class FieldCamera extends ObjectCamera {
    private Pose2d m_targetPose;
    private double m_cameraHeightMeters;
    private double m_cameraPitchRadians;
    private double m_targetHeightMeters;

    public FieldCamera(
        String pm_cameraName,
        Pose2d pm_targetPose,
        double pm_cameraHeightInches,
        double pm_cameraPitchDegrees,
        double pm_targetHeightInches) {
            
        super(pm_cameraName);
        this.m_targetPose = pm_targetPose;
        this.m_cameraHeightMeters = Units.inchesToMeters(pm_cameraHeightInches);
        this.m_cameraPitchRadians = Units.degreesToRadians(pm_cameraPitchDegrees);
        this.m_targetHeightMeters = Units.inchesToMeters(pm_targetHeightInches);
    }

    /**
     * Gets the target pose
     * @return Global target Pose2d
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
