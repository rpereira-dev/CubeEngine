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

import com.grillecube.common.event.world.entity.EventEntityPlaySound;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.entity.ai.EntityAI;
import com.grillecube.common.world.entity.ai.EntityAIIdle;
import com.grillecube.common.world.entity.collision.PhysicObject;
import com.grillecube.common.world.entity.collision.Positioneable;
import com.grillecube.common.world.entity.collision.Rotationable;
import com.grillecube.common.world.entity.collision.Sizeable;
import com.grillecube.common.world.entity.control.Control;
import com.grillecube.common.world.entity.forces.Force;

public abstract class Entity extends PhysicObject {

	/** block under the entity */
	private Block blockUnder;

	private static final int STATE_VISIBLE = (1 << 0);

	/** entity state */
	private int state;

	/** entity's world */
	private World world;

	/** entity AI */
	private final ArrayList<EntityAI<Entity>> ais;

	/** entity forces */
	private final ArrayList<Force<Entity>> forces;

	/** entity controls */
	private final ArrayList<Control<Entity>> controls;

	/** entity position */
	private float x, y, z;
	private float xVelocity, yVelocity, zVelocity;
	private float xAcceleration, yAcceleration, zAcceleration;

	/** entity rotation */
	private float rx, ry, rz;
	private float rxVelocity, ryVelocity, rzVelocity;
	private float rxAcceleration, ryAcceleration, rzAcceleration;

	/** entity size */
	private float sx, sy, sz;
	private float sxVelocity, syVelocity, szVelocity;
	private float sxAcceleration, syAcceleration, szAcceleration;

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

		this.forces = new ArrayList<Force<Entity>>();
		this.controls = new ArrayList<Control<Entity>>();

		// look vector
		this.lookVec = new Vector3f();
		this.rx = 0;
		this.ry = 0;
		this.rz = 0;

		// entity definition
		this.speed = DEFAULT_SPEED;
		this.mass = DEFAULT_MASS;

		// size
		this.sx = width;
		this.sy = height;
		this.sz = depth;

		// aies
		this.ais = new ArrayList<EntityAI<Entity>>();
		this.addAI(new EntityAIIdle<Entity>());

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

	/** update the entity */
	public void update(double dt) {
		this.updateAI(dt);
		this.updateRotation(dt);
		this.updateSize(dt);
		this.updatePosition(dt);
		this.updateBlockUnder();
		this.updateBoundingBox();
		this.onUpdate(dt);
	}

	private final void updateBoundingBox() {

	}

	private final void updateAI(double dt) {
		for (int i = 0; i < this.ais.size(); i++) {
			EntityAI<Entity> ai = this.ais.get(i);
			ai.update(this, dt);
		}
	}

	public final void addAI(EntityAI ai) {
		this.ais.add(ai);
	}

	public final void removeAI(EntityAI ai) {
		this.ais.remove(ai);
	}

	/**
	 * update entity's rotation
	 * 
	 * @param dt
	 */
	private final void updateRotation(double dt) {
		// update looking vector
		this.lookVec.x = (float) (Math.sin(this.getRotationZ()) * Math.sin(-this.getRotationX()));
		this.lookVec.y = (float) (Math.cos(this.getRotationZ()) * Math.sin(-this.getRotationX()));
		this.lookVec.z = (float) (-Math.cos(-this.getRotationX()));

		this.lookVec.normalise();

		Rotationable.rotate(this, dt);
	}

	private final void updateSize(double dt) {
		Sizeable.resize(this, dt);
	}

	/**
	 * update this entity's position, depending on forces and controls applied to it
	 * 
	 * really basis of the physic engine: the acceleration vector is reset every
	 * frame and has to be recalculated via 'Entity.addForce(Vector3f force)'
	 * 
	 * @param dt2
	 */
	private final void updatePosition(double dt) {

		// do the controls
		this.runControls(dt);

		// simulate forces
		this.runForces(dt);
	}

