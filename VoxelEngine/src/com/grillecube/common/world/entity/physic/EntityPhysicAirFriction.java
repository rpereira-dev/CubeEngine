package com.grillecube.common.world.entity.physic;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

public class EntityPhysicAirFriction extends EntityPhysic {
	
	private static final float AIR_CONSTANT = 0.05f;
	
	/**
	 * basically we add a 'up' force which represent air friction while falling,
	 * depending on entity weight, height and a constant
	 */
	private Vector3f _air_friction;

	public EntityPhysicAirFriction() {
		this._air_friction = new Vector3f();
	}

	@Override
	public void onEnable(Entity entity) {
	}

	@Override
	public void onDisable(Entity entity) {
	} // ununsed, never disabled

	@Override
	public void onUpdate(Entity entity) {
		
		float vx = entity.getPositionVelocity().x;
		float vy = entity.getPositionVelocity().y;
		float vz = entity.getPositionVelocity().z;
		
		//TODO calculate it exactly
		float contact_area = entity.getBoundingBox().getArea() / entity.getBoundingBox().getSize().y;
		
		this._air_friction.x = -0.5f * vx * contact_area * AIR_CONSTANT;
		this._air_friction.y = -0.5f * vy * contact_area * AIR_CONSTANT;
		this._air_friction.z = -0.5f * vz * contact_area * AIR_CONSTANT;
		
		// add the force
		entity.addForce(this._air_friction);
	}
}
