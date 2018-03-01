package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.camera.CameraDestinationCenter;
import com.grillecube.client.renderer.camera.CameraPicker;
import com.grillecube.client.renderer.camera.Raycasting;
import com.grillecube.client.renderer.camera.RaycastingCallback;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.lines.LineRendererFactory;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.utils.Color;
import com.grillecube.common.world.entity.WorldEntity;

public class CameraSelectorBlockFace extends CameraSelector {

	protected final Vector3i hovered;
	private final Vector3i firstBlock;
	private final Vector3i secondBlock;
	private Face face;
	private int expansion;

	public CameraSelectorBlockFace(GuiModelView guiModelView, Color color) {
		super(guiModelView, color);
		this.hovered = new Vector3i();
		this.firstBlock = new Vector3i();
		this.secondBlock = new Vector3i();
		this.expansion = 0;
	}

	@Override
	public void update() {
		LineRendererFactory lines = this.getGuiModelView().getWorldRenderer().getLineRendererFactory();
		lines.removeAllLines();
		lines.addBox(this, this, this.getSelectorColor());
	}

	private final void updateSelection() {
		if (!super.isLeftPressed()) {
			this.firstBlock.set(this.hovered);
		}
		this.updateSecondBlock();
	}

	private final void updateCameraRotation() {
		// rotate
		if (super.isRightPressed()) {
			super.getCamera().increaseTheta(-(super.getMouseDY()) * 1.5f);
			super.getCamera().increasePhi((super.getMouseDX()) * 1.5f);
		} else {
			this.updateHoveredBlock();
		}
	}

	@Override
	public void onMouseScroll(GuiEventMouseScroll<GuiModelView> event) {
		if (super.isLeftPressed()) {
			int d = -Maths.sign(event.getScrollY());
			this.expand(d);
		} else {
			float speed = super.getCamera().getR() * 0.14f;
			super.getCamera().increaseR((float) (-event.getScrollY() * speed));
		}
	}

	private void expand(int d) {
		this.expansion += d;
		this.updateSecondBlock();
	}

	private final void updateSecondBlock() {
		if (this.face == null) {
			this.secondBlock.set(this.hovered);
			return;
		}
		int x = this.hovered.x + this.expansion * this.face.getVector().x;
		int y = this.hovered.y + this.expansion * this.face.getVector().y;
		int z = this.hovered.z + this.expansion * this.face.getVector().z;
		this.secondBlock.set(x, y, z);
	}

	@Override
	public void onMouseMove() {
		this.updateCameraRotation();
		this.updateSelection();
	}

	private final void updateHoveredBlock() {

		ModelInstance modelInstance = super.getSelectedModelInstance();
		EditableModelLayer modelLayer = super.getSelectedModelLayer();

		// extract objects
		if (modelInstance == null || modelLayer == null) {
			return;
		}

		WorldEntity entity = modelInstance.getEntity();
		float s = modelLayer.getBlockSizeUnit();
		ModelEditorCamera camera = (ModelEditorCamera) this.getCamera();

		// origin relatively to the model
		Vector4f origin = new Vector4f(camera.getPosition(), 1.0f);
		Matrix4f transform = new Matrix4f();
		transform.translate(-entity.getPositionX(), -entity.getPositionY(), -entity.getPositionZ());
		transform.scale(1 / s);
		Matrix4f.transform(transform, origin, origin);

		// ray relatively to the model
		Vector3f ray = new Vector3f();
		CameraPicker.ray(ray, camera, super.getMouseX(), super.getMouseY());

		Vector3i pos = new Vector3i();
		Raycasting.raycast(origin.x, origin.y, origin.z, ray.x, ray.y, ray.z, 256.0f, 256.0f, 256.0f,
				new RaycastingCallback() {
					@Override
					public boolean onRaycastCoordinates(int x, int y, int z, Vector3i faceNormal) {
						// System.out.println(x + " : " + y + " : " + z);
						if (z < 0 || modelLayer.getBlockData(pos.set(x, y, z)) != null) {
							int bx = x + faceNormal.x;
							int by = y + faceNormal.y;
							int bz = z + faceNormal.z;
							hovered.set(bx, by, bz);
							face = Face.fromVec(faceNormal);
							return (true);
						}
						return (false);
					}
				});
	}

	@Override
	public void onLeftPressed() {
		this.expansion = 0;
	}

	@Override
	public void onRightPressed() {
		ModelEditorCamera camera = this.getCamera();
		camera.getWindow().setCursor(false);
		camera.getWindow().setCursorCenter();

		float cx = (this.firstBlock.x + 0.5f) * this.getBlockSizeUnit();
		float cy = (this.firstBlock.y + 0.5f) * this.getBlockSizeUnit();
		float cz = (this.firstBlock.z + 0.5f) * this.getBlockSizeUnit();

		float X = camera.getPosition().x - cx;
		float Y = camera.getPosition().y - cy;
		float Z = camera.getPosition().z - cz;

		float r = Maths.sqrt(X * X + Y * Y + Z * Z);
		float theta = Maths.asin(Z / r);
		float phi = camera.getPhi();// (float) Math.atan2(X, Y);
		Vector3f center = new Vector3f(cx, cy, cz);
		// camera.setCenter(center);
		// camera.setR(r);
		// camera.setTheta(theta);
		// camera.setPhi(phi);
		camera.addDestination(new CameraDestinationCenter(r, phi, theta, center, 0.2f));
	}

	@Override
	public void onRightReleased() {
		this.getCamera().getWindow().setCursor(true);
		this.getCamera().getWindow().setCursorCenter();
	}

	@Override
	public void onLeftReleased() {
		this.expansion = 0;
	}

	@Override
	public Vector3i getBlock() {
		return (this.getFirstBlock());
	}

	@Override
	public final Face getFace() {
		return (this.face);
	}

	public Vector3i getFirstBlock() {
		return (this.firstBlock);
	}

	public Vector3i getSecondBlock() {
		return (this.secondBlock);
	}

	@Override
	public final int getX() {
		return (Maths.min(this.firstBlock.x, this.secondBlock.x));
	}

	@Override
	public final int getY() {
		return (Maths.min(this.firstBlock.y, this.secondBlock.y));
	}

	@Override
	public final int getZ() {
		return (Maths.min(this.firstBlock.z, this.secondBlock.z));
	}

	@Override
	public final int getWidth() {
		return (Maths.abs(this.firstBlock.x - this.secondBlock.x) + 1);
	}

	@Override
	public final int getDepth() {
		return (Maths.abs(this.firstBlock.y - this.secondBlock.y) + 1);
	}

	@Override
	public final int getHeight() {
		return (Maths.abs(this.firstBlock.z - this.secondBlock.z) + 1);
	}
}