	private final void runForces(double dt) {
		// add constant forces
		this.addForce(Force.GRAVITY);
		this.addForce(Force.FRICTION);

		// calculate resultant of the applied forces
		Vector3f resultant = new Vector3f();
		for (Force<Entity> force : this.forces) {
			force.updateResultant(this, resultant);
		}
		this.forces.clear();

		// m.a = F
		float m = this.getMass();
		float ax = resultant.x * Terrain.BLOCKS_PER_METER / m;
		float ay = resultant.y * Terrain.BLOCKS_PER_METER / m;
		float az = resultant.z * Terrain.BLOCKS_PER_METER / m;
		this.setPositionAccelerationX(ax);
		this.setPositionAccelerationY(ay);
		this.setPositionAccelerationZ(az);

		// update velocity of this entity (integrate)
		Positioneable.velocity(this, dt);
		// this.setPosition(0, 0, 16);
		// if this entity is part of a world
		if (this.getWorld() != null) {
			// move with collision detection
			PhysicObject.move(this.getWorld(), this, (float) dt);
		} else {
			Positioneable.position(this, dt);
		}
	}

	private final void runControls(double dt) {
		for (Control<Entity> control : this.controls) {
			control.run(this, dt);
		}
		this.controls.clear();
	}

	/** make the entity jump */
	public final void jump() {
		this.forces.add(Force.JUMP);
	}

