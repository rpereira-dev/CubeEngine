/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.common.world.entity;

import java.util.ArrayList;

import com.grillecube.client.renderer.model.BoundingBox;
import com.grillecube.client.sound.ALH;
import com.grillecube.client.sound.ALSound;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.event.world.EventEntityJump;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.SoundManager;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.entity.ai.EntityAI;
import com.grillecube.common.world.entity.ai.EntityAIIdle;
import com.grillecube.common.world.entity.physic.EntityPhysic;
import com.grillecube.common.world.entity.physic.EntityPhysicAirFriction;
import com.grillecube.common.world.entity.physic.EntityPhysicGravity;
import com.grillecube.common.world.entity.physic.EntityPhysicJumping;
import com.grillecube.common.world.entity.physic.EntityPhysicMoveBackward;
import com.grillecube.common.world.entity.physic.EntityPhysicMoveForward;
import com.grillecube.common.world.entity.physic.EntityPhysicRotateLeft;
import com.grillecube.common.world.entity.physic.EntityPhysicRotateRight;
import com.grillecube.common.world.entity.physic.EntityPhysicStrafeLeft;
import com.grillecube.common.world.entity.physic.EntityPhysicStrafeRight;

public abstract class Entity {

	/** block under the entity */
	private Block _block_under;

	/** entity states , should follow the ids on EntityPhysic.IDS */
	private EntityPhysic[] _physics = { new EntityPhysicMoveForward(), new EntityPhysicMoveBackward(),
			new EntityPhysicStrafeLeft(), new EntityPhysicStrafeRight(), new EntityPhysicRotateLeft(),
			new EntityPhysicRotateRight(), new EntityPhysicGravity(), new EntityPhysicAirFriction(),
			new EntityPhysicJumping() };

	/** entity's world */
	private World _world;

	/** entity AI */
	private ArrayList<EntityAI> _ais;

	/** entity's world pos */
	private Vector3f _pos;
	private Vector3f _pos_velocity;
	private Vector3f _pos_acceleration;
	private Vector3f _forces;

	/** entity's world rotation */
	private Vector3f _rotation;
	private Vector3f _rotation_speed;

	/** vector where the entity is looking at */
	private Vector3f _look_vec;

	/** speed for this entity, in block per seconde */
	private float _speed;
	private static final float DEFAULT_SPEED = 0.2f;

	/** entity weight */
	private float _weight;
	private static final float DEFAULT_WEIGHT = 1e-7f;

	/** entity height */
	private float _height;
	private static final float DEFAULT_HEIGHT = 1e-7f; // point

	/** world id */
	public static final int DEFAULT_WORLD_ID = 0;
	private int _worldID = DEFAULT_WORLD_ID;

	public Entity(World world) {
		this._world = world;

		this._ais = new ArrayList<EntityAI>();

		this._pos = new Vector3f();
		this._pos_velocity = new Vector3f();
		this._pos_acceleration = new Vector3f();
		this._forces = new Vector3f();

		this._rotation = new Vector3f();
		this._rotation_speed = new Vector3f();

		this._look_vec = new Vector3f();

		this._speed = DEFAULT_SPEED;
		this._weight = DEFAULT_WEIGHT;
		this._height = DEFAULT_HEIGHT;

		this.addAI(new EntityAIIdle(this));
	}

	public Entity() {
		this(null);
	}

	public Vector3f getViewVector() {
		return (this._look_vec);
	}

	/** called when entity spawns */
	public void onSpawn(World world) {
	}

	/** update the entity */
	public void update() {
		this.updateAI();
		this.updatePosition();
		this.updateRotation();
		this.onUpdate();
	}

	private void updateAI() {
		for (int i = 0; i < this._ais.size(); i++) {
			EntityAI ai = this._ais.get(i);
			ai.update();
		}
	}

	public void addAI(EntityAI ai) {
		this._ais.add(ai);
	}

	public void removeAI(EntityAI ai) {
		this._ais.remove(ai);
	}

	/** update entity's rotation */
	private void updateRotation() {
		// update looking vector
		float f = (float) Math.cos(Math.toRadians(this.getPitch()));
		this._look_vec.setX((float) (f * Math.sin(Math.toRadians(this.getYaw()))));
		this._look_vec.setY((float) -Math.sin(Math.toRadians(this.getPitch())));
		this._look_vec.setZ((float) (f * Math.cos(Math.toRadians(this.getYaw()))));
		this._look_vec.normalise();

		this.increasePitch(this.getRotationSpeed().x);
		this.increaseYaw(this.getRotationSpeed().y);
		this.increaseRoll(this.getRotationSpeed().z);
	}

	/**
	 * update this entity's position
	 * 
	 * really basis of the physic engine: the acceleration vector is reset every
	 * frame and has to be recalculated via 'Entity.addForce(Vector3f force)'
	 */
	private void updatePosition() {
		this.updateForces(); // update forces
		this.getPositionVelocity().add(this.getPositionAcceleration());
		this.move(this.getPositionVelocity());
		this.updateBlockUnder();
	}

