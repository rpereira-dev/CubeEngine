package com.grillecube.common.world.entity.control;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class ControlStrafeRight extends Control<Entity> {
	@Override
	public void run(Entity entity, Vector3f resultant) {
		Vector3f view = entity.getViewVector();
		float dx = -view.z * entity.getSpeed();
		float dy = 0;
		float dz = view.x * entity.getSpeed();
		entity.move(dx, dy, dz);
	}
}
