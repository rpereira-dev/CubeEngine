package com.pot.common.world.entity;

import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.WorldEntityBiped;

public class WorldEntityBipedTest extends WorldEntityBiped {

	public WorldEntityBipedTest(World world) {
		super(world, 50, 1.0f, 2.0f, 1.0f);
	}

	@Override
	protected void onUpdate(double dt) {

		World world = this.getWorld();
		if (world == null) {
			return;
		}
		float x = this.getPositionX() + this.getSizeX() * 0.5f;
		float y = this.getPositionY() - 1.0f;
		float z = this.getPositionZ() + this.getSizeX() * 0.5f;
		world.setBlockDurability((byte) ((System.currentTimeMillis() % 2000) / 100), x, y, z);

	}

}
