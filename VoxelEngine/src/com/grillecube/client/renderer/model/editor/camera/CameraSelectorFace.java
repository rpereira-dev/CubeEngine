package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.camera.CameraPicker;
import com.grillecube.client.renderer.camera.Raycasting;
import com.grillecube.client.renderer.camera.RaycastingCallback;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.lines.Line;
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
import com.grillecube.common.world.entity.Entity;

public class CameraSelectorFace extends CameraSelector {

	protected final Vector3i hovered;
	private final Vector3i firstBlock;
	private final Vector3i secondBlock;
	private Face face;
	private Vector3f[] quad;
	private Line[] lines;

	public CameraSelectorFace(GuiModelView guiModelView, Color color) {
		super(guiModelView, color);
		this.hovered = new Vector3i();
		this.firstBlock = new Vector3i();
		this.secondBlock = new Vector3i();
		this.quad = new Vector3f[] { new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };
		this.lines = new Line[] { new Line(this.quad[0], color, this.quad[1], color),
				new Line(this.quad[1], color, this.quad[2], color), new Line(this.quad[2], color, this.quad[3], color),
				new Line(this.quad[3], color, this.quad[0], color) };
	}

	@Override
	public void update() {
		LineRendererFactory lines = this.getGuiModelView().getWorldRenderer().getLineRendererFactory();
		lines.removeAllLines();
		LineRendererFactory factory = super.getGuiModelView().getWorldRenderer().getLineRendererFactory();
		for (Line line : this.lines) {
			factory.addLine(line);
		}

	}

	private final void updateSelection() {

		if (!super.isLeftPressed()) {
			this.firstBlock.set(this.hovered);
		}
		this.secondBlock.set(this.hovered.x, this.hovered.y, this.hovered.z);

		Vector3i o0 = BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][0]];
		Vector3i o1 = BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][1]];
		Vector3i o2 = BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][2]];
		Vector3i o3 = BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][3]];
		this.quad[0].set(this.getPositionX() + this.getSizeX() * o0.x, this.getPositionY() + this.getSizeY() * o0.y,
				this.getPositionZ() + this.getSizeZ() * o0.z);
		this.quad[1].set(this.getPositionX() + this.getSizeX() * o1.x, this.getPositionY() + this.getSizeY() * o1.y,
				this.getPositionZ() + this.getSizeZ() * o1.z);
		this.quad[2].set(this.getPositionX() + this.getSizeX() * o2.x, this.getPositionY() + this.getSizeY() * o2.y,
				this.getPositionZ() + this.getSizeZ() * o2.z);
		this.quad[3].set(this.getPositionX() + this.getSizeX() * o3.x, this.getPositionY() + this.getSizeY() * o3.y,
				this.getPositionZ() + this.getSizeZ() * o3.z);
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
		if (!super.isLeftPressed()) {
			float speed = this.getCamera().getR() * 0.14f;
			this.getCamera().increaseR((float) (-event.getScrollY() * speed));
		}
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

		Entity entity = modelInstance.getEntity();
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
							hovered.set(x, y, z);
							face = Face.fromVec(faceNormal);
							return (true);
						}
						return (false);
					}
				});
	}

	@Override
	public void onLeftPressed() {
	}

	@Override
	public void onRightPressed() {
		ModelEditorCamera camera = this.getCamera();
		camera.getWindow().setCursor(false);
		camera.getWindow().setCursorCenter();

		float cx = this.getCamera().getPosition().x;
		float cy = this.getCamera().getPosition().y;
		float cz = this.getCamera().getPosition().z;

		float cphi = this.getCamera().getPhi();
		float ctheta = this.getCamera().getTheta();
		// float cr = this.getCamera().getR();

		float u = this.getBlockSizeUnit();

		float x = (this.firstBlock.x + 0.5f) * u;
		float y = (this.firstBlock.y + 0.5f) * u;
		float z = (this.firstBlock.z + 0.5f) * u;

		// float dx = x - this.getCamera().getCenter().getX();
		// float dy = y - this.getCamera().getCenter().getY();
		// float dz = z - this.getCamera().getCenter().getZ();
		//
		// float A = (float) (-dx / cr + Math.cos(ctheta) * Math.sin(cphi));
		// float B = (float) (-dy / cr + Math.cos(ctheta) * Math.cos(cphi));
		//
		// float theta = (float) Math.asin(Math.sin(ctheta) - dz / cr);
		// float phi = (float) 0.0f;

		float phi = cphi;
		float theta = ctheta;
		float r = (float) Vector3f.distance(x, y, z, cx, cy, cz);

		this.getCamera().setCenter(x, y, z);
		this.getCamera().setR(r);
		this.getCamera().setPhi(phi);
		this.getCamera().setTheta(theta);
	}

	@Override
	public void onRightReleased() {
		this.getCamera().getWindow().setCursor(true);
		this.getCamera().getWindow().setCursorCenter();
	}

	@Override
	public void onLeftReleased() {
	}

	@Override
	public Vector3i getBlock() {
		return (this.getFirstBlock());
	}

	public Vector3i getFirstBlock() {
		return (this.firstBlock);
	}

	public Vector3i getSecondBlock() {
		return (this.secondBlock);
	}

	public Face getFace() {
		return (this.face);
	}

	public final int getX() {
		return (Maths.min(this.firstBlock.x, this.secondBlock.x));
	}

	public final int getY() {
		return (Maths.min(this.firstBlock.y, this.secondBlock.y));
	}

	public final int getZ() {
		return (Maths.min(this.firstBlock.z, this.secondBlock.z));
	}

	public final int getWidth() {
		return (Maths.abs(this.firstBlock.x - this.secondBlock.x) + 1);
	}

	public final int getDepth() {
		return (Maths.abs(this.firstBlock.y - this.secondBlock.y) + 1);
	}

	public final int getHeight() {
		return (Maths.abs(this.firstBlock.z - this.secondBlock.z) + 1);
	}

}
