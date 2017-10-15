package com.grillecube.client.renderer.model.editor.gui;

import java.util.HashMap;

import com.grillecube.client.renderer.camera.CameraPicker;
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
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.utils.Pair;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;

/** the gui which displays the model */
public class GuiModelView extends Gui {

	private GuiViewWorld guiViewWorld;

	// key: block coordinates in root bone referential, value: (bounding box,
	// box id)
	private HashMap<Vector3i, Pair<BoundingBox, Integer>> selectedBlocks;

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		int worldID = ModelEditorMod.WORLD_ID;
		CameraProjective camera = new ModelEditorCamera(renderer.getMainRenderer().getGLFWWindow());
		this.guiViewWorld = new GuiViewWorld(camera, worldID);
		this.guiViewWorld.initialize(renderer);
		this.addChild(this.guiViewWorld);
		this.addChild(new GuiViewDebug(camera));
		this.selectedBlocks = new HashMap<Vector3i, Pair<BoundingBox, Integer>>();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.guiViewWorld == null) {
			return;
		}
		this.updateHoveredBlock();
		this.updateBoxes();
	}

	private void updateBoxes() {
		// this.theBox.setMinSize(this.selectedBlocks, 1, 1, 1);
		// this.guiViewWorld.getWorldRenderer().getLineRendererFactory().setBox(theBox,
		// boxID);
	}

	private final void updateHoveredBlock() {
		ModelEditorCamera camera = (ModelEditorCamera) this.guiViewWorld.getWorldRenderer().getCamera();
		Vector3f ray = new Vector3f();
		CameraPicker.ray(ray, camera, this.getMouseX(), this.getMouseY());
		Raycasting.raycast(camera.getPosition(), ray, 64.0f, new RaycastingCallback() {
			@Override
			public boolean onRaycastCoordinates(float x, float y, float z, Vector3f face) {
				World world = getWorld();
				Block block = world.getBlock(x, y, z);
				if (block.isVisible() && !block.bypassRaycast()) {
//					selectBlock(x  + face.x, y + face.y, z + face.z);
					return (true);
				}
				return (false);
			}
		});
	}

	private final void selectBlock(float x, float y, float z) {
		Vector3i block = new Vector3i((int) x, (int) y, (int) z);
		if (this.selectedBlocks.containsKey(block)) {
			return;
		}
		BoundingBox box = new BoundingBox();
		box.setMinSize(x, y, z, 1, 1, 1);
		int boxID = this.guiViewWorld.getWorldRenderer().getLineRendererFactory().addBox(box);
		this.selectedBlocks.put(block, new Pair<BoundingBox, Integer>(box, boxID));
	}

	public final GuiViewWorld getGuiViewWorld() {
		return (this.guiViewWorld);
	}

	public final ModelEditorCamera getCamera() {
		return ((ModelEditorCamera) this.guiViewWorld.getWorldRenderer().getCamera());
	}

	public final World getWorld() {
		return (this.guiViewWorld.getWorldRenderer().getWorld());
	}
}
