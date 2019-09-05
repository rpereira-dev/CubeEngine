package com.grillecube.common.world.physic;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.WorldEntity;

public class ForceGravity extends Force<WorldEntity> {

	/** a force applied to the entity which attract it down */
	// g = 9.81 m.s^-2 scaled
	public static final float G = 9.81f;

	@Override
	public void onUpdateResultant(WorldEntity entity, Vector3f resultant) {
		// F = mg
		resultant.z -= entity.getMass() * G;

	}
}
