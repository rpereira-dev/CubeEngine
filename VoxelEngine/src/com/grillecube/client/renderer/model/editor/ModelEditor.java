package com.grillecube.client.renderer.model.editor;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.model.editor.gui.GuiModelEditor;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolbox;
import com.grillecube.client.renderer.model.json.JSONModelExporter;
import com.grillecube.common.Logger;
import com.grillecube.common.resources.R;

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
		try {
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

			engine.loop();
		} catch (Exception e) {
			Logger.get().log(Logger.Level.ERROR, "That's unfortunate... VoxelEngine crashed.", e.getLocalizedMessage());
			String path = R.getResPath("models/tmp/" + System.currentTimeMillis());
			try {
				Logger.get().log(Logger.Level.ERROR, "Trying to save model", path);
				JSONModelExporter.export(guiModelEditor.getSelectedModel(), path);
			} catch (Exception exception) {
				Logger.get().log(Logger.Level.ERROR, "Couldn't save model... sorry bro",
						exception.getLocalizedMessage());
			}
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
