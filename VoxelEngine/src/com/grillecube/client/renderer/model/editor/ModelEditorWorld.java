package com.grillecube.client.renderer.model.editor;

import com.grillecube.common.world.WorldFlat;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class ModelEditorWorld extends WorldFlat {

	public ModelEditorWorld() {
		super();
	}

	@Override
	public void onLoaded() {
		this.setWorldGenerator(new ModelEditorWorldGenerator());
		for (int x = -4; x < 4; x++) {
			for (int y = -4; y < 4; y++) {
				WorldObjectTerrain terrain = this.spawnTerrain(new WorldObjectTerrain(this, x, y, -1));
				this.generateTerrain(terrain);
			}
		}
	}

	@Override
	public String getName() {
		return ("ModelEditorWorld");
	}
}
