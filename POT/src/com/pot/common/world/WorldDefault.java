/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.pot.common.world;

import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.World;
import com.grillecube.engine.world.block.Blocks;

public class WorldDefault extends World {

	public WorldDefault() {
		super();
	}

	@Override
	public String getName() {
		return ("Default");
	}

	@Override
	public void onSet() {
		this.generate();
	}

	private void generate() {

		for (int x = -6; x < 6; x++) {
			for (int y = 0; y < 1; y++) {
				for (int z = -6; z < 6; z++) {
					this.spawnTerrain(new Terrain(x, y, z) {

						@Override
						public void onGenerated() {

							for (int x = 0; x < Terrain.DIM; x++) {
								for (int z = 0; z < Terrain.DIM; z++) {
									this.setBlockAt(Blocks.DIRT, x, 0, z);
								}
							}

							// for (int x = 0; x < Terrain.DIM; x++) {
							// for (int y = 0; y < Terrain.DIM; y++) {
							// for (int z = 0; z < Terrain.DIM; z++) {
							// double d = World.NOISE_OCTAVE.noise(
							// (this.getWorldPosition().x + x *
							// Terrain.BLOCK_SIZE)
							// / (256.0f * Terrain.BLOCK_SIZE),
							// (this.getWorldPosition().y + y *
							// Terrain.BLOCK_SIZE)
							// / (128.0f * Terrain.BLOCK_SIZE),
							// (this.getWorldPosition().z + z *
							// Terrain.BLOCK_SIZE)
							// / (256.0f * Terrain.BLOCK_SIZE));
							// if (d < 0.2f) {
							// this.setBlockAt(Blocks.STONE, x, y, z);
							// } else {
							// this.setBlockAt(Blocks.AIR, x, y, z);
							// }
							// }
							// }
							// }
							//
							// for (int x = 0; x < Terrain.DIM; x++) {
							// for (int y = 0; y < Terrain.DIM; y++) {
							// for (int z = 0; z < Terrain.DIM; z++) {
							// if (y - 1 >= 0 && this.getBlockAt(x, y, z) ==
							// Blocks.AIR) {
							// if (this.getBlockAt(x, y - 1, z) != Blocks.AIR) {
							// this.setBlock(Blocks.GRASS, x, y - 1, z);
							// }
							// }
							// }
							// }
							// }
							//
							// Random rng = getRNG();
							// int x = rng.nextInt(Terrain.DIM);
							// int z = rng.nextInt(Terrain.DIM);
							// int y = this.getHeightAt(x, z);
							//
							// if (y != -1) {
							// int max = 6 + rng.nextInt(4);
							// for (int i = 0; i < max; i++) {
							// this.setBlock(Blocks.LOG, x, y + i, z);
							// }
							// //
							// this.setBlock(Blocks.LEAVES, x, y + max, z);
							// }

							// for (int x = 0; x < Terrain.DIM; x++) {
							// for (int z = 0; z < Terrain.DIM; z++) {
							// this.setBlockAt(Blocks.DIRT, x, 0, z);
							// }
							// }
							//
							// this.setBlockAt(Blocks.LOG, 8, 0, 8);

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
						}
					});
				}
			}
		}
	}

	@Override
	public void onUnset() {
	}
}
