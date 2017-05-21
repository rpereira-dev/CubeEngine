package com.grillecube.common.world.entity.physic;

import com.grillecube.common.world.entity.Entity;

public abstract class EntityPhysic {

	/** number of physic update per second for each entity */
	public static final int UPS = 120;

	public static final int MOVE_FORWARD = 0;
	public static final int MOVE_BACKWARD = 1;
	public static final int STRAFE_LEFT = 2;
	public static final int STRAFE_RIGHT = 3;
	public static final int ROTATE_LEFT = 4;
	public static final int ROTATE_RIGHT = 5;
	public static final int GRAVITY = 6;
	public static final int AIR_FRICTION = 7;
	public static final int JUMPING = 8;

	private boolean isSet;

	public void enable(Entity entity) {
		this.isSet = true;
		this.onEnable(entity);
	}

	public void disable(Entity entity) {
		this.isSet = false;
		this.onDisable(entity);
	}

	public void udpate(Entity entity) {
		this.onUpdate(entity);
	}

	public boolean isSet() {
		return (this.isSet);
	}

	/** called when the state is set */
	public abstract void onEnable(Entity entity);

	/** called when the state is unset */
	public abstract void onDisable(Entity entity);

	/** called when this state is set and the entity is updated. */
	public abstract void onUpdate(Entity entity);
}