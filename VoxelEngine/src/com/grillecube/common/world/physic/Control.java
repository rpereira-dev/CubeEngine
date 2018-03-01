package com.grillecube.common.world.physic;

import com.grillecube.common.world.entity.WorldEntity;

public abstract class Control<T extends WorldEntity> {

	/** controls */
	public static final Control<WorldEntity> BACKWARD = new ControlMoveBackward();
	public static final Control<WorldEntity> FORWARD = new ControlMoveForward();
	public static final Control<WorldEntity> STRAFE_LEFT = new ControlStrafeLeft();
	public static final Control<WorldEntity> STRAFE_RIGHT = new ControlStrafeRight();
	public static final Control<WorldEntity> ROTATE_LEFT = new ControlRotateLeft();
	public static final Control<WorldEntity> ROTATE_RIGHT = new ControlRotateRight();

	/** run this control */
	public abstract void run(WorldEntity entity, double dt);
}
