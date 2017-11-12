package com.pot.common.world.entity;

import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.EntityBiped;

public class EntityBipedTest extends EntityBiped {

	public EntityBipedTest(World world) {
		super(world);
		super.setDimensions(1.0f, 2.0f, 1.0f);
		super.setWeight(100);
	}

	@Override
	protected void onUpdate() {
	}

}
