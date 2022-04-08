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
