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
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.Timer;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.entity.ai.EntityAI;
import com.grillecube.common.world.entity.ai.EntityAIIdle;
import com.grillecube.common.world.entity.collision.AABB;
import com.grillecube.common.world.entity.collision.PhysicObject;
import com.grillecube.common.world.entity.collision.Positioneable;
import com.grillecube.common.world.entity.collision.Rotationable;
import com.grillecube.common.world.entity.collision.Sizeable;
import com.grillecube.common.world.entity.control.Control;
import com.grillecube.common.world.entity.forces.Force;
import com.grillecube.common.world.terrain.Terrain;

public abstract class Entity extends PhysicObject {

	/** block under the entity */
	private Block blockUnder;

	private static final int STATE_VISIBLE = (1 << 0);

	/** entity state */
	private int state;

	/** entity's world */
	private World world;

	/** entity AI */
	private final ArrayList<EntityAI> ais;

	/** entity forces */
	private final ArrayList<Force<Entity>> forces;

	/** entity controls */
	private final ArrayList<Control<Entity>> controls;

	/** entity timer */
	private final Timer timer;

	/** the entity bounding box */
	private final AABB aabb;

	/** entity rotation */
	private float pitch, yaw, roll;
	private float pitchVelocity, yawVelocity, rollVelocity;
	private float pitchAcceleration, yawAcceleration, rollAcceleration;

	/** vector where the entity is looking at */
	private final Vector3f lookVec;

	/** speed for this entity, in block per seconds */
	private static final float DEFAULT_SPEED = 8.0f;
	private float speed;

	/** entity mass */
	private static final float DEFAULT_MASS = 1e-7f;
	private float mass;

	/** world id */
	public static final int DEFAULT_ENTITY_ID = 0;
	private int id = DEFAULT_ENTITY_ID;

	public Entity(World world, float width, float height, float depth) {
		this.world = world;

		this.timer = new Timer();

		this.forces = new ArrayList<Force<Entity>>();
		// this.addForce(Force.GRAVITY);
		// this.addForce(Force.FRICTION);

		this.controls = new ArrayList<Control<Entity>>();

		// bounding box
		this.aabb = new AABB();

		// look vector
		this.lookVec = new Vector3f();
		this.pitch = 0;
		this.yaw = 0;
		this.roll = 0;

		// entity definition
		this.speed = DEFAULT_SPEED;
		this.mass = DEFAULT_MASS;

		// aies
		this.ais = new ArrayList<EntityAI>();
		this.addAI(new EntityAIIdle(this));

		// default states
		this.setState(Entity.STATE_VISIBLE);
	}

