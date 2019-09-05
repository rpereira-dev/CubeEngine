package com.grillecube.common.world.physic;

import javax.vecmath.Vector3f;

import com.grillecube.common.world.entity.WorldEntity;

public class ControlStrafeLeft extends Control<WorldEntity> {
	@Override
	public void run(WorldEntity entity, double dt) {
		// save velocity
		float vx = entity.getPositionVelocityX();
		float vy = entity.getPositionVelocityY();
		float vz = entity.getPositionVelocityZ();

		// set control velocity, and move
		Vector3f view = entity.getViewVector();
		entity.setPositionVelocity(-view.y * entity.getSpeed(), view.x * entity.getSpeed(), 0.0f);
		WorldObject.move(entity.getWorld(), entity, dt);

		// reset velocities
		entity.setPositionVelocity(vx, vy, vz);
	}
}
