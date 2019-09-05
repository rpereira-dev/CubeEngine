package com.grillecube.client.renderer.model.editor;

import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.generator.WorldGenerator;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class ModelEditorWorldGenerator extends WorldGenerator {

	@Override
	public void generate(WorldObjectTerrain terrain) {
		for (int x = 0; x < WorldObjectTerrain.DIMX; x++) {
			for (int y = 0; y < WorldObjectTerrain.DIMY; y++) {
				// terrain.setBlockAt((x + z) % 2 == 0 ? Blocks.GRASS :
				// Blocks.LOG, x, 4, z);
				terrain.setBlockAt(Blocks.GRASS, x, y, WorldObjectTerrain.DIMZ - 1);
				terrain.setBlockAt(Blocks.STONE, x, y, WorldObjectTerrain.DIMZ - 2);
				terrain.setBlockAt(Blocks.STONE, x, y, WorldObjectTerrain.DIMZ - 3);
				terrain.setBlockAt(Blocks.STONE, x, y, WorldObjectTerrain.DIMZ - 4);
				terrain.setBlockAt(Blocks.STONE, x, y, WorldObjectTerrain.DIMZ - 5);
			}
		}

	}
}
