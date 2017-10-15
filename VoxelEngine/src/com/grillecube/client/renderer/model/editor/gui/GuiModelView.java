package com.grillecube.client.renderer.model.editor.gui;

import java.util.ArrayList;

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
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;

/** the gui which displays the model */
public class GuiModelView extends Gui {

	private final ArrayList<ModelInstance> modelInstances;
	private final GuiViewWorld guiViewWorld;
	private final BoundingBox theBox;
	private int boxID;

	public GuiModelView() {
		super();
		this.modelInstances = new ArrayList<ModelInstance>();
		this.guiViewWorld = new GuiViewWorld();
		this.theBox = new BoundingBox();
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		int worldID = ModelEditorMod.WORLD_ID;
		CameraProjective camera = new ModelEditorCamera(renderer.getMainRenderer().getGLFWWindow());
		this.guiViewWorld.set(camera, worldID);
		this.guiViewWorld.initialize(renderer);
		this.boxID = this.guiViewWorld.getWorldRenderer().getLineRendererFactory().addBox(this.theBox);

		this.addChild(this.guiViewWorld);
		this.addChild(new GuiViewDebug(camera));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.updateHoveredBlock();
		this.updateModelInstances();
	}

	private final void updateModelInstances() {
		for (ModelInstance modelInstance : this.modelInstances) {
			modelInstance.getEntity().update();
			modelInstance.update();
		}
	}

	private final void updateHoveredBlock() {
		ModelEditorCamera camera = (ModelEditorCamera) this.guiViewWorld.getWorldRenderer().getCamera();
		Vector3f ray = new Vector3f();
		CameraPicker.ray(ray, camera, this.getMouseX(), this.getMouseY());
		Raycasting.raycast(camera.getPosition(), ray, 256.0f, new RaycastingCallback() {
			@Override
			public boolean onRaycastCoordinates(float x, float y, float z, Vector3f face) {
				World world = getWorld();
				Block block = world.getBlock(x, y, z);
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

	public final ModelEditorCamera getCamera() {
		return ((ModelEditorCamera) this.guiViewWorld.getWorldRenderer().getCamera());
	}

	public final World getWorld() {
		return (this.guiViewWorld.getWorldRenderer().getWorld());
	}

	public final void addModelInstance(ModelInstance modelInstance) {
		this.modelInstances.add(modelInstance);
		this.guiViewWorld.getWorldRenderer().getModelRendererFactory().addModelInstance(modelInstance);
	}

	public final void removeModelInstance(ModelInstance modelInstance) {
		this.modelInstances.remove(modelInstance);
		this.guiViewWorld.getWorldRenderer().getModelRendererFactory().removeModelInstance(modelInstance);
	}
}
