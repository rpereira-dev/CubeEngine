package com.grillecube.common.world.generator;

import java.util.Random;

import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class WorldGeneratorHoles extends WorldGenerator {

	@Override
	public void generate(WorldObjectTerrain terrain) {
		//
		// if ((terrain.getWorldIndex().x == 0 && terrain.getWorldIndex().y < 2
		// && terrain.getWorldIndex().z == 0)
		// || (terrain.getWorldIndex().x != 0 || terrain.getWorldIndex().z !=
		// 0)) {
		// for (int x = 0; x < Terrain.DIM; x++) {
		// for (int y = 0; y < Terrain.DIM - 2; y++) {
		// for (int z = 0; z < Terrain.DIM; z++) {
		// terrain.setBlockAt(Blocks.GRASS, x, y, z);
		// }
		// }
		// }
		// } else {
		//
		// }

		for (int x = 0; x < WorldObjectTerrain.DIMX; x++) {
			for (int y = 0; y < WorldObjectTerrain.DIMY; y++) {
				for (int z = 0; z < WorldObjectTerrain.DIMZ; z++) {
					double d = World.NOISE_OCTAVE.noise(
							(terrain.getWorldPosition().x + x * WorldObjectTerrain.BLOCK_SIZE) / (64.0f * WorldObjectTerrain.BLOCK_SIZE),
							(terrain.getWorldPosition().y + y * WorldObjectTerrain.BLOCK_SIZE) / (32.0f * WorldObjectTerrain.BLOCK_SIZE),
							(terrain.getWorldPosition().z + z * WorldObjectTerrain.BLOCK_SIZE) / (64.0f * WorldObjectTerrain.BLOCK_SIZE));
					if (d < 0.2f) {
						terrain.setBlockAt(Blocks.STONE, x, y, z);
					} else {
						terrain.setBlockAt(Blocks.AIR, x, y, z);
					}
				}
			}
		}

		Random rng = new Random();

		for (int x = 0; x < WorldObjectTerrain.DIMX; x++) {
			for (int y = 0; y < WorldObjectTerrain.DIMY; y++) {
				int z = terrain.getHeightAt(x, y) - 1;
				if (z < 0) {
					continue;
				}

				double d = World.NOISE_OCTAVE.noise(
						(terrain.getWorldPosition().x + x * WorldObjectTerrain.BLOCK_SIZE) / (16.0f * WorldObjectTerrain.BLOCK_SIZE),
						(terrain.getWorldPosition().y+ y * WorldObjectTerrain.BLOCK_SIZE) / (16.0f * WorldObjectTerrain.BLOCK_SIZE));

				if (d < -0.6) {
					terrain.setBlock(Blocks.PLANTS[rng.nextInt(Blocks.PLANTS.length)], x, y, z + 1);
				}
				terrain.setBlock(Blocks.GRASS, x, y, z);

			}
		}

		int x = rng.nextInt(WorldObjectTerrain.DIMX);
		int y = rng.nextInt(WorldObjectTerrain.DIMY);
		int z = terrain.getHeightAt(x, y);

		if (z != -1) {
			int max = 4 + rng.nextInt(4);
			for (int i = 0; i < max; i++) {
				terrain.setBlock(Blocks.LOG, x, y, z + i);
			}
			for (int dx = -3; dx <= 3; dx++) {
				for (int dy = -3; dy <= 3; dy++) {
					terrain.setBlock(Blocks.LEAVES, x + dx, y + dy, z + max);

				}
			}
		}
	}
}
