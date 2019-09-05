package com.pot.common.world.entity;

import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.WorldEntityLiving;

public class EntityPlant extends WorldEntityLiving {

	public EntityPlant(World world) {
		super(world);
		this.setSize(0.98f, 0.98f, 0.98f);
		this.setMass(1.0f);
	}

	@Override
	protected void onUpdate(double dt) {
	}

}