	/** update the value of the block under this entity */
	private final void updateBlockUnder() {
		World world = this.getWorld();
		if (world == null) {
			this.blockUnder = null;
			return;
		}
		float x = this.getPositionX() + this.getSizeX() * 0.5f;
		float y = this.getPositionY() - 1.0f;
		float z = this.getPositionZ() + this.getSizeX() * 0.5f;
		this.blockUnder = world.getBlock(x, y, z);
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

	/**
	 * update the entity
	 * 
	 * @param dt
	 */
	protected abstract void onUpdate(double dt);

	/** get entity world */
	public final World getWorld() {
		return (this.world);
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

	/** entity speed in blocks per seconds */
	public final float getSpeed() {
		return (this.speed);
	}

	/** entity speed in blocks per seconds */
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
		this.setPosition(x, y, z);
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

	/** play the sound at the entity position and velocity */
	public final void playSound(String soundName) {
		EventEntityPlaySound event = new EventEntityPlaySound(this, soundName);
		EventManager.instance().invokeEvent(event);
	}

	public final boolean isVisible() {
		return (this.hasState(STATE_VISIBLE));
	}

	public final boolean isJumping() {
		return (this.getPositionAccelerationY() > 0.0f);
	}

	/***************************************************************************************/
	/** position begins */
	/***************************************************************************************/

	@Override
	public float getPositionX() {
		return (this.x);
	}

	@Override
	public float getPositionY() {
		return (this.y);
	}

	@Override
	public float getPositionZ() {
		return (this.z);
	}

	@Override
	public float getPositionVelocityX() {
		return (this.xVelocity);
	}

	@Override
	public float getPositionVelocityY() {
		return (this.yVelocity);
	}

	@Override
	public float getPositionVelocityZ() {
		return (this.zVelocity);
	}

	@Override
	public float getPositionAccelerationX() {
		return (this.xAcceleration);
	}

	@Override
	public float getPositionAccelerationY() {
		return (this.yAcceleration);
	}

	@Override
	public float getPositionAccelerationZ() {
		return (this.zAcceleration);
	}

	@Override
	public void setPositionX(float x) {
		this.x = x;
	}

	@Override
	public void setPositionY(float y) {
		this.y = y;
	}

	@Override
	public void setPositionZ(float z) {
		this.z = z;
	}

	@Override
	public void setPositionVelocityX(float vx) {
		this.xVelocity = vx;
	}

	@Override
	public void setPositionVelocityY(float vy) {
		this.yVelocity = vy;
	}

	@Override
	public void setPositionVelocityZ(float vz) {
		this.zVelocity = vz;
	}

	@Override
	public void setPositionAccelerationX(float ax) {
		this.xAcceleration = ax;
	}

	@Override
	public void setPositionAccelerationY(float ay) {
		this.yAcceleration = ay;
	}

	@Override
	public void setPositionAccelerationZ(float az) {
		this.zAcceleration = az;
	}

	/***************************************************************************************/
	/** rotation begins */
	/***************************************************************************************/

	@Override
	public float getRotationX() {
		return (this.rx);
	}

	@Override
	public float getRotationY() {
		return (this.ry);
	}

	@Override
	public float getRotationZ() {
		return (this.rz);
	}

	@Override
	public float getRotationVelocityX() {
		return (this.rxVelocity);
	}

	@Override
	public float getRotationVelocityY() {
		return (this.ryVelocity);
	}

	@Override
	public float getRotationVelocityZ() {
		return (this.rzVelocity);
	}

	@Override
	public float getRotationAccelerationX() {
		return (this.rxAcceleration);
	}

	@Override
	public float getRotationAccelerationY() {
		return (this.ryAcceleration);
	}

	@Override
	public float getRotationAccelerationZ() {
		return (this.rzAcceleration);
	}

	@Override
	public void setRotationX(float x) {
		this.rx = x;
	}

	@Override
	public void setRotationY(float y) {
		this.ry = y;
	}

	@Override
	public void setRotationZ(float z) {
		this.rz = z;
	}

	@Override
	public void setRotationVelocityX(float vx) {
		this.rxVelocity = vx;
	}

	@Override
	public void setRotationVelocityY(float vy) {
		this.ryVelocity = vy;
	}

	@Override
	public void setRotationVelocityZ(float vz) {
		this.rzVelocity = vz;
	}

	@Override
	public void setRotationAccelerationX(float ax) {
		this.rxAcceleration = ax;
	}

	@Override
	public void setRotationAccelerationY(float ay) {
		this.ryAcceleration = ay;
	}

	@Override
	public void setRotationAccelerationZ(float az) {
		this.rzAcceleration = az;
	}

	/***************************************************************************************/
	/** position begins */
	/***************************************************************************************/

	@Override
	public float getSizeX() {
		return (this.sx);
	}

	@Override
	public float getSizeY() {
		return (this.sy);
	}

	@Override
	public float getSizeZ() {
		return (this.sz);
	}

	@Override
	public float getSizeVelocityX() {
		return (this.sxVelocity);
	}

	@Override
	public float getSizeVelocityY() {
		return (this.syVelocity);
	}

	@Override
	public float getSizeVelocityZ() {
		return (this.szVelocity);
	}

	@Override
	public float getSizeAccelerationX() {
		return (this.sxAcceleration);
	}

	@Override
	public float getSizeAccelerationY() {
		return (this.syAcceleration);
	}

	@Override
	public float getSizeAccelerationZ() {
		return (this.szAcceleration);
	}

	@Override
	public void setSizeX(float x) {
		this.sx = x;
	}

	@Override
	public void setSizeY(float y) {
		this.sy = y;
	}

	@Override
	public void setSizeZ(float z) {
		this.sz = z;
	}

	@Override
	public void setSizeVelocityX(float vx) {
		this.sxVelocity = vx;
	}

	@Override
	public void setSizeVelocityY(float vy) {
		this.syVelocity = vy;
	}

	@Override
	public void setSizeVelocityZ(float vz) {
		this.szVelocity = vz;
	}

	@Override
	public void setSizeAccelerationX(float ax) {
		this.sxAcceleration = ax;
	}

	@Override
	public void setSizeAccelerationY(float ay) {
		this.syAcceleration = ay;
	}

	@Override
	public void setSizeAccelerationZ(float az) {
		this.szAcceleration = az;
	}
}
