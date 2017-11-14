package com.grillecube.common.world.entity;

import com.grillecube.common.world.World;

public abstract class EntityObject extends Entity {

	public EntityObject(World world) {
		super(world);
	}

	@Override
	protected void onUpdate(double dt) {
	}
}
