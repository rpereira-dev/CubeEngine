package com.grillecube.client.renderer.camera;

public class CameraDestinationCenter extends CameraDestination<CameraPerspectiveWorldCentered> {

	public final float desiredDistance;
	public final float desiredPhi;
	public final float desiredTheta;

	public float camDistance;
	public float camPhi;
	public float camTheta;

	/**
	 * 
	 * @param desiredPosition
	 *            : future position of the camera
	 * @param desiredRotation
	 *            : future rotation of the camera
	 * @param duration
	 *            : duration of the transition
	 */
	public CameraDestinationCenter(float desiredDistance, float desiredPhi, float desiredTheta, float duration) {
		super(duration);
		this.desiredDistance = desiredDistance;
		this.desiredPhi = desiredPhi;
		this.desiredTheta = desiredTheta;
	}

	@Override
	protected final void onInit(CameraPerspectiveWorldCentered camera) {
		this.camDistance = camera.getR();
		this.camPhi = camera.getPhi();
		this.camTheta = camera.getTheta();
	}

	@Override
	protected final void onUpdate(CameraPerspectiveWorldCentered camera, float ratio) {
		camera.setR(this.camDistance * (1.0f - ratio) + this.desiredDistance * ratio);
		camera.setPhi(this.camPhi * (1.0f - ratio) + this.desiredPhi * ratio);
		camera.setTheta(this.camTheta * (1.0f - ratio) + this.desiredTheta * ratio);
	}

	@Override
	protected final void onReached(CameraPerspectiveWorldCentered camera) {
		camera.setR(this.desiredDistance);
		camera.setPhi(this.desiredPhi);
		camera.setTheta(this.desiredTheta);
	}
}
