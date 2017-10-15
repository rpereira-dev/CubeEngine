package com.grillecube.common.world.generator;

import java.util.Random;

import com.grillecube.common.maths.Maths;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.terrain.Terrain;

public class WorldGeneratorFlat extends WorldGenerator {

	@Override
	public void generate(Terrain terrain) {
		if (terrain.getWorldIndex3().y != 0) {
			return;
		}
		for (int x = 0; x < Terrain.DIMX; x++) {
			for (int z = 0; z < Terrain.DIMZ; z++) {
				terrain.setBlockAt(Blocks.GRASS, x, 4, z);
				terrain.setBlockAt(Blocks.STONE, x, 3, z);
				terrain.setBlockAt(Blocks.STONE, x, 2, z);
				terrain.setBlockAt(Blocks.STONE, x, 1, z);
				terrain.setBlockAt(Blocks.STONE, x, 0, z);
			}
		}

		Random rng = new Random();
		int ix = terrain.getWorldIndex3().x;
		int iz = terrain.getWorldIndex3().z;
		if (Maths.abs(ix) > 1 && Maths.abs(iz) > 1 && rng.nextInt() % 4 == 0) {
			int x = rng.nextInt() % 8;
			int z = rng.nextInt() % 8;
			for (int y = 4; y < 8; y++) {
				terrain.setBlock(Blocks.LOG, x, y, z);
			}
			terrain.setBlock(Blocks.LEAVES, x - 1, 8, z);
			terrain.setBlock(Blocks.LEAVES, x, 8, z - 1);
			terrain.setBlock(Blocks.LEAVES, x, 9, z);
			terrain.setBlock(Blocks.LEAVES, x, 8, z + 1);
			terrain.setBlock(Blocks.LEAVES, x + 1, 8, z);
		}
	}
}

// for (int x = 0; x < Terrain.DIM; x++) {
// for (int z = 0; z < Terrain.DIM; z++) {
// this.setBlockAt(Blocks.DIRT, x, 0, z);
// }
// }

// this.setBlockAt(Blocks.LOG, 8, 1, 8);
// this.setBlockAt(Blocks.LOG, 8, 2, 8);
// this.setBlockAt(Blocks.LOG, 8, 3, 8);
// this.setBlockAt(Blocks.LOG, 8, 4, 8);
// this.setBlockAt(Blocks.LOG, 8, 5, 8);
// this.setBlockAt(Blocks.LOG, 8, 6, 8);
//
// for (int x = 4; x <= 12; x++) {
// this.setBlockAt(Blocks.LOG, x, 1, 4);
// this.setBlockAt(Blocks.LOG, x, 1, 12);
// this.setBlockAt(Blocks.LOG, 4, 1, x);
// this.setBlockAt(Blocks.LOG, 12, 1, x);
//
// for (int z = 4; z < 12; z++) {
// this.setBlockAt(Blocks.LOG, x, 0, z);
// }
// }
