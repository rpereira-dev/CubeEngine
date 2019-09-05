package com.grillecube.common.world.physic;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.WorldEntity;

public class ForceJump extends Force<WorldEntity> {

	@Override
	public void onUpdateResultant(WorldEntity entity, Vector3f resultant) {
		resultant.z += entity.getMass() * ForceGravity.G * 8.0f;
	}
}
