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

import com.grillecube.client.resources.SoundManager;
import com.grillecube.client.sound.ALH;
import com.grillecube.client.sound.ALSound;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
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
import com.grillecube.common.world.events.EventEntityJump;

public abstract class Entity {

	/** block under the entity */
	private Block blockUnder;

	/** entity states , should follow the ids on EntityPhysic.IDS */
	private EntityPhysic[] physics = { new EntityPhysicMoveForward(), new EntityPhysicMoveBackward(),
			new EntityPhysicStrafeLeft(), new EntityPhysicStrafeRight(), new EntityPhysicRotateLeft(),
			new EntityPhysicRotateRight(), new EntityPhysicGravity(), new EntityPhysicAirFriction(),
			new EntityPhysicJumping() };

	/** entity's world */
	private World world;

	/** entity AI */
	private ArrayList<EntityAI> ais;

	/** entity dimensions */
	private static final float DEFAULT_DIMENSION = 1e-7f; // point
	private float width;
	private float height;
	private float depth;

	/** entity's world pos */
	private Vector3f pos;
	private Vector3f vel;
	private Vector3f acc;
	private Vector3f forces;

	/** entity's world rotation */
	private Vector3f rotation;
	private Vector3f rotVel;

	/** vector where the entity is looking at */
	private Vector3f lookVec;

	/** the entity bounding box */
	private BoundingBox boundingBox;

	/** speed for this entity, in block per seconde */
	private float speed;
	private static final float DEFAULT_SPEED = 0.2f;

	/** entity weight */
	private float weight;
	private static final float DEFAULT_WEIGHT = 1e-7f;

	/** world id */
	public static final int DEFAULT_WORLD_ID = 0;
	private int worldID = DEFAULT_WORLD_ID;

	public Entity(World world) {
		this.world = world;

		this.boundingBox = new BoundingBox();

		this.ais = new ArrayList<EntityAI>();

		this.pos = new Vector3f();
		this.vel = new Vector3f();
		this.acc = new Vector3f();
		this.forces = new Vector3f();

		this.rotation = new Vector3f();
		this.rotVel = new Vector3f();

		this.lookVec = new Vector3f();

		this.speed = DEFAULT_SPEED;
		this.weight = DEFAULT_WEIGHT;

		this.width = DEFAULT_DIMENSION;
		this.height = DEFAULT_DIMENSION;
		this.depth = DEFAULT_DIMENSION;

		this.addAI(new EntityAIIdle(this));

		// this.enablePhysic(EntityPhysic.AIR_FRICTION);
		// this.enablePhysic(EntityPhysic.GRAVITY);
	}

	public Entity() {
		this(null);
	}

	public Vector3f getViewVector() {
		return (this.lookVec);
	}

	/** called when entity spawns */
	public void onSpawn(World world) {
	}

	/** update the entity */
	public void update() {
		this.updateAI();
		this.updatePosition();
		this.updateRotation();
		this.updateBoundingBox();
		this.onUpdate();
	}

	private void updateBoundingBox() {
		this.width = 1.5f;
		this.height = 1.5f;
		this.depth = 1.5f;
		this.boundingBox.setCenterSize(this.pos, this.width, this.height, this.depth);
		this.boundingBox.setColor(1.0f, 0.0f, 0.0f, 1.0f);
	}

	private void updateAI() {
		for (int i = 0; i < this.ais.size(); i++) {
			EntityAI ai = this.ais.get(i);
			ai.update();
		}
	}

	public void addAI(EntityAI ai) {
		this.ais.add(ai);
	}

	public void removeAI(EntityAI ai) {
		this.ais.remove(ai);
	}

	/** update entity's rotation */
	private void updateRotation() {
		// update looking vector
		float f = (float) Math.cos(Math.toRadians(this.getPitch()));
		this.lookVec.setX((float) (f * Math.sin(Math.toRadians(this.getYaw()))));
		this.lookVec.setY((float) -Math.sin(Math.toRadians(this.getPitch())));
		this.lookVec.setZ((float) (f * Math.cos(Math.toRadians(this.getYaw()))));
		this.lookVec.normalise();

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

		for (EntityPhysic physic : this.physics) {
			if (physic.isSet()) {
				physic.udpate(this);
			}
		}

		float m = this.getWeight();
		float ax = this.forces.x / m;
		float ay = this.forces.y / m;
		float az = this.forces.z / m;
		this.getPositionAcceleration().set(ax, ay, az);
		this.forces.set(0, 0, 0);
	}

