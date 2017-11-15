package com.grillecube.common.world.entity.control;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.forces.ForceGravity;

public class ControlJump extends Control<Entity> {

	@Override
	public void run(Entity entity, Vector3f resultant) {
		entity.setPositionVelocityY(entity.getPositionVelocityY() + 0.1f * ForceGravity.G);
	}
}