	/** update forces applied to this entity */
	private void updateForces() {

		for (EntityPhysic physic : this._physics) {
			if (physic.isSet()) {
				physic.udpate(this);
			}
		}

		float m = this.getWeight();
		float ax = this._forces.x / m;
		float ay = this._forces.y / m;
		float az = this._forces.z / m;
		this.getPositionAcceleration().set(ax, ay, az);
		this._forces.set(0, 0, 0);
	}

	/** make the entity jump (same as Entity.setState(EntityPhysic.JUMPING) */
	public void jump() {
		this.enablePhysic(EntityPhysic.JUMPING);
		VoxelEngine.instance().getResourceManager().getEventManager().invokeEvent(new EventEntityJump(this));
	}

	public boolean isJumping() {
		return this._physics[EntityPhysic.JUMPING].isSet();
	}

	/** update the value of the block under this entity */
	private void updateBlockUnder() {
		this._block_under = this.getWorld().getBlock(this._pos.x, this._pos.y - 1, this._pos.z);
	}

	/** add a force to this entity */
	public void addForce(Vector3f force) {
		this._forces.add(force);
	}

	public Vector3f getRotation() {
		return (this._rotation);
	}

	public float getPitch() {
		return (this.getRotation().x);
	}

	public float getYaw() {
		return (this.getRotation().y);
	}

	public float getRoll() {
		return (this.getRotation().z);
	}

	/** update the entity */
	protected abstract void onUpdate();

	/** get entity position */
	public Vector3f getPosition() {
		return (this._pos);
	}

	public Vector3f getPositionVelocity() {
		return (this._pos_velocity);
	}

	public Vector3f getPositionAcceleration() {
		return (this._pos_acceleration);
	}

	/** get entity world */
	public World getWorld() {
		return (this._world);
	}

	public void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public void setPosition(float x, float y, float z) {
		this._pos.set(x, y, z);
	}

	public void setWorld(World world) {
		this._world = world;
	}

	/** world ID for this entity, this is set on spawn */
	public void setWorldID(int worldID) {
		this._worldID = worldID;
	}

	public int getWorldID() {
		return (this._worldID);
	}

	public void increasePitch(float f) {
		this._rotation.x += f;
	}

	public void increaseYaw(float f) {
		this._rotation.y += f;
	}

	public void increaseRoll(float f) {
		this._rotation.z += f;
	}

	public float getSpeed() {
		return (this._speed);
	}

	public void setSpeed(float speed) {
		this._speed = speed;
	}

	/** return true if the entity is moving */
	public boolean isMoving() {
		return (this.getPositionVelocity().x != 0 || this.getPositionVelocity().y != 0
				|| this.getPositionVelocity().z != 0);
	}

	public void setPitch(double pitch) {
		this._rotation.x = (float) pitch;
	}

	public void setYaw(double yaw) {
		this._rotation.y = (float) yaw;
	}

	public void setRoll(double roll) {
		this._rotation.z = (float) roll;
	}

	/** rotation speed vector, x == pitch, y == yaw, z == roll */
	public Vector3f getRotationSpeed() {
		return (this._rotation_speed);
	}

	public void setWeight(float weight) {
		this._weight = weight;
	}

	public float getWeight() {
		return (this._weight);
	}

	public void setHeight(float height) {
		this._height = height;
	}

	public float getHeight() {
		return (this._height);
	}

	public boolean hasState(int stateID) {
		return (this._physics[stateID].isSet());
	}

	public void enablePhysic(int stateID) {
		this._physics[stateID].enable(this);
	}

	public void disablePhysic(int stateID) {
		this._physics[stateID].disable(this);
	}

	/**
	 * try to move the entity of the delta position vector 'move'
	 * 
	 * @param move:
	 *            movement vector
	 * @return true if the entity moved, false else way
	 */
	public boolean move(Vector3f move) {
		return (this.move(move.x, move.y, move.z));
	}

	public boolean move(float x, float y, float z) {
		this.getPosition().add(x, y, z);
		return (true);
	}

	public EntityPhysic getPhysic(int id) {
		return (this._physics[id]);
	}

	public Block getBlockUnder() {
		return (this._block_under);
	}

	public boolean isInAir() {
		return (this._block_under == Blocks.AIR);
	}

	public boolean isFalling() {
		return (this._pos_velocity.y < -1.0e-3f);
	}

	public BoundingBox getBoundingBox() {
		return (null);
	}

	public void playSound(ALSound sound) {
		SoundManager.instance().playSoundAt(sound, this.getPosition(), this.getPositionVelocity());
	}

	public void playSound(String filepath) {
		this.playSound(ALH.alhLoadSound(filepath));
	}
}
