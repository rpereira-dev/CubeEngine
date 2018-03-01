package com.grillecube.common.world.physic;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.grillecube.common.world.World;

public abstract class WorldObjectEntity extends WorldObject {

	/** the rigid body of the entity */
	private final RigidBody rigidBody;
	private final Transform transform;
	private Vector3f position;
	private final Quat4f rotation;

	/** position / rotation */
	private final Vector3f positionVelocity;
	private final Vector3f positionAcceleration;
	private final Vector3f rotationVelocity;
	private final Vector3f rotationAcceleration;

	/** size */
	private final float width, height, depth;

	/** mass */
	private float mass;

	/** vector where the entity is looking at */
	private final Vector3f lookVec;

	public WorldObjectEntity(World world, float mass, float width, float height, float depth) {
		super(world);
		this.mass = mass;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.rigidBody = new RigidBody(mass, new DefaultMotionState(),
				new BoxShape(new Vector3f(width * 0.5f, height * 0.5f, depth * 0.5f)));
		this.lookVec = new Vector3f();
		this.transform = new Transform();
		this.rigidBody.getWorldTransform(this.transform);
		this.position = this.transform.origin;
		this.rotation = new Quat4f();
		this.transform.getRotation(this.rotation);
		this.positionVelocity = new Vector3f();
		this.positionAcceleration = new Vector3f();
		this.rotationVelocity = new Vector3f();
		this.rotationAcceleration = new Vector3f();

	}

	public final RigidBody getRigidBody() {
		return (this.rigidBody);
	}

	public Vector3f getViewVector() {
		return (this.lookVec);
	}

	@Override
	public final float getMass() {
		return (this.mass);
	}

	@Override
	public final void setMass(float mass) {
		this.mass = mass;
		// TODO : intertia
		this.rigidBody.setMassProps(mass, new Vector3f(0, 0, 0));
	}

	/***************************************************************************************/
	/** position begins */
	/***************************************************************************************/
	@Override
	protected void postWorldUpdate(double dt) {
		super.postWorldUpdate(dt);
		float x = this.getPositionX();
		float y = this.getPositionY();
		float z = this.getPositionZ();
		float vx = this.getPositionVelocityX();
		float vy = this.getPositionVelocityY();
		float vz = this.getPositionVelocityZ();

		float rx = this.getRotationX();
		float ry = this.getRotationY();
		float rz = this.getRotationZ();
		float vrx = this.getRotationVelocityX();
		float vry = this.getRotationVelocityY();
		float vrz = this.getRotationVelocityZ();

		this.rigidBody.getMotionState().getWorldTransform(this.transform);
		this.position = this.transform.origin;
		this.transform.getRotation(this.rotation);

		// TODO : optimize this by a * 1/dt, but compiler should do it alone
		this.setPositionVelocityX((float) ((this.getPositionX() - x) / dt));
		this.setPositionVelocityX((float) ((this.getPositionY() - y) / dt));
		this.setPositionVelocityX((float) ((this.getPositionZ() - z) / dt));
		this.setPositionAccelerationX((float) ((this.getPositionVelocityX() - vx) / dt));
		this.setPositionAccelerationY((float) ((this.getPositionVelocityY() - vy) / dt));
		this.setPositionAccelerationZ((float) ((this.getPositionVelocityZ() - vz) / dt));

		this.setRotationVelocityX((float) ((this.getRotationX() - rx) / dt));
		this.setRotationVelocityX((float) ((this.getRotationY() - ry) / dt));
		this.setRotationVelocityX((float) ((this.getRotationZ() - rz) / dt));
		this.setRotationAccelerationX((float) ((this.getRotationVelocityX() - vrx) / dt));
		this.setRotationAccelerationY((float) ((this.getRotationVelocityY() - vry) / dt));
		this.setRotationAccelerationZ((float) ((this.getRotationVelocityZ() - vrz) / dt));

		// update looking vector
		this.lookVec.x = (float) (Math.sin(this.getRotationZ()) * Math.sin(-this.getRotationX()));
		this.lookVec.y = (float) (Math.cos(this.getRotationZ()) * Math.sin(-this.getRotationX()));
		this.lookVec.z = (float) (-Math.cos(-this.getRotationX()));
		this.lookVec.normalize();
	}

	/** return true if the entity is moving */
	public final boolean isMoving() {
		return (Positioneable.isMoving(this));
	}

	public final boolean isRotating() {
		return (Rotationable.isRotating(this));
	}

	@Override
	public float getPositionX() {
		return (this.position.x);
	}

	@Override
	public float getPositionY() {
		return (this.position.y);
	}

	@Override
	public float getPositionZ() {
		return (this.position.z);
	}

	@Override
	public float getPositionVelocityX() {
		return (this.positionVelocity.x);
	}

	@Override
	public float getPositionVelocityY() {
		return (this.positionVelocity.y);
	}

	@Override
	public float getPositionVelocityZ() {
		return (this.positionVelocity.z);
	}

	@Override
	public float getPositionAccelerationX() {
		return (this.positionAcceleration.x);
	}

