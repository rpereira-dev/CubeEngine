package com.grillecube.client.renderer.model.editor.gui;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.renderer.camera.CameraPicker;
import com.grillecube.client.renderer.camera.Raycasting;
import com.grillecube.client.renderer.camera.RaycastingCallback;
import com.grillecube.client.renderer.model.editor.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.mesher.BlockData;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.WorldFlat;
import com.grillecube.common.world.entity.Entity;

public class CameraController {

	private final Vector3i hoveredBlock;
	private final ArrayList<Vector3i> selectedBlocks;
	private final BoundingBox theBox;
	private int boxID;

	public CameraController(WorldRenderer<WorldFlat> worldRenderer) {
		this.hoveredBlock = new Vector3i();
		this.selectedBlocks = new ArrayList<Vector3i>();
		this.theBox = new BoundingBox();
		this.boxID = worldRenderer.getLineRendererFactory().addBox(this.theBox);
	}

	public void update(ModelInstance modelInstance, WorldRenderer<WorldFlat> worldRenderer, float mouseX,
			float mouseY) {
		if (modelInstance == null) {
			this.theBox.setMinSize(0, 0, 0, 1, 1, 1);
			worldRenderer.getLineRendererFactory().setBox(this.theBox, this.boxID);
			return;
		}

		// extract objcets
		EditableModel model = (EditableModel) modelInstance.getModel();
		Entity entity = modelInstance.getEntity();
		float s = model.getBlockSizeUnit();
		ModelEditorCamera camera = (ModelEditorCamera) worldRenderer.getCamera();

		// origin relatively to the model
		Vector4f origin = new Vector4f(camera.getPosition(), 1.0f);
		Matrix4f transform = new Matrix4f();
		transform.translate(entity.getPosition().negate(null));
		transform.scale(1 / s);
		Matrix4f.transform(transform, origin, origin);

		// ray relatively to the model
		Vector3f ray = new Vector3f();
		CameraPicker.ray(ray, camera, mouseX, mouseY);

		Raycasting.raycast(origin.x, origin.y, origin.z, ray.x, ray.y, ray.z, 256.0f, 256.0f, 256.0f,
				new RaycastingCallback() {
					@Override
					public boolean onRaycastCoordinates(int x, int y, int z, Vector3i face) {
						// System.out.println(x + " : " + y + " : " + z);
						if (y < model.getMinY() || model.getBlockData(x, y, z) != null) {
							int bx = x + face.x;
							int by = y + face.y;
							int bz = z + face.z;
							Vector4f boxPos = new Vector4f(bx, by, bz, 1.0f);
							Matrix4f.transform(Matrix4f.invert(transform, transform), boxPos, boxPos);
							theBox.setMinSize(boxPos.x, boxPos.y, boxPos.z, s, s, s);
							worldRenderer.getLineRendererFactory().setBox(theBox, boxID);
							hoveredBlock.set(bx, by, bz);
							return (true);
						}
						return (false);
					}
				});
	}

	public final void onKeyPress(ModelInstance modelInstance, int key, int mods, int scancode) {
		if (modelInstance != null) {
			if (key == GLFW.GLFW_KEY_E) {
				EditableModel model = (EditableModel) modelInstance.getModel();
				if (model != null) {
					model.setBlockData(new BlockData(), this.hoveredBlock.x, this.hoveredBlock.y, this.hoveredBlock.z);
					model.requestMeshUpdate();
				}
			}
		}
	}

	public final void onLeftPressed(float mouseX, float mouseY) {
		// TODO Auto-generated method stub

	}

	public final void onLeftReleased(float mouseX, float mouseY) {
		// TODO Auto-generated method stub

	}
}
