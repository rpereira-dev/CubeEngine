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
import com.grillecube.common.maths.AABB;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.Timer;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.entity.ai.EntityAI;
import com.grillecube.common.world.entity.ai.EntityAIIdle;
import com.grillecube.common.world.entity.control.Control;
import com.grillecube.common.world.entity.forces.Force;
import com.grillecube.common.world.terrain.Terrain;

public abstract class Entity {

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

	/** entity's world pos */
	private final Vector3f pos;
	private final Vector3f vel;
	private final Vector3f acc;
	private final Vector3f resultant;

	/** entity timer */
	private final Timer timer;

	/** entity's world rotation */
	private final Vector3f rotation;
	private final Vector3f rotVel;

	/** vector where the entity is looking at */
	private final Vector3f lookVec;

	/** the entity bounding box */
	private final AABB boundingBox;

	/** speed for this entity, in block per seconds */
	private static final float DEFAULT_SPEED = 1.0f / 60.0f;
	private float speed;

	/** entity weight */
	private static final float DEFAULT_WEIGHT = 1e-7f;
	private float weight;

	/** world id */
	public static final int DEFAULT_ENTITY_ID = 0;
	private int id = DEFAULT_ENTITY_ID;

	public Entity(World world, float width, float height, float depth) {
		this.world = world;

		this.timer = new Timer();

		this.forces = new ArrayList<Force<Entity>>();
		this.addForce(Force.GRAVITY);
		this.addForce(Force.FRICTION);

		this.controls = new ArrayList<Control<Entity>>();

		this.pos = new Vector3f();
		this.vel = new Vector3f();
		this.acc = new Vector3f();
		this.resultant = new Vector3f();

		this.rotation = new Vector3f();
		this.rotVel = new Vector3f();

		this.lookVec = new Vector3f();

		this.speed = DEFAULT_SPEED;
		this.weight = DEFAULT_WEIGHT;

		this.ais = new ArrayList<EntityAI>();
		this.addAI(new EntityAIIdle(this));

		this.boundingBox = new AABB();
		this.boundingBox.setMinSize(this.pos, width, height, depth);

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
		this.updateForces();
		this.updateControls();
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
	private final void updatePosition() {
		float dt = (float) this.timer.getDt(); // dt since last update

		float m = this.getWeight();
		float ax = this.resultant.x * Terrain.METER_TO_BLOCK / m;
		float ay = this.resultant.y * Terrain.METER_TO_BLOCK / m;
		float az = this.resultant.z * Terrain.METER_TO_BLOCK / m;
		this.acc.set(ax, ay, az);
		this.vel.add(ax * dt, ay * dt, az * dt);
		this.move(this.vel.x, this.vel.y, this.vel.z, dt);
	}

	/** update forces applied to this entity */
	private final void updateForces() {
		this.resultant.set(0, 0, 0);
		for (Force<Entity> force : this.forces) {
			force.updateResultant(this, this.resultant);
		}
	}

	private final void updateControls() {
		for (Control<Entity> control : this.controls) {
			control.run(this, this.resultant);
		}
		this.controls.clear();
	}

	/** make the entity jump */
	public final void jump() {
		this.controls.add(Control.JUMP);
	}

	/** update the value of the block under this entity */
	private final void updateBlockUnder() {
		this.blockUnder = (this.getWorld() == null) ? null
				: this.getWorld().getBlock(this.pos.x, this.pos.y - 1, this.pos.z);
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

	public final Vector3f getRotation() {
		return (this.rotation);
	}

	public final float getPitch() {
		return (this.getRotation().x);
	}

	public final float getYaw() {
		return (this.getRotation().y);
	}

	public final float getRoll() {
		return (this.getRotation().z);
	}

	public final void setPitch(double pitch) {
		this.rotation.x = (float) pitch;
	}

	public final void setYaw(double yaw) {
		this.rotation.y = (float) yaw;
	}

	public final void setRoll(double roll) {
		this.rotation.z = (float) roll;
	}

	public final void rotate(float pitch, float yaw, float roll) {
		this.rotation.add(pitch, yaw, roll);
	}

	/** update the entity */
	protected abstract void onUpdate();

	/** get entity position */
	public final Vector3f getPosition() {
		return (this.pos);
	}

	public final Vector3f getPositionVelocity() {
		return (this.vel);
	}

	public final Vector3f getPositionAcceleration() {
		return (this.acc);
	}

	/** get entity world */
	public World getWorld() {
		return (this.world);
	}

	public final void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public final void setPosition(float x, float y, float z) {
		this.pos.set(x, y, z);
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

	public final void increasePitch(float f) {
		this.rotation.x += f;
	}

	public final void increaseYaw(float f) {
		this.rotation.y += f;
	}

	public final void increaseRoll(float f) {
		this.rotation.z += f;
	}

	public final float getSpeed() {
		return (this.speed);
	}

	public final void setSpeed(float speed) {
		this.speed = speed;
	}

	/** return true if the entity is moving */
	public final boolean isMoving() {
		return (this.getPositionVelocity().x != 0 || this.getPositionVelocity().y != 0
				|| this.getPositionVelocity().z != 0);
	}

	/** rotation speed vector, x == pitch, y == yaw, z == roll */
	public final Vector3f getRotationSpeed() {
		return (this.rotVel);
	}

	public final void setWeight(float weight) {
		this.weight = weight;
	}

	public final float getWeight() {
		return (this.weight);
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

	/**
	 * try to move the entity of the delta position vector 'move'
	 * 
	 * @param move:
	 *            velocity
	 * @param dt:
	 *            time
	 * @return true if the entity moved, false else way
	 */
	public final boolean move(Vector3f move, float dt) {
		return (this.move(move.x, move.y, move.z, dt));
	}

	public final boolean move(float dx, float dy, float dz, float dt) {
		return (this.move(dx * dt, dy * dt, dz * dt));
	}

	public final boolean move(float dx, float dy, float dz) {
		this.pos.add(dx, dy, dz);
		return (true);
	}

	public final void teleport(float x, float y, float z) {
		this.getPosition().set(x, y, z);
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

	public AABB getBoundingBox() {
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
	public final void setWidth(float width) {
		this.boundingBox.setWidth(width);
	}

	public final void setHeight(float height) {
		this.boundingBox.setHeight(height);
	}

	public final void setDepth(float depth) {
		this.boundingBox.setDepth(depth);
	}

	public final void setDimensions(float width, float height, float depth) {
		this.boundingBox.setSize(width, height, depth);
	}

	public final boolean isVisible() {
		return (this.hasState(STATE_VISIBLE));
	}

	public final boolean isJumping() {
		return (this.acc.y > 0.0f);
	}
}
