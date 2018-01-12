package com.grillecube.client.renderer.model.editor;

import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.generator.WorldGenerator;

public class ModelEditorWorldGenerator extends WorldGenerator {

	@Override
	public void generate(Terrain terrain) {
		for (int x = 0; x < Terrain.DIMX; x++) {
			for (int y = 0; y < Terrain.DIMY; y++) {
				// terrain.setBlockAt((x + z) % 2 == 0 ? Blocks.GRASS :
				// Blocks.LOG, x, 4, z);
				terrain.setBlockAt(Blocks.GRASS, x, y, Terrain.DIMZ - 1);
				terrain.setBlockAt(Blocks.STONE, x, y, Terrain.DIMZ - 2);
				terrain.setBlockAt(Blocks.STONE, x, y, Terrain.DIMZ - 3);
				terrain.setBlockAt(Blocks.STONE, x, y, Terrain.DIMZ - 4);
				terrain.setBlockAt(Blocks.STONE, x, y, Terrain.DIMZ - 5);
			}
		}

	}
}
