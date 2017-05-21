package com.grillecube.client.renderer.camera;

import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;

public abstract class CameraView extends Camera {

	/** position */
	private Vector3f _pos;
	private Vector3f _rot;

	/** velocities */
	private Vector3f _pos_vel;
	private Vector3f _rot_vel;

	/** speeds */
	private float _speed;
	private float _rot_speed;

	/** view informations */
	private Matrix4f _view_matrix;
	private Vector3f _view_vec;

	public CameraView() {
		super();
		this._view_vec = new Vector3f();
		this._view_matrix = new Matrix4f();
		this._pos = new Vector3f();
		this._rot = new Vector3f();
		this._pos_vel = new Vector3f();
		this._rot_vel = new Vector3f();
	}

	@Override
	public void update() {
		super.update();
		this.createViewVector(this.getViewVector());
		this.createViewMatrix(this.getViewMatrix());
		this.move(this._pos_vel);
		this.rotate(this._rot_vel);
	}

	protected void createViewMatrix(Matrix4f dst) {
		dst.setIdentity();
		dst.rotate((float) Math.toRadians(this.getPitch()), Vector3f.AXIS_X);
		dst.rotate((float) Math.toRadians(this.getYaw()), Vector3f.AXIS_Y);
		dst.rotate((float) Math.toRadians(this.getRoll()), Vector3f.AXIS_Z);
		dst.translate(this.getPosition().negate(null));
	}

	/**
	 * create the view vector (direction on which the camera is looking at,
	 * normalized)
	 */
	protected void createViewVector(Vector3f dst) {

		float f = (float) Math.cos(Math.toRadians(this.getPitch()));
		this._view_vec.setX((float) (f * Math.sin(Math.toRadians(this.getYaw()))));
		this._view_vec.setY((float) -Math.sin(Math.toRadians(this.getPitch())));
		this._view_vec.setZ((float) (-f * Math.cos(Math.toRadians(this.getYaw()))));
		this._view_vec.normalise();
	}

	public Vector3f getPosition() {
		return (this._pos);
	}

	public void setRotationVelocity(float x, float y, float z) {
		this._rot_vel.set(x, y, z);
	}

	public void setPositionVelocity(float x, float y, float z) {
		this._pos_vel.set(x, y, z);
	}

	public void setPosition(float x, float y, float z) {
		this._pos.set(x, y, z);
	}

	public void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public Vector3f getViewVector() {
		return (this._view_vec);
	}

	public Matrix4f getViewMatrix() {
		return (this._view_matrix);
	}

	protected Vector3f getPositionVelocity() {
		return (this._pos_vel);
	}

	protected Vector3f getRotationVelocity() {
		return (this._rot_vel);
	}

	public void move(Vector3f dpos) {
		this.setPosition(this._pos.x + dpos.x * this._speed, this._pos.y + dpos.y * this._speed,
				this._pos.z + dpos.z * this._speed);
	}

	public void rotate(Vector3f drot) {
		this.setRotation(this._rot.x + drot.x * this._rot_speed, this._rot.y + drot.y * this._rot_speed,
				this._rot.z + drot.z * this._rot_speed);
	}

	private void setRotation(float x, float y, float z) {
		this._rot.set(x, y, z);
	}

	public void setSpeed(float f) {
		this._speed = f;
	}

	public void setRotSpeed(float f) {
		this._rot_speed = f;
	}

	public float getPitch() {
		return (this._rot.x);
	}

	public float getYaw() {
		return (this._rot.y);
	}

	public float getRoll() {
		return (this._rot.z);
	}

	public void invertPitch() {
		this._rot.x = -this._rot.x;
	}

	public void invertYaw() {
		this._rot.y = -this._rot.y;
	}

	public void invertRoll() {
		this._rot.z = -this._rot.z;
	}

	public void setPitch(float pitch) {
		this._rot.x = pitch;
	}

	public void setYaw(float yaw) {
		this._rot.y = yaw;
	}

	public void setRoll(float roll) {
		this._rot.z = roll;
	}

	public void increasePitch(float pitch) {
		this._rot.x += pitch;
	}

	public void increaseYaw(float yaw) {
		this._rot.y += yaw;
	}

	public void increaseRoll(float roll) {
		this._rot.z += roll;
	}
}
