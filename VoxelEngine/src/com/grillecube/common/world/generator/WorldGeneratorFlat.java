package com.grillecube.common.world.generator;

import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class WorldGeneratorFlat extends WorldGenerator {

	@Override
	public void generate(WorldObjectTerrain terrain) {
//		for (int x = 0; x < Terrain.DIMX; x++) {
//			for (int y = 0; y < Terrain.DIMY; y++) {
//				// terrain.setBlockAt((x + z) % 2 == 0 ? Blocks.GRASS :
//				// Blocks.LOG, x, 4, z);
//				terrain.setBlockAt(Blocks.GRASS, x, y, Terrain.DIMZ - 1);
//				terrain.setBlockAt(Blocks.STONE, x, y, Terrain.DIMZ - 2);
//				terrain.setBlockAt(Blocks.STONE, x, y, Terrain.DIMZ - 3);
//				terrain.setBlockAt(Blocks.STONE, x, y, Terrain.DIMZ - 4);
//				terrain.setBlockAt(Blocks.STONE, x, y, Terrain.DIMZ - 5);
//			}
//		}

		for (int x = 0; x < WorldObjectTerrain.DIMX; x++) {
			for (int y = 0; y < WorldObjectTerrain.DIMY; y++) {
				// terrain.setBlockAt((x + z) % 2 == 0 ? Blocks.GRASS :
				// Blocks.LOG, x, 4, z);
				terrain.setBlockAt(Blocks.GRASS, x, y, 4);
				terrain.setBlockAt(Blocks.STONE, x, y, 3);
				terrain.setBlockAt(Blocks.STONE, x, y, 2);
				terrain.setBlockAt(Blocks.STONE, x, y, 1);
				terrain.setBlockAt(Blocks.STONE, x, y, 0);
			}
		}

		for (int d = 4; d <= 12; d++) {
			terrain.setBlockAt(Blocks.LOG, d, 4, 5);
			terrain.setBlockAt(Blocks.LOG, d, 12, 5);
			terrain.setBlockAt(Blocks.LOG, 4, d, 5);
			terrain.setBlockAt(Blocks.LOG, 12, d, 5);
		}
		terrain.setBlockAt(Blocks.LOG, 8, 8, 5);
		terrain.setBlockAt(Blocks.LOG, 8, 8, 6);
		terrain.setBlockAt(Blocks.LOG, 8, 8, 7);
		terrain.setBlockAt(Blocks.LOG, 8, 8, 8);
		terrain.setBlockAt(Blocks.LOG, 8, 8, 9);
		terrain.setBlockAt(Blocks.LOG, 8, 8, 10);

		// Random rng = new Random();
		// if (rng.nextInt() % 4 == 0) {
		// int x = rng.nextInt() % 8;
		// int z = rng.nextInt() % 8;
		// for (int y = 4; y < 8; y++) {
		// terrain.setBlock(Blocks.LOG, x, y, z);
		// }
		// terrain.setBlock(Blocks.LEAVES, x - 1, 8, z);
		// terrain.setBlock(Blocks.LEAVES, x, 8, z - 1);
		// terrain.setBlock(Blocks.LEAVES, x, 9, z);
		// terrain.setBlock(Blocks.LEAVES, x, 8, z + 1);
		// terrain.setBlock(Blocks.LEAVES, x + 1, 8, z);
		// }
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
