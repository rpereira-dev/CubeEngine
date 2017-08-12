package com.grillecube.client.renderer.model.editor;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldFree;
import com.grillecube.client.renderer.gui.components.GuiViewDebug;
import com.grillecube.client.renderer.gui.components.GuiViewIngame;

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
		engine.getGLFWWindow().swapInterval(1);
		engine.getRenderer().setCamera(new CameraPerspectiveWorldFree(engine.getGLFWWindow()));
		engine.getRenderer().getCamera().setPosition(0.0f, 170.0f, -40.0f);
		engine.setWorld(ModelEditorMod.WORLD_ID);
		engine.getGLFWWindow().swapInterval(1);
		engine.getGLFWWindow().setScreenPosition(100, 100);
	}
}
