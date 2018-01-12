package com.grillecube.client.renderer.model.editor;

import com.grillecube.common.world.Terrain;
import com.grillecube.common.world.WorldFlat;

public class ModelEditorWorld extends WorldFlat {

	public ModelEditorWorld() {
		super();
	}

	@Override
	public void onLoaded() {
		this.setWorldGenerator(new ModelEditorWorldGenerator());
		for (int x = -4; x < 4; x++) {
			for (int y = -4; y < 4; y++) {
				Terrain terrain = this.spawnTerrain(new Terrain(x, y, -1));
				this.generateTerrain(terrain);
			}
		}
	}

	@Override
	public String getName() {
		return ("ModelEditorWorld");
	}
}
