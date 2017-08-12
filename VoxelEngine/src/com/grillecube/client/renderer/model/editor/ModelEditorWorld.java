package com.grillecube.client.renderer.model.editor;

import com.grillecube.common.world.World;
import com.grillecube.common.world.generator.WorldGeneratorFlat;
import com.grillecube.common.world.terrain.Terrain;

public class ModelEditorWorld extends World {

	@Override
	public String getName() {
		return ("ModelEditorWorld");
	}

	@Override
	public void onSet() {
		this.setWorldGenerator(new WorldGeneratorFlat());
		for (int x = -4; x < 4; x++) {
			for (int z = -4; z < 4; z++) {
				Terrain terrain = this.spawnTerrain(new Terrain(x, 0, z));
				this.generateTerrain(terrain);
			}
		}
	}

	@Override
	public void onUnset() {
		// TODO Auto-generated method stub
	}
}
