package com.grillecube.client.renderer.camera;

import com.grillecube.common.maths.Vector3f;

public class CameraDestinationCenter extends CameraDestination<CameraPerspectiveWorldCentered> {

	public final float desiredDistance;
	public float desiredTheta;
	public float desiredPhi;
	public final Vector3f desiredCenter;

	public float camDistance;
	public float camTheta;
	public float camPhi;
	public Vector3f camCenter;

	/**
	 * 
	 * @param desiredPosition
	 *            : future position of the camera
	 * @param desiredRotation
	 *            : future rotation of the camera
	 * @param desiredCenter
	 *            : center of camera
	 * @param duration
	 *            : duration of the transition
	 */
	public CameraDestinationCenter(float desiredDistance, float desiredPhi, float desiredTheta, Vector3f desiredCenter,
			float duration) {
		super(duration);
		this.desiredDistance = desiredDistance;
		this.desiredPhi = desiredPhi;
		this.desiredTheta = desiredTheta;
		this.desiredCenter = new Vector3f(desiredCenter);
	}

	@Override
	protected final void onInit(CameraPerspectiveWorldCentered camera) {
		this.camDistance = camera.getR();
		this.camPhi = camera.getPhi();
		this.camTheta = camera.getTheta();
		this.camCenter = new Vector3f(camera.getCenter());
	}

	@Override
	protected final void onUpdate(CameraPerspectiveWorldCentered camera, float ratio) {
		camera.setR(this.camDistance * (1.0f - ratio) + this.desiredDistance * ratio);
		camera.setPhi(this.camPhi * (1.0f - ratio) + this.desiredPhi * ratio);
		camera.setTheta(this.camTheta * (1.0f - ratio) + this.desiredTheta * ratio);
		camera.setCenter(Vector3f.interpolate(this.camCenter, this.desiredCenter, ratio, camera.getCenter()));
	}

	@Override
	protected final void onReached(CameraPerspectiveWorldCentered camera) {
		camera.setR(this.desiredDistance);
		camera.setPhi(this.desiredPhi);
		camera.setTheta(this.desiredTheta);
		camera.setCenter(this.desiredCenter);
	}
}
