package com.grillecube.common.world.entity.control;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.forces.ForceGravity;

public class ControlJump extends Control<Entity> {

	@Override
	public void run(Entity entity, Vector3f resultant) {
		resultant.y += 4.0f * ForceGravity.G;
	}
}