	/** make the entity jump (same as Entity.setState(EntityPhysic.JUMPING) */
	public void jump() {
		this.enablePhysic(EntityPhysic.JUMPING);
		VoxelEngine.instance().getResourceManager().getEventManager().invokeEvent(new EventEntityJump(this));
	}

	public boolean isJumping() {
		return this.physics[EntityPhysic.JUMPING].isSet();
	}

	/** update the value of the block under this entity */
	private void updateBlockUnder() {
		this.blockUnder = this.getWorld().getBlock(this.pos.x, this.pos.y - 1, this.pos.z);
	}

	/** add a force to this entity */
	public void addForce(Vector3f force) {
		this.forces.add(force);
	}

	public Vector3f getRotation() {
		return (this.rotation);
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
		return (this.pos);
	}

	public Vector3f getPositionVelocity() {
		return (this.vel);
	}

	public Vector3f getPositionAcceleration() {
		return (this.acc);
	}

	/** get entity world */
	public World getWorld() {
		return (this.world);
	}

	public void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public void setPosition(float x, float y, float z) {
		this.pos.set(x, y, z);
	}

	public void setWorld(World world) {
		this.world = world;
	}

	/** world ID for this entity, this is set on spawn */
	public void setWorldID(int worldID) {
		this.worldID = worldID;
	}

	public int getWorldID() {
		return (this.worldID);
	}

	public void increasePitch(float f) {
		this.rotation.x += f;
	}

	public void increaseYaw(float f) {
		this.rotation.y += f;
	}

	public void increaseRoll(float f) {
		this.rotation.z += f;
	}

	public float getSpeed() {
		return (this.speed);
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/** return true if the entity is moving */
	public boolean isMoving() {
		return (this.getPositionVelocity().x != 0 || this.getPositionVelocity().y != 0
				|| this.getPositionVelocity().z != 0);
	}

	public void setPitch(double pitch) {
		this.rotation.x = (float) pitch;
	}

	public void setYaw(double yaw) {
		this.rotation.y = (float) yaw;
	}

	public void setRoll(double roll) {
		this.rotation.z = (float) roll;
	}

	/** rotation speed vector, x == pitch, y == yaw, z == roll */
	public Vector3f getRotationSpeed() {
		return (this.rotVel);
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getWeight() {
		return (this.weight);
	}

	public boolean hasState(int stateID) {
		return (this.physics[stateID].isSet());
	}

	public void enablePhysic(int stateID) {
		this.physics[stateID].enable(this);
	}

	public void disablePhysic(int stateID) {
		this.physics[stateID].disable(this);
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
		return (this.physics[id]);
	}

	public Block getBlockUnder() {
		return (this.blockUnder);
	}

	public boolean isInAir() {
		return (this.blockUnder == Blocks.AIR);
	}

	public boolean isFalling() {
		return (this.vel.y < -1.0e-3f);
	}

	public BoundingBox getBoundingBox() {
		return (this.boundingBox);
	}

	public void playSound(ALSound sound) {
		// TODO : CLIENT SIDE ONLY
		SoundManager.instance().playSoundAt(sound, this.getPosition(), this.getPositionVelocity());
	}

	public void playSound(String filepath) {
		// TODO : CLIENT SIDE ONLY
		this.playSound(ALH.alhLoadSound(filepath));
	}

	/** set entity width */
	public void setWidth(float width) {
		this.width = width;
	}

	/** set entity width */
	public void setHeight(float height) {
		this.height = height;
	}

	/** set entity width */
	public void setDepth(float depth) {
		this.depth = depth;
	}

	/** @return : entity width */
	public float getWidth() {
		return (this.width);
	}

	/** @return : entity width */
	public float getHeight() {
		return (this.height);
	}

	/** @return : entity width */
	public float getDepth() {
		return (this.depth);
	}
}
