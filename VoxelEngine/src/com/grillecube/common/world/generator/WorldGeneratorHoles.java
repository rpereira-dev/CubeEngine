package com.grillecube.common.world.generator;

import java.util.Random;

import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.world.World;
import com.grillecube.common.world.terrain.Terrain;

public class WorldGeneratorHoles extends WorldGenerator {

	@Override
	public void generate(Terrain terrain) {
		for (int x = 0; x < Terrain.DIM; x++) {
			for (int y = 0; y < Terrain.DIM; y++) {
				for (int z = 0; z < Terrain.DIM; z++) {
					double d = World.NOISE_OCTAVE.noise(
							(terrain.getWorldPosition().x + x * Terrain.BLOCK_SIZE) / (64.0f * Terrain.BLOCK_SIZE),
							(terrain.getWorldPosition().y + y * Terrain.BLOCK_SIZE) / (32.0f * Terrain.BLOCK_SIZE),
							(terrain.getWorldPosition().z + z * Terrain.BLOCK_SIZE) / (64.0f * Terrain.BLOCK_SIZE));
					if (d < 0.2f) {
						terrain.setBlockAt(Blocks.STONE, x, y, z);
					} else {
						terrain.setBlockAt(Blocks.AIR, x, y, z);
					}
				}
			}
		}

		for (int x = 0; x < Terrain.DIM; x++) {
			for (int y = 0; y < Terrain.DIM; y++) {
				for (int z = 0; z < Terrain.DIM; z++) {
					if (y - 1 >= 0 && terrain.getBlockAt(x, y, z) == Blocks.AIR) {
						if (terrain.getBlockAt(x, y - 1, z) != Blocks.AIR) {
							terrain.setBlock(Blocks.GRASS, x, y - 1, z);
						}
					}
				}
			}
		}

		Random rng = new Random();
		int x = rng.nextInt(Terrain.DIM);
		int z = rng.nextInt(Terrain.DIM);
		int y = terrain.getHeightAt(x, z);

		if (y != -1) {
			int max = 6 + rng.nextInt(4);
			for (int i = 0; i < max; i++) {
				terrain.setBlock(Blocks.LOG, x, y + i, z);
			}
			//
			terrain.setBlock(Blocks.LEAVES, x, y + max, z);
			terrain.setBlock(Blocks.LEAVES, x + 1, y + max, z);
			terrain.setBlock(Blocks.LEAVES, x, y + max, z + 1);
			terrain.setBlock(Blocks.LEAVES, x, y + max, z - 1);
			terrain.setBlock(Blocks.LEAVES, x - 1, y + max, z);
		}
	}
}
