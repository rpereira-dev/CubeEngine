package com.pot.common.world.entity;

import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.EntityBiped;

public class EntityBipedTest extends EntityBiped {

	public EntityBipedTest(World world) {
		super(world);
		super.setSizeX(1.0f);
		super.setSizeY(2.0f);
		super.setSizeZ(1.0f);
		super.setMass(100);
	}

	@Override
	protected void onUpdate(double dt) {
	}

}
