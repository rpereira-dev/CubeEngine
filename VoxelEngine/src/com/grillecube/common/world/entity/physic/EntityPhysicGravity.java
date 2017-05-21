package com.grillecube.common.world.entity.physic;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class EntityPhysicGravity extends EntityPhysic {

	/** a force applied to the entity which attract it down */
	public static final float GRAVITY_CONSTANT = 5e-2f; //g = 9.81 scaled
	private Vector3f _gravity = new Vector3f();

	@Override
	public void onEnable(Entity entity) {
	}

	@Override
	public void onDisable(Entity entity) {} //unused, never disabled

	@Override
	public void onUpdate(Entity entity) {
		this._gravity.set(0, -entity.getWeight() * GRAVITY_CONSTANT, 0);  //F = mg

		if (entity.isInAir()) { //if the entity is in air
			entity.addForce(this._gravity); //apply gravity
		} else {
			entity.getPositionAcceleration().setY(0);
		}
	}
}
