package com.grillecube.client.renderer.particles;

import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.entity.collision.PhysicObject;
import com.grillecube.common.world.entity.collision.Positioneable;
import com.grillecube.common.world.entity.collision.Rotationable;
import com.grillecube.common.world.entity.collision.Sizeable;

public class Particle extends PhysicObject {

	protected final Vector3f pos;
	protected final Vector3f rot;
	protected final Vector3f size;

	protected final Vector3f posVel;
	protected final Vector3f rotVel;
	protected final Vector3f sizeVel;

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
		this.size = new Vector3f(1, 1, 1);

		this.posVel = new Vector3f(0, 0, 0);
		this.rotVel = new Vector3f(0, 0, 0);
		this.sizeVel = new Vector3f(0, 0, 0);

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
	
	public final Vector3f getSize() {
		return (this.size);
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
		float dt = 1 / 60.0f; // TODO real timer
		Positioneable.velocity(this, dt);
		Positioneable.position(this, dt);
		Rotationable.rotate(this, dt);
		Sizeable.resize(this, dt);

		this.health--;
		this.healthRatio = this.health / (float) this.maxhealth;
		this.cameraSquareDistance = Vector3f.distanceSquare(camera.getPosition(), this.getPosition());

		this.onUpdate(camera);
	}

	protected void onUpdate(CameraProjective camera) {

	}

	@Override
	public float getPositionX() {
		return (this.pos.x);
	}

	@Override
	public float getPositionY() {
		return (this.pos.y);
	}

	@Override
	public float getPositionZ() {
		return (this.pos.z);
	}

	@Override
	public float getPositionVelocityX() {
		return (this.posVel.x);
	}

	@Override
	public float getPositionVelocityY() {
		return (this.posVel.y);
	}

	@Override
	public float getPositionVelocityZ() {
		return (this.posVel.z);
	}

	@Override
	public float getPositionAccelerationX() {
		return (0);
	}

	@Override
	public float getPositionAccelerationY() {
		return (0);
	}

	@Override
	public float getPositionAccelerationZ() {
		return (0);
	}

	@Override
	public void setPositionX(float x) {
		this.pos.x = x;
	}

	@Override
	public void setPositionY(float y) {
		this.pos.y = y;
	}

	@Override
	public void setPositionZ(float z) {
		this.pos.z = z;
	}

	@Override
	public void setPositionVelocityX(float vx) {
		this.posVel.x = vx;
	}

	@Override
	public void setPositionVelocityY(float vy) {
		this.posVel.y = vy;
	}

	@Override
	public void setPositionVelocityZ(float vz) {
		this.posVel.z = vz;
	}

	@Override
	public void setPositionAccelerationX(float ax) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPositionAccelerationY(float ay) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPositionAccelerationZ(float az) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getRotationX() {
		return (this.rot.x);
	}

	@Override
	public float getRotationY() {
		return (this.rot.y);
	}

	@Override
	public float getRotationZ() {
		return (this.rot.z);
	}

	@Override
	public float getRotationVelocityX() {
		return (this.rotVel.x);
	}

	@Override
	public float getRotationVelocityY() {
		return (this.rotVel.y);
	}

	@Override
	public float getRotationVelocityZ() {
		return (this.rotVel.z);
	}

	@Override
	public float getRotationAccelerationX() {
		return (0);
	}

	@Override
	public float getRotationAccelerationY() {
		return (0);
	}

	@Override
	public float getRotationAccelerationZ() {
		return (0);
	}

	@Override
	public void setRotationX(float x) {
		this.rot.x = x;
	}

	@Override
	public void setRotationY(float y) {
		this.rot.y = y;
	}

	@Override
	public void setRotationZ(float z) {
		this.rot.z = z;
	}

	@Override
	public void setRotationVelocityX(float vx) {
		this.rotVel.x = vx;
	}

	@Override
	public void setRotationVelocityY(float vy) {
		this.rotVel.y = vy;
	}

	@Override
	public void setRotationVelocityZ(float vz) {
		this.rotVel.z = vz;
	}

	@Override
	public void setRotationAccelerationX(float ax) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationAccelerationY(float ay) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRotationAccelerationZ(float az) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getSizeX() {
		return (this.size.x);
	}

	@Override
	public float getSizeY() {
		return (this.size.y);
	}

	@Override
	public float getSizeZ() {
		return (this.size.z);
	}

	@Override
	public float getSizeVelocityX() {
		return (this.sizeVel.x);
	}

	@Override
	public float getSizeVelocityY() {
		return (this.sizeVel.y);
	}

	@Override
	public float getSizeVelocityZ() {
		return (this.sizeVel.z);
	}

	@Override
	public float getSizeAccelerationX() {
		return (0);
	}

	@Override
	public float getSizeAccelerationY() {
		return (0);
	}

	@Override
	public float getSizeAccelerationZ() {
		return (0);
	}

	@Override
	public void setSizeX(float x) {
		this.size.x = x;
	}

	@Override
	public void setSizeY(float y) {
		this.size.y = y;
	}

	@Override
	public void setSizeZ(float z) {
		this.size.z = z;
	}

	@Override
	public void setSizeVelocityX(float vx) {
		this.sizeVel.x = vx;
	}

	@Override
	public void setSizeVelocityY(float vy) {
		this.sizeVel.y = vy;
	}

	@Override
	public void setSizeVelocityZ(float vz) {
		this.sizeVel.z = vz;
	}

	@Override
	public void setSizeAccelerationX(float ax) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeAccelerationY(float ay) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSizeAccelerationZ(float az) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getMass() {
		return (0.0001f); // ~ 10 grains of sand
	}

	@Override
	public void setMass(float mass) {
		throw new UnsupportedOperationException("Can't modify particle masses");
	}
}
