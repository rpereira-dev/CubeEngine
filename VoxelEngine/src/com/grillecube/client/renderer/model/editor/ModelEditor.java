package com.grillecube.client.renderer.model.editor;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldFree;
import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.editor.mesher.BlockData;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.ModelMesher;
import com.grillecube.client.renderer.model.editor.mesher.ModelMesherCull;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;

public class ModelEditor {
	public static void main(String[] args) {

		/* 1 */
		// initialize engine
		VoxelEngineClient engine = new VoxelEngineClient();

		/* 2 */
		// inject resources to be loaded
		engine.getModLoader().injectMod(ModelEditorMod.class);

		// load resources (mods)
		engine.load();

		/* prepare engine before looping */
		prepareEngine(engine);

		/* 3 */
		// loop, every allocated memory will be released properly on program
		// termination */
		try {
			engine.loop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		engine.stopAll();
	}

	private static void prepareEngine(VoxelEngineClient engine) {
		engine.getRenderer().setCamera(new CameraPerspectiveWorldFree(engine.getGLFWWindow()));
		engine.getRenderer().getCamera().setPosition(0.0f, 64.0f, -40.0f);
		engine.setWorld(ModelEditorMod.WORLD_ID);
		engine.getGLFWWindow().swapInterval(1);
		engine.getGLFWWindow().setScreenPosition(100, 100);
		newModel(engine);
	}

	private static void newModel(VoxelEngineClient engine) {
		World world = engine.getWorld();
		ResourceManagerClient manager = engine.getResourceManager();
		Entity entity = new Entity() {
			@Override
			protected void onUpdate() {
			}
		};
		world.spawnEntity(entity);

		entity.setPosition(0.0f, 16.0f, 0.0f);
		EditableModel editableModel = new EditableModel();
		ModelSkin modelSkin = new ModelSkin();
		editableModel.addSkin(modelSkin);

		editableModel.initialize();
		editableModel.resize(16, 16, 16);
		editableModel.setBlockData(new BlockData(), 0, 0, 0);
		editableModel.setBlockData(new BlockData(), 0, 1, 0);
		editableModel.setBlockData(new BlockData(), 0, 2, 0);
		editableModel.setBlockData(new BlockData(), 0, 3, 0);

		ModelMesher modelMesher = new ModelMesherCull();
		modelMesher.generate(editableModel);

		ModelInstance modelInstance = new ModelInstance(editableModel, entity);
		manager.getModelManager().addModelInstance(modelInstance);
	}
}