	public Entity(World world) {
		this(world, 1.0f, 1.0f, 1.0f);
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

	/** get this entity timer */
	public final Timer getTimer() {
		return (this.timer);
	}

	/** update the entity */
	public void update() {
		this.timer.update();

		this.updateAI();
		this.updateRotation();
		this.updateSize();
		this.updatePosition();
		this.updateBlockUnder();
		this.updateBoundingBox();
		this.onUpdate();
	}

	private final void updateBoundingBox() {

	}

	private final void updateAI() {
		for (int i = 0; i < this.ais.size(); i++) {
			EntityAI ai = this.ais.get(i);
			ai.update();
		}
	}

	public final void addAI(EntityAI ai) {
		this.ais.add(ai);
	}

	public final void removeAI(EntityAI ai) {
		this.ais.remove(ai);
	}

	/** update entity's rotation */
	private final void updateRotation() {
		// update looking vector
		double pitch = Math.toRadians(this.getRotationX());
		double yaw = Math.toRadians(this.getRotationY());
		float f = (float) Math.cos(pitch);
		this.lookVec.setX((float) (f * Math.sin(yaw)));
		this.lookVec.setY((float) -Math.sin(pitch));
		this.lookVec.setZ((float) (f * Math.cos(yaw)));
		this.lookVec.normalise();

		Rotationable.rotate(this, (float) this.timer.getDt());
	}

	private final void updateSize() {
		Sizeable.resize(this, this.timer.getDt());
	}

	/**
	 * update this entity's position, depending on forces and controls applied
	 * to it
	 * 
	 * really basis of the physic engine: the acceleration vector is reset every
	 * frame and has to be recalculated via 'Entity.addForce(Vector3f force)'
	 */
	private final void updatePosition() {

		Vector3f resultant = new Vector3f();

		// apply forces
		for (Force<Entity> force : this.forces) {
			force.updateResultant(this, resultant);
		}

		// apply controls
		for (Control<Entity> control : this.controls) {
			control.run(this, resultant);
		}
		this.controls.clear();

		// advance depending on last update
		float dt = (float) this.timer.getDt();
		float m = this.getMass();
		float ax = resultant.x * Terrain.BLOCK_TO_METER / m;
		float ay = resultant.y * Terrain.BLOCK_TO_METER / m;
		float az = resultant.z * Terrain.BLOCK_TO_METER / m;

		this.aabb.setPositionAccelerationX(ax);
		this.aabb.setPositionAccelerationY(ay);
		this.aabb.setPositionAccelerationZ(az);
		Positioneable.position(this, dt);
	}

	/** make the entity jump */
	public final void jump() {
		this.controls.add(Control.JUMP);
	}

	/** update the value of the block under this entity */
	private final void updateBlockUnder() {
		this.blockUnder = (this.getWorld() == null) ? null
				: this.getWorld().getBlock(this.getPositionX(), this.getPositionY() - 1, this.getPositionZ());
	}

	/** add a force to this entity */
	public final void addForce(Force<Entity> force) {
		this.forces.add(force);
	}

	public final void removeForce(Force<Entity> force) {
		this.forces.remove(force);
	}

	public final void addControl(Control<Entity> control) {
		this.controls.add(control);
	}

	/** update the entity */
	protected abstract void onUpdate();

	/** get entity world */
	public World getWorld() {
		return (this.world);
	}

	public final void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public final void setPosition(float x, float y, float z) {
		this.teleport(x, y, z);
	}

	public final void setWorld(World world) {
		this.world = world;
	}

	/** world ID for this entity, this is set on spawn */
	public final void setEntityID(int id) {
		this.id = id;
	}

	public final int getEntityID() {
		return (this.id);
	}

	public final float getSpeed() {
		return (this.speed);
	}

	public final void setSpeed(float speed) {
		this.speed = speed;
	}

	/** return true if the entity is moving */
	public final boolean isMoving() {
		return (Positioneable.isMoving(this));
	}

	public final boolean isRotating() {
		return (Rotationable.isRotating(this));
	}

	@Override
	public final void setMass(float mass) {
		this.mass = mass;
	}

	@Override
	public final float getMass() {
		return (this.mass);
	}

	public final boolean hasState(int state) {
		return ((this.state & state) == state);
	}

	public final void setState(int state) {
		this.state = this.state | state;
	}

	public final void setState(int state, boolean enabled) {
		if (enabled) {
			this.setState(state);
		} else {
			this.unsetState(state);
		}
	}

	public final void unsetState(int state) {
		this.state = this.state & ~state;
	}

	public final void swapState(int state) {
		this.state = this.state ^ state;
	}

	/** teleport the entity to the given position */
	public final void teleport(float x, float y, float z) {
		this.setPositionX(x);
		this.setPositionY(y);
		this.setPositionZ(z);
	}

	public Block getBlockUnder() {
		return (this.blockUnder);
	}

	public boolean isInAir() {
		return (this.blockUnder == Blocks.AIR);
	}

	public boolean isFalling() {
		return (this.getPositionVelocityY() < 0.0f);
	}

	/**
	 * @return : the entity bounding box
	 */
	public final AABB getBoundingBox() {
		return (this.aabb);
	}

	/**
	 * 
	 * move the given PhysicObject by the velocity vector (dx, dy, dz) for a
	 * time dt
	 * 
	 * 
	 * @param object
	 *            : the physicobject to move
	 * @param dx
	 * @param dy
	 * @param dz
	 * @param dt
	 */
	public static final void collisionMove(PhysicObject object, float dx, float dy, float dz, float dt) {
		// TODO : implements
		// https://www.gamedev.net/articles/programming/general-and-gameplay-programming/swept-aabb-collision-detection-and-response-r3084/
	}

	public void playSound(ALSound sound) {
		// TODO : CLIENT SIDE ONLY
		Vector3f pos = new Vector3f(this.getPositionX(), this.getPositionY(), this.getPositionZ());
		Vector3f vel = new Vector3f(this.getPositionVelocityX(), this.getPositionVelocityY(),
				this.getPositionVelocityZ());
		SoundManager.instance().playSoundAt(sound, pos, vel);
	}

	public void playSound(String filepath) {
		// TODO : CLIENT SIDE ONLY
		this.playSound(ALH.alhLoadSound(filepath));
	}

	/** set entity width */
	public final void setWidth(float width) {
		this.aabb.setSizeX(width);
	}

	public final void setHeight(float height) {
		this.aabb.setSizeY(height);
	}

	public final void setDepth(float depth) {
		this.aabb.setSizeZ(depth);
	}

	// TODO DIMENSIONABLE

	public final boolean isVisible() {
		return (this.hasState(STATE_VISIBLE));
	}

	public final boolean isJumping() {
		return (this.getPositionAccelerationY() > 0.0f);
	}

	/** rotation begins */
	@Override
	public float getRotationX() {
		return (this.pitch);
	}

	@Override
	public float getRotationY() {
		return (this.yaw);
	}

	@Override
	public float getRotationZ() {
		return (this.roll);
	}

	@Override
	public float getRotationVelocityX() {
		return (this.pitchVelocity);
	}

	@Override
	public float getRotationVelocityY() {
		return (this.yawVelocity);
	}

	@Override
	public float getRotationVelocityZ() {
		return (this.rollVelocity);
	}

	@Override
	public float getRotationAccelerationX() {
		return (this.pitchAcceleration);
	}

	@Override
	public float getRotationAccelerationY() {
		return (this.yawAcceleration);
	}

	@Override
	public float getRotationAccelerationZ() {
		return (this.rollAcceleration);
	}

	@Override
	public void setRotationX(float x) {
		this.pitch = x;
	}

	@Override
	public void setRotationY(float y) {
		this.yaw = y;
	}

	@Override
	public void setRotationZ(float z) {
		this.roll = z;
	}

	@Override
	public void setRotationVelocityX(float vx) {
		this.pitchVelocity = vx;
	}

	@Override
	public void setRotationVelocityY(float vy) {
		this.yawVelocity = vy;
	}

	@Override
	public void setRotationVelocityZ(float vz) {
		this.rollVelocity = vz;
	}

	@Override
	public void setRotationAccelerationX(float ax) {
		this.pitchAcceleration = ax;
	}

	@Override
	public void setRotationAccelerationY(float ay) {
		this.yawAcceleration = ay;
	}

	@Override
	public void setRotationAccelerationZ(float az) {
		this.rollAcceleration = az;
	}

	/** position begins */

	@Override
	public float getPositionX() {
		return (this.aabb.getPositionX());
	}

	@Override
	public float getPositionY() {
		return (this.aabb.getPositionY());
	}

	@Override
	public float getPositionZ() {
		return (this.aabb.getPositionZ());
	}

	@Override
	public float getPositionVelocityX() {
		return (this.aabb.getPositionVelocityX());
	}

	@Override
	public float getPositionVelocityY() {
		return (this.aabb.getPositionVelocityY());
	}

	@Override
	public float getPositionVelocityZ() {
		return (this.aabb.getPositionVelocityZ());
	}

	@Override
	public float getPositionAccelerationX() {
		return (this.aabb.getPositionAccelerationX());
	}

	@Override
	public float getPositionAccelerationY() {
		return (this.aabb.getPositionAccelerationY());
	}

	@Override
	public float getPositionAccelerationZ() {
		return (this.aabb.getPositionAccelerationZ());
	}

	@Override
	public void setPositionX(float x) {
		this.aabb.setPositionX(x);
	}

	@Override
	public void setPositionY(float y) {
		this.aabb.setPositionY(y);
	}

	@Override
	public void setPositionZ(float z) {
		this.aabb.setPositionZ(z);
	}

	@Override
	public void setPositionVelocityX(float vx) {
		this.aabb.setPositionVelocityX(vx);
	}

	@Override
	public void setPositionVelocityY(float vy) {
		this.aabb.setPositionVelocityY(vy);
	}

	@Override
	public void setPositionVelocityZ(float vz) {
		this.aabb.setPositionVelocityZ(vz);
	}

	@Override
	public void setPositionAccelerationX(float ax) {
		this.aabb.setPositionAccelerationX(ax);
	}

	@Override
	public void setPositionAccelerationY(float ay) {
		this.aabb.setPositionAccelerationY(ay);
	}

	@Override
	public void setPositionAccelerationZ(float az) {
		this.aabb.setPositionAccelerationZ(az);
	}

	/** size begins */

	@Override
	public float getSizeX() {
		return (this.aabb.getSizeX());
	}

	@Override
	public float getSizeY() {
		return (this.aabb.getSizeY());
	}

	@Override
	public float getSizeZ() {
		return (this.aabb.getSizeZ());
	}

	@Override
	public float getSizeVelocityX() {
		return (this.aabb.getSizeVelocityX());
	}

	@Override
	public float getSizeVelocityY() {
		return (this.aabb.getSizeVelocityY());
	}

	@Override
	public float getSizeVelocityZ() {
		return (this.aabb.getSizeVelocityZ());
	}

	@Override
	public float getSizeAccelerationX() {
		return (this.aabb.getSizeAccelerationX());
	}

	@Override
	public float getSizeAccelerationY() {
		return (this.aabb.getSizeAccelerationY());
	}

	@Override
	public float getSizeAccelerationZ() {
		return (this.aabb.getSizeAccelerationZ());
	}

	@Override
	public void setSizeX(float x) {
		this.aabb.setSizeX(x);
	}

	@Override
	public void setSizeY(float y) {
		this.aabb.setSizeY(y);
	}

	@Override
	public void setSizeZ(float z) {
		this.aabb.setSizeZ(z);
	}

	@Override
	public void setSizeVelocityX(float vx) {
		this.aabb.setSizeVelocityX(vx);
	}

	@Override
	public void setSizeVelocityY(float vy) {
		this.aabb.setSizeVelocityY(vy);
	}

	@Override
	public void setSizeVelocityZ(float vz) {
		this.aabb.setSizeVelocityZ(vz);
	}

	@Override
	public void setSizeAccelerationX(float ax) {
		this.aabb.setSizeAccelerationX(ax);
	}

	@Override
	public void setSizeAccelerationY(float ay) {
		this.aabb.setSizeAccelerationY(ay);
	}

	@Override
	public void setSizeAccelerationZ(float az) {
		this.aabb.setSizeAccelerationZ(az);
	}
}
