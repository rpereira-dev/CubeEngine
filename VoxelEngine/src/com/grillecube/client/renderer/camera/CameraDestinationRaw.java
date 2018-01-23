package com.grillecube.client.renderer.camera;

import com.grillecube.common.maths.Quaternion;
import com.grillecube.common.maths.Vector3f;

public class CameraDestinationRaw extends CameraDestination<CameraView> {

	public final Vector3f desiredPosition;
	public final Quaternion desiredRotation;

	private Vector3f camPos;
	private Quaternion camRot;

	/**
	 * 
	 * @param desiredPosition
	 *            : future position of the camera
	 * @param desiredRotation
	 *            this.accumulator = 0;
	 * 
	 *            : future rotation of the camera
	 * @param duration
	 *            : duration of the transition
	 */
	public CameraDestinationRaw(Vector3f desiredPosition, Quaternion desiredRotation, float duration) {
		super(duration);
		this.desiredPosition = desiredPosition;
		this.desiredRotation = desiredRotation;
	}

	@Override
	protected final void onInit(CameraView camera) {
		this.camRot = new Quaternion();
		this.camPos = new Vector3f();
		this.camPos.set(camera.getPosition());
		this.camRot.set(Quaternion.toQuaternion(camera.getRot()));
	}

	@Override
	protected final void onUpdate(CameraView camera, float ratio) {
		camera.setPosition(Vector3f.interpolate(this.camPos, this.desiredPosition, ratio, null));
		camera.setRot(Quaternion.toEulerAngle(Quaternion.interpolate(this.camRot, this.desiredRotation, ratio)));
	}

	@Override
	protected final void onReached(CameraView camera) {
		camera.setPosition(this.desiredPosition);
		camera.setRot(Quaternion.toEulerAngle(this.desiredRotation));
	}
}
