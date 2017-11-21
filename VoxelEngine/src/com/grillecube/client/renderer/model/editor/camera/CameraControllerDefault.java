
package com.grillecube.client.renderer.model.editor.camera;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.renderer.camera.CameraPicker;
import com.grillecube.client.renderer.camera.Raycasting;
import com.grillecube.client.renderer.camera.RaycastingCallback;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.model.EditableModel;
import com.grillecube.client.renderer.model.editor.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.entity.Entity;

public class CameraControllerDefault extends CameraController {

	private final CameraControllerSelection selection;
	protected final Vector3i hovered;
	private final Vector3i firstBlock;
	private final Vector3i secondBlock;
	private int ySelected;
	private int box;

	public CameraControllerDefault(GuiModelView guiModelView) {
		super(guiModelView);
		this.hovered = new Vector3i();
		this.firstBlock = new Vector3i();
		this.secondBlock = new Vector3i();
		this.selection = new CameraControllerSelectionBlocks(this, this.firstBlock, this.secondBlock);
		this.box = super.guiModelView.getWorldRenderer().getLineRendererFactory().addBox(this.selection,
				this.selection);
		this.ySelected = 0;
	}

	@Override
	public void onKeyPress(GuiEventKeyPress<GuiModelView> event) {
		ModelInstance modelInstance = this.guiModelView.getSelectedModelInstance();
		if (modelInstance != null) {
			if (event.getKey() == GLFW.GLFW_KEY_E) {
				EditableModel model = (EditableModel) modelInstance.getModel();
				if (model != null) {
					int x0 = this.selection.getX();
					int y0 = this.selection.getY();
					int z0 = this.selection.getZ();
					for (int dx = 0; dx < this.selection.getWidth(); dx++) {
						for (int dy = 0; dy < this.selection.getHeight(); dy++) {
							for (int dz = 0; dz < this.selection.getDepth(); dz++) {
								model.setBlockData(new ModelBlockData(x0 + dx, y0 + dy, z0 + dz));
							}
						}
					}
					model.requestMeshUpdate();
					requestPanelsRefresh();
				}
			}
		}
	}

	@Override
	public void onMouseMove() {
	}

	@Override
	public void onRightPressed() {
		ModelEditorCamera camera = this.getCamera();
		camera.getWindow().setCursor(false);
		float u = this.getBlockSizeUnit();
		float x = 0;
		float y = 0;
		float z = 0;
		this.getCamera().setCenter((x + 0.5f) * u, (y + 0.5f) * u, (z + 0.5f) * u);
		camera.setDistanceFromCenter((float) Vector3f.distance(camera.getCenter(), camera.getPosition()));
	}

	@Override
	public void onRightReleased() {
		this.getCamera().getWindow().setCursor(true);
		this.getCamera().getWindow().setCursorCenter();
	}

	@Override
	public void onLeftReleased() {
		this.ySelected = 0;
	}

	private final void updateHoveredBlock() {

		ModelInstance modelInstance = this.guiModelView.getSelectedModelInstance();

		// extract objects
		if (modelInstance == null) {
			return;
		}
		EditableModel model = (EditableModel) modelInstance.getModel();
		Entity entity = modelInstance.getEntity();
		float s = model.getBlockSizeUnit();
		ModelEditorCamera camera = (ModelEditorCamera) this.getCamera();

		// origin relatively to the model
		Vector4f origin = new Vector4f(camera.getPosition(), 1.0f);
		Matrix4f transform = new Matrix4f();
		transform.translate(-entity.getPositionX(), -entity.getPositionY(), -entity.getPositionZ());
		transform.scale(1 / s);
		Matrix4f.transform(transform, origin, origin);

		// ray relatively to the model
		Vector3f ray = new Vector3f();
		CameraPicker.ray(ray, camera, this.guiModelView.getMouseX(), this.guiModelView.getMouseY());

		Raycasting.raycast(origin.x, origin.y, origin.z, ray.x, ray.y, ray.z, 256.0f, 256.0f, 256.0f,
				new RaycastingCallback() {
					@Override
					public boolean onRaycastCoordinates(int x, int y, int z, Vector3i face) {
						// System.out.println(x + " : " + y + " : " + z);
						if (y < model.getMinY() || model.getBlockData(x, y, z) != null) {
							int bx = x + face.x;
							int by = y + face.y;
							int bz = z + face.z;
							hovered.set(bx, by, bz);
							return (true);
						}
						return (false);
					}
				});

	}

	@Override
	public void onMouseScroll(GuiEventMouseScroll<GuiModelView> event) {
		if (super.guiModelView.isLeftPressed()) {
			int dy = -Maths.sign(event.getScrollY());
			this.ySelected += dy;
			this.secondBlock.y += dy;
		} else {
			float speed = this.getCamera().getDistanceFromCenter() * 0.14f;
			this.getCamera().increaseDistanceFromCenter((float) (-event.getScrollY() * speed));
		}
	}

	@Override
	public void onUpdate() {
		this.updateCameraRotation();
		this.updateSelection();
	}

	private final void updateSelection() {
		if (!this.guiModelView.isLeftPressed()) {
			this.firstBlock.set(this.hovered);
		}
		this.secondBlock.set(this.hovered.x, this.hovered.y + this.ySelected, this.hovered.z);

		super.guiModelView.getWorldRenderer().getLineRendererFactory().setBox(this.selection, this.selection, this.box);
	}

	private final void updateCameraRotation() {
		// rotate
		if (this.guiModelView.isRightPressed()) {
			float pitch = (float) ((this.guiModelView.getPrevMouseY() - this.guiModelView.getMouseY()) * 64.0f);
			this.getCamera().increasePitch(pitch);

			float angle = (float) ((this.guiModelView.getPrevMouseX() - this.guiModelView.getMouseX()) * 128.0f);
			this.getCamera().increaseAngleAroundCenter(angle);

			this.hovered.set(0, 0, 0);
		} else {
			this.updateHoveredBlock();
		}

		float u = this.getBlockSizeUnit();
		float x = 0;
		float y = 0;
		float z = 0;
		this.getCamera().setCenter((x + 0.5f) * u, (y + 0.5f) * u, (z + 0.5f) * u);
	}

	@Override
	public String getName() {
		return ("Build");
	}
}
