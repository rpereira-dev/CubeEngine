package com.grillecube.common.world.entity;

import com.grillecube.common.world.World;

public abstract class EntityStatic extends EntityModeled {
	public EntityStatic(World world) {
		super(world);
	}

	@Override
	protected void onUpdate() {
	}
}
