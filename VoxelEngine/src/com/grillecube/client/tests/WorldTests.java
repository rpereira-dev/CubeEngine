package com.grillecube.client.tests;

import org.junit.Test;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.World;
import com.grillecube.common.world.WorldFlat;
import com.grillecube.common.world.block.Blocks;

import junit.framework.Assert;

public class WorldTests {

	public WorldTests() {

	}

	@Test
	public void testWorld() {

		VoxelEngineClient engine = new VoxelEngineClient();
		engine.initialize();

		World world = new WorldFlat() {
			@Override
			public String getName() {
				return ("Test world");
			}
		};
		world.spawnTerrain(new Terrain(1, 0, 0));
		world.spawnTerrain(new Terrain(0, 0, 0));
		world.spawnTerrain(new Terrain(-1, 0, 0));
		world.setBlock(Blocks.DIRT, 0.0f, 1.0f, 0.0f);

		float e = Maths.ESPILON;
		Assert.assertEquals(world.getBlock(0.0f, 1.0f, 0.0f), Blocks.DIRT);
		Assert.assertEquals(world.getBlock(0.0f, 1.0f + e, 0.0f), Blocks.DIRT);
		Assert.assertEquals(world.getBlock(0.0f, 1.0f - e, 0.0f), Blocks.AIR);
		Assert.assertEquals(world.getBlock(e, 1.0f, e), Blocks.DIRT);
		Assert.assertEquals(world.getBlock(-e, 1.0f, e), Blocks.AIR);
		Assert.assertEquals(world.getBlock(e, 1.0f, -e), Blocks.AIR);
		Assert.assertEquals(world.getBlock(-e, 1.0f, -e), Blocks.AIR);
		Assert.assertEquals(world.getBlock(1.0f - e, 1.0f, 1.0f - e), Blocks.DIRT);

		engine.deinitialize();
	}
}
