package com.grillecube.common.world.generator;

import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.terrain.Terrain;

public class WorldGeneratorHoles extends WorldGenerator {

	@Override
	public void generate(Terrain terrain) {
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

		for (int x = 0; x < Terrain.DIMX; x++) {
			for (int y = 0; y < Terrain.DIMY; y++) {
				for (int z = 0; z < Terrain.DIMZ; z++) {
					double d = World.NOISE_OCTAVE.noise(
							(terrain.getWorldPos().x + x * Terrain.BLOCK_SIZE) / (64.0f * Terrain.BLOCK_SIZE),
							(terrain.getWorldPos().y + y * Terrain.BLOCK_SIZE) / (32.0f * Terrain.BLOCK_SIZE),
							(terrain.getWorldPos().z + z * Terrain.BLOCK_SIZE) / (64.0f * Terrain.BLOCK_SIZE));
					if (d < 0.2f) {
						terrain.setBlockAt(Blocks.STONE, x, y, z);
					} else {
						terrain.setBlockAt(Blocks.AIR, x, y, z);
					}
				}
			}
		}

		for (int x = 0; x < Terrain.DIMX; x++) {
			for (int y = 0; y < Terrain.DIMY; y++) {
				for (int z = 0; z < Terrain.DIMZ; z++) {
					if (y - 1 >= 0 && terrain.getBlockAt(x, y, z) == Blocks.AIR) {
						if (terrain.getBlockAt(x, y - 1, z) != Blocks.AIR) {
							terrain.setBlock(Blocks.GRASS, x, y - 1, z);
						}
					}
				}
			}
		}

		// Random rng = new Random();
		// int x = rng.nextInt(Terrain.DIM);
		// int z = rng.nextInt(Terrain.DIM);
		// int y = terrain.getHeightAt(x, z);
		//
		// if (y != -1) {
		// int max = 6 + rng.nextInt(4);
		// for (int i = 0; i < max; i++) {
		// terrain.setBlock(Blocks.LOG, x, y + i, z);
		// }
		// for (int dx = -3; dx <= 3; dx++) {
		// for (int dz = -3; dz <= 3; dz++) {
		// terrain.setBlock(Blocks.LEAVES, x + dx, y + max, z + dz);
		//
		// }
		// }
		// }
	}
}
