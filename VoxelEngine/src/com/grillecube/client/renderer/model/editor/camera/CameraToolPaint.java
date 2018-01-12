
package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.camera.CameraPicker;
import com.grillecube.client.renderer.camera.Raycasting;
import com.grillecube.client.renderer.camera.RaycastingCallback;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.lines.Line;
import com.grillecube.client.renderer.lines.LineRendererFactory;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.utils.Color;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.collision.Positioneable;
import com.grillecube.common.world.entity.collision.Sizeable;

public class CameraToolPaint extends CameraTool implements Positioneable, Sizeable {

	protected final Vector3i hovered;
	private final Vector3i firstBlock;
	private final Vector3i secondBlock;
	private Face face;
	private Vector3f[] quad;
	private Line[] lines;

	public CameraToolPaint(GuiModelView guiModelView) {
		super(guiModelView);
		this.hovered = new Vector3i();
		this.firstBlock = new Vector3i();
		this.secondBlock = new Vector3i();
		this.quad = new Vector3f[] { new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };
		this.lines = new Line[] { new Line(this.quad[0], Color.BLUE, this.quad[1], Color.BLUE),
				new Line(this.quad[1], Color.BLUE, this.quad[2], Color.BLUE),
				new Line(this.quad[2], Color.BLUE, this.quad[3], Color.BLUE),
				new Line(this.quad[3], Color.BLUE, this.quad[0], Color.BLUE) };
	}

	@Override
	public boolean action(ModelInstance modelInstance, EditableModelLayer editableModelLayer) {
		int x0 = getX();
		int y0 = getY();
		int z0 = getZ();

		Vector3i pos = new Vector3i();
		boolean generate = false;
		for (int dx = 0; dx < getWidth(); dx++) {
			for (int dy = 0; dy < getHeight(); dy++) {
				for (int dz = 0; dz < getDepth(); dz++) {
					ModelBlockData blockData = editableModelLayer.getBlockData(pos.set(x0 + dx, y0 + dy, z0 + dz));
					if (blockData != null) {
						blockData.setColor(this.guiModelView.getSelectedSkin(), this.guiModelView.getSelectedColor(),
								this.face);
						generate = true;
					}
				}
			}
		}
		return (generate);
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
		camera.setR((float) Vector3f.distance(camera.getCenter(), camera.getPosition()));
	}

	@Override
	public void onRightReleased() {
		this.getCamera().getWindow().setCursor(true);
		this.getCamera().getWindow().setCursorCenter();
	}

	@Override
	public void onLeftReleased() {
	}

	private final void updateHoveredBlock() {

		ModelInstance modelInstance = this.guiModelView.getSelectedModelInstance();
		EditableModelLayer modelLayer = this.guiModelView.getSelectedModelLayer();

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
		CameraPicker.ray(ray, camera, this.guiModelView.getMouseX(), this.guiModelView.getMouseY());

		Vector3i pos = new Vector3i();
		Raycasting.raycast(origin.x, origin.y, origin.z, ray.x, ray.y, ray.z, 256.0f, 256.0f, 256.0f,
				new RaycastingCallback() {
					@Override
					public boolean onRaycastCoordinates(int x, int y, int z, Vector3i theFace) {
						// System.out.println(x + " : " + y + " : " + z);
						if (y < 0 || modelLayer.getBlockData(pos.set(x, y, z)) != null) {
							hovered.set(x, y, z);
							face = Face.fromVec(theFace);
							return (true);
						}
						return (false);
					}
				});

	}

	@Override
	public void onMouseScroll(GuiEventMouseScroll<GuiModelView> event) {
		if (!super.guiModelView.isLeftPressed()) {
			float speed = this.getCamera().getR() * 0.14f;
			this.getCamera().increaseR((float) (-event.getScrollY() * speed));
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

		LineRendererFactory factory = super.guiModelView.getWorldRenderer().getLineRendererFactory();
		for (Line line : this.lines) {
			factory.addLine(line);
		}

	}

	private final void updateCameraRotation() {
		// rotate
		if (this.guiModelView.isRightPressed()) {
			float pitch = (float) ((this.guiModelView.getPrevMouseY() - this.guiModelView.getMouseY()) * 64.0f);
			this.getCamera().increaseRotX(pitch);

			float angle = (float) ((this.guiModelView.getPrevMouseX() - this.guiModelView.getMouseX()) * 128.0f);
			this.getCamera().increaseRotZ(angle);

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
		return ("Paint");
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

	public final int getHeight() {
		return (Maths.abs(this.firstBlock.y - this.secondBlock.y) + 1);
	}

	public final int getDepth() {
		return (Maths.abs(this.firstBlock.z - this.secondBlock.z) + 1);
	}

	@Override
	public float getPositionX() {
		return (this.getX() * this.getBlockSizeUnit());
	}

	@Override
	public float getPositionY() {
		return ((this.getY() + 0.05f) * this.getBlockSizeUnit());
	}

	@Override
	public float getPositionZ() {
		return (this.getZ() * this.getBlockSizeUnit());
	}

	@Override
	public float getPositionVelocityX() {
		return 0;
	}

	@Override
	public float getPositionVelocityY() {
		return 0;
	}

	@Override
	public float getPositionVelocityZ() {
		return 0;
	}

	@Override
	public float getPositionAccelerationX() {
		return 0;
	}

	@Override
	public float getPositionAccelerationY() {
		return 0;
	}

	@Override
	public float getPositionAccelerationZ() {
		return 0;
	}

	@Override
	public void setPositionX(float x) {
	}

	@Override
	public void setPositionY(float y) {
	}

	@Override
	public void setPositionZ(float z) {
	}

	@Override
	public void setPositionVelocityX(float vx) {
	}

	@Override
	public void setPositionVelocityY(float vy) {
	}

	@Override
	public void setPositionVelocityZ(float vz) {
	}

	@Override
	public void setPositionAccelerationX(float ax) {
	}

	@Override
	public void setPositionAccelerationY(float ay) {
	}

	@Override
	public void setPositionAccelerationZ(float az) {
	}

	@Override
	public float getSizeX() {
		return (this.getWidth() * this.getBlockSizeUnit());
	}

	@Override
	public float getSizeY() {
		return (this.getHeight() * this.getBlockSizeUnit());
	}

	@Override
	public float getSizeZ() {
		return (this.getDepth() * this.getBlockSizeUnit());
	}

	@Override
	public float getSizeVelocityX() {
		return 0;
	}

	@Override
	public float getSizeVelocityY() {
		return 0;
	}

	@Override
	public float getSizeVelocityZ() {
		return 0;
	}

	@Override
	public float getSizeAccelerationX() {
		return 0;
	}

	@Override
	public float getSizeAccelerationY() {
		return 0;
	}

	@Override
	public float getSizeAccelerationZ() {
		return 0;
	}

	@Override
	public void setSizeX(float x) {
	}

	@Override
	public void setSizeY(float y) {
	}

	@Override
	public void setSizeZ(float z) {
	}

	@Override
	public void setSizeVelocityX(float vx) {
	}

	@Override
	public void setSizeVelocityY(float vy) {
	}

	@Override
	public void setSizeVelocityZ(float vz) {
	}

	@Override
	public void setSizeAccelerationX(float ax) {
	}

	@Override
	public void setSizeAccelerationY(float ay) {
	}

	@Override
	public void setSizeAccelerationZ(float az) {
	}
}
