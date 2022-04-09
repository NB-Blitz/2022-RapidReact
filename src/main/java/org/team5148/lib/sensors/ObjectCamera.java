package org.team5148.lib.sensors;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Rotation2d;

/**
 * PhotonCamera wrapper that tracks moving objects
 */
public class ObjectCamera extends PhotonCamera {

    /**
     * Initializes a PhotonCamera that tracks moving objects (Ex. Game Pieces)
     * @param pm_cameraName - Name of the PhotonVision camera
     */
    public ObjectCamera(String pm_cameraName) {
        super(pm_cameraName);
    }

    /**
     * Gets the relative target yaw
     * @return Relative target in Rotation2d. Null if none is found.
     */
    public Rotation2d getTargetYaw() {
        PhotonPipelineResult lv_photonResult = this.getLatestResult();
        if (lv_photonResult.hasTargets()) {
            PhotonTrackedTarget lv_photonTarget = lv_photonResult.getBestTarget();
            return Rotation2d.fromDegrees(lv_photonTarget.getYaw());
        }
        return null;
    }
}
