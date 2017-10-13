package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.camera.Raycasting;
import com.grillecube.client.renderer.camera.RaycastingCallback;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiViewDebug;
import com.grillecube.client.renderer.gui.components.GuiViewWorld;
import com.grillecube.client.renderer.model.editor.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.ModelEditorMod;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.block.Block;

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

		this.addChild(new GuiViewDebug(camera));
	}

	int i = 0;
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.guiViewWorld == null) {
			return;
		}
		ModelEditorCamera camera = (ModelEditorCamera) this.guiViewWorld.getWorldRenderer().getCamera();

		Vector3f ray = camera.getPicker().getRay();
		Raycasting.raycast(camera.getPosition(), ray, 512.0f, new RaycastingCallback() {
			@Override
			public boolean onRaycastCoordinates(float x, float y, float z, Vector3f face) {
//				if (i++ % 500 == 0) System.out.println(x + " : " + y + " : " + z);
				Block block = guiViewWorld.getWorldRenderer().getWorld().getBlock(x, y, z);
				if (block.isVisible() && !block.bypassRaycast()) {
					theBox.setMinSize(x + face.x, y + face.y, z + face.z, 1, 1, 1);
					guiViewWorld.getWorldRenderer().getLineRendererFactory().setBox(theBox, boxID);
					return (true);
				}
				return (false);
			}
		});
	}

	public final GuiViewWorld getGuiViewWorld() {
		return (this.guiViewWorld);
	}
}
