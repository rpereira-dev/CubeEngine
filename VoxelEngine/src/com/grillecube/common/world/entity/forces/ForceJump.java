package com.grillecube.common.world.entity.forces;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class ForceJump extends Force<Entity> {

	@Override
	public void onUpdateResultant(Entity entity, Vector3f resultant) {
		resultant.y += entity.getMass() * ForceGravity.G * 8.0f;
	}
}
