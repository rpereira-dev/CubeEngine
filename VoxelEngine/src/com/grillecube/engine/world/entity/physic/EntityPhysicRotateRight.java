package com.grillecube.engine.world.entity.physic;

import com.grillecube.engine.world.entity.Entity;

public class EntityPhysicRotateRight extends EntityPhysic {
	@Override
	public void onEnable(Entity entity) {
		entity.getRotationSpeed().y -= 2.0f;
	}

	@Override
	public void onDisable(Entity entity) {
		entity.getRotationSpeed().y += 2.0f;
	}

	@Override
	public void onUpdate(Entity entity) {
	}
}
