package com.grillecube.client.renderer.model.editor;

import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.WorldFlat;
import com.grillecube.common.world.generator.WorldGeneratorFlat;

public class ModelEditorWorld extends WorldFlat {

	public ModelEditorWorld() {
		super();
	}

	@Override
	public void onLoaded() {
		this.setWorldGenerator(new WorldGeneratorFlat());
		for (int x = -4; x < 4; x++) {
			for (int z = -4; z < 4; z++) {
				Terrain terrain = this.spawnTerrain(new Terrain(x, -1, z));
				this.generateTerrain(terrain);
			}
		}
	}

	@Override
	public String getName() {
		return ("ModelEditorWorld");
	}
}
