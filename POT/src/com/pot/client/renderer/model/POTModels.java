package com.pot.client.renderer.model;

import com.grillecube.client.resources.ModelManager;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.pot.common.ModPOT;
import com.pot.common.world.entity.EntityTest;

public class POTModels implements IModResource {

	// entities id
	public static int TEST_MODEL_ID;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		ModelManager modelManager = ((ResourceManagerClient) manager).getModelManager();

		TEST_MODEL_ID = modelManager.registerModel(R.getResPath(ModPOT.MOD_ID, "models/player/"));
		modelManager.attachEntityToModel(EntityTest.class, TEST_MODEL_ID);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

}