	@Override
	public float getPositionAccelerationY() {
		return (this.positionAcceleration.y);
	}

	@Override
	public float getPositionAccelerationZ() {
		return (this.positionAcceleration.z);
	}

	@Override
	public void setPositionX(float x) {
		this.setPosition(x, this.getPositionY(), this.getPositionZ());
	}

	@Override
	public void setPositionY(float y) {
		this.setPosition(this.getPositionX(), y, this.getPositionZ());
	}

	@Override
	public void setPositionZ(float z) {
		this.setPosition(this.getPositionX(), this.getPositionY(), z);
	}

	@Override
	public void setPosition(float x, float y, float z) {
		this.setPositionAndRotation(x, y, z, this.getRotationX(), this.getRotationY(), this.getRotationZ());
	}

	public final void setPositionAndRotation(float x, float y, float z, float rx, float ry, float rz) {
		Vector3f pos = new Vector3f(this.transform.origin);
		Quat4f q = new Quat4f();
		q.set(rx, ry, rz, 1.0f); // TODO quaternion or euler?
		this.transform.getRotation(q);
		this.transform.setIdentity();
		this.transform.transform(pos);
		this.transform.setRotation(q);
		this.rigidBody.getMotionState().setWorldTransform(this.transform);
	}

	@Override
	public void setPositionVelocityX(float vx) {
		this.positionVelocity.x = vx;
	}

	@Override
	public void setPositionVelocityY(float vy) {
		this.positionVelocity.y = vy;
	}

	@Override
	public void setPositionVelocityZ(float vz) {
		this.positionVelocity.z = vz;
	}

	@Override
	public void setPositionAccelerationX(float ax) {
		this.positionAcceleration.x = ax;
	}

	@Override
	public void setPositionAccelerationY(float ay) {
		this.positionAcceleration.y = ay;
	}

	@Override
	public void setPositionAccelerationZ(float az) {
		this.positionAcceleration.z = az;
	}

	/***************************************************************************************/
	/** rotation begins */
	/***************************************************************************************/

	@Override
	public float getRotationX() {
		return (this.rotation.x);
	}

	@Override
	public float getRotationY() {
		return (this.rotation.y);
	}

	@Override
	public float getRotationZ() {
		return (this.rotation.z);
	}

	@Override
	public float getRotationVelocityX() {
		return (this.rotationVelocity.x);
	}

	@Override
	public float getRotationVelocityY() {
		return (this.rotationVelocity.y);
	}

	@Override
	public float getRotationVelocityZ() {
		return (this.rotationVelocity.z);
	}

	@Override
	public float getRotationAccelerationX() {
		return (this.rotationAcceleration.x);
	}

	@Override
	public float getRotationAccelerationY() {
		return (this.rotationAcceleration.y);
	}

	@Override
	public float getRotationAccelerationZ() {
		return (this.rotationAcceleration.z);
	}

	@Override
	public void setRotation(float rx, float ry, float rz) {
		this.setPositionAndRotation(this.getPositionX(), this.getPositionY(), this.getPositionZ(), rx, ry, rz);
	}

	@Override
	public void setRotationX(float x) {
		this.setRotation(x, this.getRotationY(), this.getRotationZ());
	}

	@Override
	public void setRotationY(float y) {
		this.setRotation(this.getRotationX(), y, this.getRotationZ());
	}

	@Override
	public void setRotationZ(float z) {
		this.setRotation(this.getRotationX(), this.getRotationY(), z);
	}

	@Override
	public void setRotationVelocityX(float vx) {
		this.rotationVelocity.x = vx;
	}

	@Override
	public void setRotationVelocityY(float vy) {
		this.rotationVelocity.y = vy;
	}

	@Override
	public void setRotationVelocityZ(float vz) {
		this.rotationVelocity.z = vz;
	}

	@Override
	public void setRotationAccelerationX(float ax) {
		this.rotationAcceleration.x = ax;
	}

	@Override
	public void setRotationAccelerationY(float ay) {
		this.rotationAcceleration.y = ay;
	}

	@Override
	public void setRotationAccelerationZ(float az) {
		this.rotationAcceleration.z = az;
	}

	/***************************************************************************************/
	/** position begins */
	/***************************************************************************************/

	@Override
	public float getSizeX() {
		return (this.width);
	}

	@Override
	public float getSizeY() {
		return (this.height);
	}

	@Override
	public float getSizeZ() {
		return (this.depth);
	}

	@Override
	public float getSizeVelocityX() {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public float getSizeVelocityY() {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public float getSizeVelocityZ() {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public float getSizeAccelerationX() {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public float getSizeAccelerationY() {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public float getSizeAccelerationZ() {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeX(float x) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeY(float y) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeZ(float z) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSize(float x, float y, float z) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeVelocityX(float vx) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeVelocityY(float vy) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeVelocityZ(float vz) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeAccelerationX(float ax) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeAccelerationY(float ay) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

	@Override
	public void setSizeAccelerationZ(float az) {
		throw new UnsupportedOperationException("Cannot dynamically resize an entity!");
	}

}
