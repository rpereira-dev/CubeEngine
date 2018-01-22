package com.grillecube.common.world.entity.control;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.collision.PhysicObject;

public class ControlStrafeLeft extends Control<Entity> {
	@Override
	public void run(Entity entity, double dt) {
		// save velocity
		float vx = entity.getPositionVelocityX();
		float vy = entity.getPositionVelocityY();
		float vz = entity.getPositionVelocityZ();

		// set control velocity, and move
		Vector3f view = entity.getViewVector();
		entity.setPositionVelocity(-view.y * entity.getSpeed(), view.x * entity.getSpeed(), 0.0f);
		PhysicObject.move(entity.getWorld(), entity, dt);

		// reset velocities
		entity.setPositionVelocity(vx, vy, vz);
	}
}
