package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.client.renderer.model.editor.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.ModelEditorMod;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;

/** the gui which displays the model */
public class GuiModelView extends Gui {

	private GuiViewWorld guiViewWorld;
	private BoundingBox theBox;
	private int boxID;

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		int worldID = ModelEditorMod.WORLD_ID;
		CameraProjective camera = new ModelEditorCamera(renderer.getMainRenderer().getGLFWWindow());
		this.guiViewWorld = new GuiViewWorld(camera, worldID);
		this.guiViewWorld.initialize(renderer);
		this.addChild(this.guiViewWorld);
		this.theBox = new BoundingBox();
		this.boxID = this.guiViewWorld.getWorldRenderer().getLineRendererFactory().addBox(this.theBox);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.guiViewWorld == null) {
			return;
		}
		ModelEditorCamera camera = (ModelEditorCamera) this.guiViewWorld.getWorldRenderer().getCamera();
		Vector3f pos = camera.getLookCoords();
		this.theBox.setMinSize(pos, 8, 8, 8);
		this.guiViewWorld.getWorldRenderer().getLineRendererFactory().setBox(this.theBox, this.boxID);
	}

	public final GuiViewWorld getGuiViewWorld() {
		return (this.guiViewWorld);
	}
}
