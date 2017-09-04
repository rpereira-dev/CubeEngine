package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.client.renderer.model.editor.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.ModelEditorMod;

/** the gui which displays the model */
public class GuiModelView extends Gui {

	private GuiViewWorld guiViewWorld;

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		int worldID = ModelEditorMod.WORLD_ID;
		CameraProjective camera = new ModelEditorCamera(renderer.getMainRenderer().getGLFWWindow());
		this.guiViewWorld = new GuiViewWorld(camera, worldID);
		this.addChild(this.guiViewWorld);
	}

	public final GuiViewWorld getGuiViewWorld() {
		return (this.guiViewWorld);
	}
}
