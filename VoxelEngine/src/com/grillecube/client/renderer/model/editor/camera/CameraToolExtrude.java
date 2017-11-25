
package com.grillecube.client.renderer.model.editor.camera;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.renderer.blocks.BlockRenderer;
import com.grillecube.client.renderer.camera.CameraPicker;
import com.grillecube.client.renderer.camera.Raycasting;
import com.grillecube.client.renderer.camera.RaycastingCallback;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.lines.Line;
import com.grillecube.client.renderer.lines.LineRendererFactory;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
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

public class CameraToolExtrude extends CameraTool implements Positioneable, Sizeable {

	protected final Vector3i hovered;
	private final Vector3i firstBlock;
	private final Vector3i secondBlock;
	private Face face;
	private Vector3f[] quad;
	private Line[] lines;

	public CameraToolExtrude(GuiModelView guiModelView) {
		super(guiModelView);
		this.hovered = new Vector3i();
		this.firstBlock = new Vector3i();
		this.secondBlock = new Vector3i();
		this.quad = new Vector3f[] { new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };
		this.lines = new Line[] { new Line(this.quad[0], Color.YELLOW, this.quad[1], Color.YELLOW),
				new Line(this.quad[1], Color.YELLOW, this.quad[2], Color.YELLOW),
				new Line(this.quad[2], Color.YELLOW, this.quad[3], Color.YELLOW),
				new Line(this.quad[3], Color.YELLOW, this.quad[0], Color.YELLOW) };
	}

	@Override
	public void onKeyPress(GuiEventKeyPress<GuiModelView> event) {
		ModelInstance modelInstance = this.guiModelView.getSelectedModelInstance();
		if (modelInstance != null) {
			if (event.getKey() == GLFW.GLFW_KEY_Z) {
				this.extrudeBlocks((EditableModel) modelInstance.getModel());
			}
		}
	}

	private void extrudeBlocks(EditableModel model) {

		boolean updated = false;

		int stepx = -face.getVector().x;
		int stepy = -face.getVector().y;
		int stepz = -face.getVector().z;

		int x1 = 0;
		if (stepx < 0) {
			x1 = model.getMinx() - 1;
		} else if (stepx > 0) {
			x1 = model.getMaxx() + 1;
		}

		int y1 = 0;
		if (stepy < 0) {
			y1 = model.getMiny() - 1;
		} else if (stepy > 0) {
			y1 = model.getMaxy() + 1;
		}

		int z1 = 0;
		if (stepz < 0) {
			z1 = model.getMinz() - 1;
		} else if (stepz > 0) {
			z1 = model.getMaxz() + 1;
		}

		Vector3i pos = new Vector3i();

		if (stepx != 0) {
			for (int dy = 0; dy < this.getHeight(); dy++) {
				for (int dz = 0; dz < this.getDepth(); dz++) {
					int x0 = this.getX();
					int y0 = this.getY() + dy;
					int z0 = this.getZ() + dz;
					int x;
					for (x = x0; x != x1; x += stepx) {
						if (model.unsetBlockData(pos.set(x, y0, z0))) {
							updated = true;
						}
					}
				}
			}
		}

		if (stepy != 0) {
			for (int dx = 0; dx < this.getWidth(); dx++) {
				for (int dz = 0; dz < this.getDepth(); dz++) {
					int x0 = this.getX() + dx;
					int y0 = this.getY();
					int z0 = this.getZ() + dz;
					int y;
					for (y = y0; y != y1; y += stepy) {
						if (model.unsetBlockData(pos.set(x0, y, z0))) {
							updated = true;
						}
					}
				}
			}
		}

		if (stepz != 0) {
			for (int dx = 0; dx < this.getWidth(); dx++) {
				for (int dy = 0; dy < this.getHeight(); dy++) {
					int x0 = this.getX() + dx;
					int y0 = this.getY() + dy;
					int z0 = this.getZ();
					int z;
					for (z = z0; z != z1; z += stepz) {
						if (model.unsetBlockData(pos.set(x0, y0, z))) {
							updated = true;
						}
					}
				}
			}
		}

		if (updated) {
			model.generateMesh();
			this.guiModelView.getToolbox().getSelectedModelPanels().getGuiToolboxModelPanelSkin().refresh();
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

		Vector3i pos = new Vector3i();
		Raycasting.raycast(origin.x, origin.y, origin.z, ray.x, ray.y, ray.z, 256.0f, 256.0f, 256.0f,
				new RaycastingCallback() {
					@Override
					public boolean onRaycastCoordinates(int x, int y, int z, Vector3i theFace) {
						// System.out.println(x + " : " + y + " : " + z);
						if (y < 0 || model.getBlockData(pos.set(x, y, z)) != null) {
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
		this.secondBlock.set(this.hovered.x, this.hovered.y, this.hovered.z);

		Vector3i o0 = BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][0]];
		Vector3i o1 = BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][1]];
		Vector3i o2 = BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][2]];
		Vector3i o3 = BlockRenderer.VERTICES[BlockRenderer.FACES_VERTICES[face.getID()][3]];
		this.quad[0].set(this.getX() + this.getWidth() * o0.x, this.getY() + this.getHeight() * o0.y,
				this.getZ() + this.getDepth() * o0.z);
		this.quad[1].set(this.getX() + this.getWidth() * o1.x, this.getY() + this.getHeight() * o1.y,
				this.getZ() + this.getDepth() * o1.z);
		this.quad[2].set(this.getX() + this.getWidth() * o2.x, this.getY() + this.getHeight() * o2.y,
				this.getZ() + this.getDepth() * o2.z);
		this.quad[3].set(this.getX() + this.getWidth() * o3.x, this.getY() + this.getHeight() * o3.y,
				this.getZ() + this.getDepth() * o3.z);

		LineRendererFactory factory = super.guiModelView.getWorldRenderer().getLineRendererFactory();
		for (Line line : this.lines) {
			factory.addLine(line);
		}

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
		return ("Extrude");
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
