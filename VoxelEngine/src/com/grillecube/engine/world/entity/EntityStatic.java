package com.grillecube.engine.world.entity;

import com.grillecube.engine.world.World;

public abstract class EntityStatic extends EntityModeled {
	public EntityStatic(World world) {
		super(world);
	}

	@Override
	protected void onUpdate() {
	}
}
