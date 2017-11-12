package com.grillecube.common.world.entity.control;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class ControlMoveBackward extends Control<Entity> {
	@Override
	public void run(Entity entity, Vector3f resultant) {
		Vector3f view = entity.getViewVector();
		float dx = -view.x * entity.getSpeed();
		float dy = 0;
		float dz = -view.z * entity.getSpeed();

		entity.move(dx, dy, dz);
	}
}
