package com.grillecube.client.renderer.model.editor;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.client.renderer.model.ModelInitializer;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiViewToolbox;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.ModelMesher;
import com.grillecube.client.renderer.model.editor.mesher.ModelMesherCull;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.model.json.JSONEditableModelInitializer;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;

public class ModelEditor {

	public static ModelEditor instance;

	public static void main(String[] args) {
		new ModelEditor().run();
	}

	private void run() {

		instance = this;

		/* 1 */
		// initialize engine
		VoxelEngineClient engine = new VoxelEngineClient();

		/* 2 */
		// inject resources to be loaded
		engine.getModLoader().injectMod(ModelEditorMod.class);

		// load resources (mods)
		engine.load();

		/* prepare engine before looping */
		this.prepareEngine(engine);

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

	private void prepareEngine(VoxelEngineClient engine) {

		engine.setWorld(ModelEditorMod.WORLD_ID);

		engine.getGLFWWindow().swapInterval(1);
		engine.getGLFWWindow().setScreenPosition(100, 100);

		CameraProjectiveWorld camera = new ModelEditorCamera(engine.getGLFWWindow());
		engine.getRenderer().setCamera(camera);

		GuiViewToolbox toolbox = new GuiViewToolbox();
		toolbox.setBox(0.0f, 0, 0.25f, 1.0f, 0);
		engine.getRenderer().getGuiRenderer().addGui(toolbox);

		GuiViewWorld world = new GuiViewWorld();
		world.setBox(0.25f, 0, 0.75f, 1.0f, 0);
		engine.getRenderer().getGuiRenderer().addGui(world);

		engine.getRenderer().getCamera().getPicker().setGuiViewWorldRelative(world);

		newModel(engine);
	}

	private void newModel(VoxelEngineClient engine) {
		World world = engine.getWorld();
		ResourceManagerClient manager = engine.getResourceManager();
		Entity entity = new Entity() {
			@Override
			protected void onUpdate() {
			}
		};
		world.spawnEntity(entity);

		entity.setPosition(0.0f, 16.0f, 0.0f);

		ModelInitializer initializer = new JSONEditableModelInitializer(R.getResPath("models/bipedJSON/"));
		EditableModel editableModel = new EditableModel(initializer);
		editableModel.initialize();

		ModelMesher modelMesher = new ModelMesherCull();
		modelMesher.generate(editableModel);

		ModelInstance modelInstance = new ModelInstance(editableModel, entity);
		manager.getModelManager().addModelInstance(modelInstance);
	}
}
