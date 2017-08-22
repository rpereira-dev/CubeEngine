package com.grillecube.client.renderer.camera;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.common.maths.Vector3f;

/** a camera which follow the given entity, 3rd perso view */
public class CameraPerspectiveWorldCentered extends CameraPerspectiveWorld {
	
	private Vector3f _center;

	/** distance from the entity */
	private float _distance_from_center;

	/** angle around the entity */
	private float _angle_around_center;

	public CameraPerspectiveWorldCentered(GLFWWindow window) {
		super(window);
		this._center = new Vector3f();
		this._distance_from_center = 16;
		this._angle_around_center = 0;
		this.increasePitch(15);
	}

	@Override
	public void update() {
		super.update();
		this.calculateCameraPosition();
	}
	
	public double getAngleAroundCenter() {
		return (this._angle_around_center);
	}
	
	public void setAngleAroundCenter(float angle) {
		this._angle_around_center = angle;
	}
	
	public void increaseAngleAroundCenter(float inc) {
		this._angle_around_center += inc;
	}

	public void increaseDistanceFromCenter(float inc) {
		this._distance_from_center += inc;
	}
	
	public void setCenter(Vector3f center) {
		this._center.set(center);
	}
	
	public void setCenter(float x, float y, float z) {
		this._center.set(x, y, z);
	}

	public float getDistanceFromCenter() {
		return (this._distance_from_center);
	}
	
	public void setDistanceFromCenter(float distance) {
		this._distance_from_center = distance;
	}

	public Vector3f getCenter() {
		return (this._center);
	}

	private Vector3f vecbuffer = new Vector3f();

	private void calculateCameraPosition() {
		Vector3f center = vecbuffer.set(this._center);

		double horizontal = (this._distance_from_center * Math.cos(Math.toRadians(this.getPitch())));
		double vertical = (this._distance_from_center * Math.sin(Math.toRadians(this.getPitch())));
		double angle = this._angle_around_center;
		float offx = (float) (horizontal * Math.sin(Math.toRadians(angle)));
		float offz = (float) (horizontal * Math.cos(Math.toRadians(angle)));

		float x = center.x - offx;
		float y = (float) (center.y + vertical);
		float z = center.z - offz;

		super.setPosition(x, y, z);
		super.setYaw((float) (180 - (this._angle_around_center)));
	}
}
