package com.grillecube.common.world.entity.ai;

import com.grillecube.common.world.entity.Entity;

/** a simple idle ai, to test the system */
public class EntityAIIdle<T extends Entity> extends EntityAI<T> {

	public EntityAIIdle() {
		super();
		super.setUpdateTime(1000);
	}

	@Override
	protected void onUpdate(T entity, double dt) {
	}

	@Override
	protected void onTimedUpdate(Entity entity) {

		if (!entity.isInAir()) {
			entity.jump();
		}
	}

}
