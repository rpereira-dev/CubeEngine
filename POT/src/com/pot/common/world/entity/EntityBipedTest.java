package com.pot.common.world.entity;

import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.EntityBiped;

public class EntityBipedTest extends EntityBiped {

	public EntityBipedTest(World world) {
		super(world);
		super.setSizeX(0.98f);
		super.setSizeY(1.96f);
		super.setSizeZ(0.98f);
		super.setMass(100);
	}

	@Override
	protected void onUpdate(double dt) {
	}

}
