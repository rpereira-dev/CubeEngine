package com.grillecube.common.world.entity.forces;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class ForceGravity extends Force<Entity> {

	/** a force applied to the entity which attract it down */
	// g = 9.81 m.s^-2 scaled
	public static final float G = 9.81f;

	@Override
	public void onUpdateResultant(Entity entity, Vector3f resultant) {
		// F = mg
		resultant.z -= entity.getMass() * G;

	}
}
