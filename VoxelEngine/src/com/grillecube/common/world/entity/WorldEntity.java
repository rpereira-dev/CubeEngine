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
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.entity.ai.EntityAI;
import com.grillecube.common.world.entity.ai.EntityAIIdle;
import com.grillecube.common.world.physic.Control;
import com.grillecube.common.world.physic.Force;
import com.grillecube.common.world.physic.WorldObjectEntity;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public abstract class WorldEntity extends WorldObjectEntity {

	/** block under the entity */
	private Block blockUnder;

	private static final int STATE_VISIBLE = (1 << 0);

	/** entity state */
	private int state;

	/** entity AI */
	private final ArrayList<EntityAI<WorldEntity>> ais;

	/** entity forces */
	private final ArrayList<Force<WorldEntity>> forces;

	/** entity controls */
	private final ArrayList<Control<WorldEntity>> controls;

	/** world id */
	public static final int DEFAULT_ENTITY_ID = 0;
	private int id = DEFAULT_ENTITY_ID;

	public WorldEntity(World world, float mass, float width, float height, float depth) {
		super(world, mass, width, height, depth);

		this.forces = new ArrayList<Force<WorldEntity>>();
		this.controls = new ArrayList<Control<WorldEntity>>();

		// aies
		this.ais = new ArrayList<EntityAI<WorldEntity>>();
		this.addAI(new EntityAIIdle<WorldEntity>());

		// default states
		this.setState(WorldEntity.STATE_VISIBLE);
	}

	public WorldEntity(World world) {
		this(world, 1.0f, 1.0f, 1.0f, 1.0f);
	}

	public WorldEntity() {
		this(null);
	}

	/** called when entity spawns */
	public void onSpawn(World world) {
	}

	@Override
	public void preWorldUpdate(double dt) {
		super.preWorldUpdate(dt);
		this.update(dt);
	}

	/** update the entity */
	public void update(double dt) {
		this.updateAI(dt);
		this.runControls(dt);
		this.runForces(dt);
		this.updateBlockUnder();
		this.onUpdate(dt);
	}

	@Override
	public void postWorldUpdate(double dt) {
		super.postWorldUpdate(dt);
		this.updateBlockUnder();
	}

	private final void updateAI(double dt) {
		for (int i = 0; i < this.ais.size(); i++) {
			EntityAI<WorldEntity> ai = this.ais.get(i);
			ai.update(this, dt);
		}
	}

	public final void addAI(EntityAI<WorldEntity> ai) {
		this.ais.add(ai);
	}

	public final void removeAI(EntityAI<WorldEntity> ai) {
		this.ais.remove(ai);
	}

	private final void runForces(double dt) {
		// add constant forces
		this.addForce(Force.GRAVITY);
		this.addForce(Force.FRICTION);

		// calculate resultant of the applied forces
		Vector3f resultant = new Vector3f();
		for (Force<WorldEntity> force : this.forces) {
			force.updateResultant(this, resultant);
		}
		this.forces.clear();

		// m.a = F
		float m = this.getMass();
		float ax = resultant.x * WorldObjectTerrain.BLOCKS_PER_METER / m;
		float ay = resultant.y * WorldObjectTerrain.BLOCKS_PER_METER / m;
		float az = resultant.z * WorldObjectTerrain.BLOCKS_PER_METER / m;
		this.setPositionAccelerationX(ax);
		this.setPositionAccelerationY(ay);
		this.setPositionAccelerationZ(az);

		// // update velocity of this entity (integrate)
		// Positioneable.velocity(this, dt);
		// // this.setPosition(0, 0, 16);
		// // if this entity is part of a world
		// if (this.getWorld() != null) {
		// // move with collision detection
		// WorldObject.move(this.getWorld(), this, (float) dt);
		// } else {
		// Positioneable.position(this, dt);
		// }
	}

	private final void runControls(double dt) {
		for (Control<WorldEntity> control : this.controls) {
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
	public final void addForce(Force<WorldEntity> force) {
		this.forces.add(force);
	}

	public final void removeForce(Force<WorldEntity> force) {
		this.forces.remove(force);
	}

	public final void addControl(Control<WorldEntity> control) {
		this.controls.add(control);
	}

	/**
	 * update the entity
	 * 
	 * @param dt
	 */
	protected abstract void onUpdate(double dt);

	/** world ID for this entity, this is set on spawn */
	public final void setEntityID(int id) {
		this.id = id;
	}

	public final int getEntityID() {
		return (this.id);
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

	public final float getSpeed() {
		return (1.0f);
	}
}