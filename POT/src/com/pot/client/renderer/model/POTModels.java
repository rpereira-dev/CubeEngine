package com.pot.client.renderer.model;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.client.renderer.model.json.JSONModelInitializer;
import com.grillecube.client.resources.ModelManager;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.entity.WorldEntity;
import com.pot.common.ModPOT;
import com.pot.common.world.entity.WorldEntityBipedTest;
import com.pot.common.world.entity.EntityPlant;

public class POTModels implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
		registerJSONModel(manager, R.getResPath(ModPOT.MOD_ID, "models/physicTest2/"), WorldEntityBipedTest.class);
		registerJSONModel(manager, R.getResPath(ModPOT.MOD_ID, "models/plant/"), EntityPlant.class);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

	public static final int registerJSONModel(ResourceManager manager, String dirpath,
			Class<? extends WorldEntity> entityClass) {
		return (registerModel(manager, new JSONModelInitializer(dirpath), entityClass));
	}

	public static final int registerModel(ResourceManager manager, ModelInitializer modelInitializer,
			Class<? extends WorldEntity> entityClass) {
		Model model = new Model(modelInitializer);
		ModelManager modelManager = ((ResourceManagerClient) manager).getModelManager();
		int modelID = modelManager.registerModel(model);
		modelManager.attachEntityToModel(entityClass, modelID);
		return (modelID);
	}

}
