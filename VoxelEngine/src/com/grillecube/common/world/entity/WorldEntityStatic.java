package com.grillecube.common.world.entity;

import com.grillecube.common.world.World;

/** represent static entities (chair...) */
public abstract class WorldEntityStatic extends WorldEntity {

	public WorldEntityStatic(World world) {
		super(world);
	}

	@Override
	protected void onUpdate(double dt) {
	}
}
