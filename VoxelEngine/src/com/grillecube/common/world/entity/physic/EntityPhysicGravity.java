package com.grillecube.common.world.entity.physic;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class EntityPhysicGravity extends EntityPhysic {

	/** a force applied to the entity which attract it down */
	public static final float GRAVITY_CONSTANT = 5e-2f; // g = 9.81 m.s^-2 scaled
	private Vector3f gravity = new Vector3f();

	@Override
	public void onEnable(Entity entity) {
	}

	@Override
	public void onDisable(Entity entity) {
	} // unused, never disabled

	@Override
	public void onUpdate(Entity entity) {
		this.gravity.set(0, -entity.getWeight() * GRAVITY_CONSTANT, 0); // F =
																		// mg

		if (entity.isInAir()) { // if the entity is in air
			entity.addForce(this.gravity); // apply gravity
		} else {
			entity.getPositionAcceleration().setY(0);
		}
	}
}
