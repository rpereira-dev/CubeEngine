package com.pot.client.renderer.model;

import com.grillecube.client.renderer.model.JSONModelInitializer;
import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.client.resources.ModelManager;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.entity.Entity;
import com.pot.common.ModPOT;
import com.pot.common.world.entity.EntityTest;

public class POTModels implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
		registerJSONModel(manager, R.getResPath(ModPOT.MOD_ID, "models/player/"), EntityTest.class);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

	public static final int registerJSONModel(ResourceManager manager, String dirpath,
			Class<? extends Entity> entityClass) {
		ModelInitializer modelInitializer = new JSONModelInitializer(dirpath);
		Model model = new Model(modelInitializer);
		ModelManager modelManager = ((ResourceManagerClient) manager).getModelManager();
		int modelID = modelManager.registerModel(model);
		modelManager.attachEntityToModel(entityClass, modelID);
		return (modelID);
	}

}
