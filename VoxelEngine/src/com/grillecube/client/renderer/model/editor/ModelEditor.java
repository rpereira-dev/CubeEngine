package com.grillecube.client.renderer.model.editor;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.model.editor.gui.GuiModelEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolbox;

public class ModelEditor {

	private static ModelEditor instance;

	public static void main(String[] args) {
		new ModelEditor().run();
	}

	private GuiModelEditor guiModelEditor;

	private void run() {

		instance = this;

		/* 1 */
		// initialize engine
		VoxelEngineClient engine = new VoxelEngineClient();
		engine.initialize();

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
		engine.deinitialize();
	}

	private void prepareEngine(VoxelEngineClient engine) {

		engine.loadWorld(ModelEditorMod.WORLD_ID);

		engine.getGLFWWindow().swapInterval(1);
		engine.getGLFWWindow().setScreenPosition(100, 100);

		this.guiModelEditor = new GuiModelEditor();
		this.guiModelEditor.setBox(0, 0, 1.0f, 1.0f, 0);
		engine.getRenderer().getGuiRenderer().addGui(this.guiModelEditor);
	}

	public final ModelEditor instance() {
		return (instance);
	}

	public final GuiToolbox getToolbox() {
		return (this.guiModelEditor.getToolbox());
	}

	public final GuiModelView getModelView() {
		return (this.guiModelEditor.getModelView());
	}
}
