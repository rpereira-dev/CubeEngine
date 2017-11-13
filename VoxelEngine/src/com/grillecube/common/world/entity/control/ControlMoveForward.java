package com.grillecube.common.world.entity.control;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class ControlMoveForward extends Control<Entity> {
	@Override
	public void run(Entity entity, Vector3f resultant) {
		Vector3f view = entity.getViewVector();
		float dx = view.x * entity.getSpeed();
		float dz = view.z * entity.getSpeed();
		entity.setPositionVelocityX(entity.getPositionVelocityX() + dx);
		entity.setPositionVelocityZ(entity.getPositionVelocityZ() + dz);

	}
}
