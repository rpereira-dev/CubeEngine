package com.grillecube.common.world.entity.control;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class ControlStrafeLeft extends Control<Entity> {
	@Override
	public void run(Entity entity, Vector3f resultant) {
		Vector3f view = entity.getViewVector();
		float dx = -view.z * entity.getSpeed();
		float dz = view.x * entity.getSpeed();
		entity.setPositionVelocityX(entity.getPositionVelocityX() + dx);
		entity.setPositionVelocityZ(entity.getPositionVelocityZ() + dz);
	}
}
