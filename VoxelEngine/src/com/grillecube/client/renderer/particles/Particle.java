package com.grillecube.client.renderer.particles;

import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

public abstract class Particle {

	protected final Vector3f pos;
	protected final Vector3f rot;
	protected final Vector3f scale;

	protected final Vector3f posVel;
	protected final Vector3f rotVel;
	protected final Vector3f scaleVel;

	protected final Vector4f color;

	protected int maxhealth;
	protected int health;
	protected float healthRatio;

	private double cameraSquareDistance;

	public Particle(int health) {
		this.maxhealth = health;
		this.health = health;
		this.healthRatio = 1;
		this.pos = new Vector3f(0, 0, 0);
		this.rot = new Vector3f(0, 0, 0);
		this.scale = new Vector3f(1, 1, 1);

		this.posVel = new Vector3f(0, 0, 0);
		this.rotVel = new Vector3f(0, 0, 0);
		this.scaleVel = new Vector3f(0, 0, 0);

		this.color = new Vector4f(0.8f, 0.5f, 0.3f, 1.0f);

		this.cameraSquareDistance = 0;
	}

	public double getCameraSquareDistance() {
		return (this.cameraSquareDistance);
	}

	/** return true if the partcle is dead */
	public boolean isDead() {
		return (this.health <= 0);
	}

	public Vector4f getColor() {
		return (this.color);
	}

	public Vector3f getPosition() {
		return (this.pos);
	}

	/**
	 * a ratio in range of [0,1] which determine particle health statues (0 is
	 * dead, 1 is born)
	 */
	public final float getHealthRatio() {
		return (this.healthRatio);
	}

	public final void setRotation(Vector3f rot) {
		this.setRotation(rot.x, rot.y, rot.z);
	}

	/** set particle world location */
	public final void setPosition(float x, float y, float z) {
		this.pos.set(x, y, z);
	}

	public final void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public final void setHealth(int health) {
		this.health = health;
		this.maxhealth = health;
	}

	/** set particle world location */
	public final void setPositionVel(float x, float y, float z) {
		this.posVel.set(x, y, z);
	}

	public final Vector3f getRotation() {
		return (this.rot);
	}

	/** set particle world location */
	public void setRotation(float x, float y, float z) {
		this.rot.set(x, y, z);
	}

	/** set particle world location */
	public final void setRotationVel(float x, float y, float z) {
		this.rotVel.set(x, y, z);
	}

	/** set particle world location */
	public final void setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
	}

	public final void setScale(float f) {
		this.setScale(f, f, f);
	}

	/** set particle world location */
	public final void setScaleVel(float x, float y, float z) {
		this.scaleVel.set(x, y, z);
	}

	public final Vector3f getScale() {
		return (this.scale);
	}

	public final void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}

	public final int getMaxHealth() {
		return (this.maxhealth);
	}

	public final int getHealth() {
		return (this.health);
	}

	/** update the particle (move it) */
	public final void update(CameraProjective camera) {
		this.pos.add(this.posVel);
		this.rot.add(this.rotVel);
		this.scale.add(this.scaleVel);
		this.health--;

		this.healthRatio = this.health / (float) this.maxhealth;

		this.cameraSquareDistance = Vector3f.distanceSquare(camera.getPosition(), this.getPosition());

		this.onUpdate(camera);
	}

	protected void onUpdate(CameraProjective camera) {

	}
}
