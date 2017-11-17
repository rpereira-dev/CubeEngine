package com.grillecube.common.world.entity.control;

import com.grillecube.common.world.entity.Entity;

public abstract class Control<T extends Entity> {

	/** controls */
	public static final Control<Entity> BACKWARD = new ControlMoveBackward();
	public static final Control<Entity> FORWARD = new ControlMoveForward();
	public static final Control<Entity> STRAFE_LEFT = new ControlStrafeLeft();
	public static final Control<Entity> STRAFE_RIGHT = new ControlStrafeRight();
	public static final Control<Entity> ROTATE_LEFT = new ControlRotateLeft();
	public static final Control<Entity> ROTATE_RIGHT = new ControlRotateRight();

	/** run this control */
	public abstract void run(Entity entity, double dt);
}
