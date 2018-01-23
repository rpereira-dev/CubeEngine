package com.grillecube.client.renderer.camera;

import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;

public abstract class CameraView extends Camera {

	/** position */
	private final Vector3f pos;
	private final Vector3f rot;

	/** view informations */
	private Matrix4f viewMatrix;
	private Vector3f viewVec;

	public CameraView() {
		super();
		this.viewVec = new Vector3f();
		this.viewMatrix = new Matrix4f();
		this.pos = new Vector3f();
		this.rot = new Vector3f();
	}

	@Override
	public void update() {
		super.update();
		this.createViewVector(this.getViewVector());
		this.createViewMatrix(this.getViewMatrix());
	}

	protected void createViewMatrix(Matrix4f dst) {
		dst.setIdentity();
		dst.rotate(this.getRotX(), Vector3f.AXIS_X);
		dst.rotate(this.getRotY(), Vector3f.AXIS_Y);
		dst.rotate(this.getRotZ(), Vector3f.AXIS_Z);
		dst.translate(this.getPosition().negate(null));
	}

	/**
	 * create the view vector (direction on which the camera is looking at,
	 * normalized)
	 */
	protected void createViewVector(Vector3f dst) {
		float x, y, z;
		x = (float) (Math.sin(this.getRotZ()) * Math.sin(-this.getRotX()));
		y = (float) (Math.cos(this.getRotZ()) * Math.sin(-this.getRotX()));
		z = (float) (-Math.cos(-this.getRotX()));
		dst.set(x, y, z).normalise();
	}

	public Vector3f getPosition() {
		return (this.pos);
	}

	public void setPosition(float x, float y, float z) {
		this.pos.set(x, y, z);
	}

	public void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public Vector3f getViewVector() {
		return (this.viewVec);
	}

	public Matrix4f getViewMatrix() {
		return (this.viewMatrix);
	}

	public void move(Vector3f dpos, float dt) {
		this.setPosition(this.pos.x + dpos.x, this.pos.y + dpos.y, this.pos.z + dpos.z);
	}

	public final Vector3f getRot() {
		return (this.rot);
	}

	public final void setRot(Vector3f rot) {
		this.rot.set(rot);
	}

	public float getRotX() {
		return (this.rot.x);
	}

	public float getRotY() {
		return (this.rot.y);
	}

	public float getRotZ() {
		return (this.rot.z);
	}

	public void setRotX(float x) {
		this.rot.x = x;
	}

	public void setRotY(float y) {
		this.rot.y = y;
	}

	public void setRotZ(float z) {
		this.rot.z = z;
	}

	public void increaseRotX(float x) {
		this.rot.x += x;
	}

	public void increaseRotY(float y) {
		this.rot.y += y;
	}

	public void increaseRotZ(float z) {
		this.rot.z += z;
	}

}
