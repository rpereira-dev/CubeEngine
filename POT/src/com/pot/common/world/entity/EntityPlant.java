package com.pot.common.world.entity;

import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.EntityLiving;

public class EntityPlant extends EntityLiving {

	public EntityPlant(World world) {
		super(world);
		this.setSize(0.98f, 0.98f, 0.98f);
		this.setMass(1.0f);
	}

	@Override
	protected void onUpdate(double dt) {
	}

}
