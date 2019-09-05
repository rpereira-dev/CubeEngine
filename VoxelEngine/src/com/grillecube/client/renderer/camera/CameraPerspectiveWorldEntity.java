package com.grillecube.client.renderer.camera;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.world.World;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.entity.WorldEntity;
import com.grillecube.common.world.physic.Control;

/** a camera which follow the given entity, 3rd perso view */
public class CameraPerspectiveWorldEntity extends CameraPerspectiveWorldCentered {

	/** the entity which this camera follows */
	private WorldEntity entity;

	public CameraPerspectiveWorldEntity(GLFWWindow window) {
		super(window);
	}

	@Override
	public void update() {
		super.update();
		this.updateCenter();
		this.updateRotation();
		this.updateControls();
	}

	/** ensure that the camera isn't "in-wall", and that the entity can be seen */
	@Override
	protected void calculateCameraPosition(float distance) {
		super.calculateCameraPosition(this.getR());

		World world = this.getWorld();
		if (world == null) {
			return;
		}
		// this.getEntity().setPosition(0, 0, 120);

		Vector3f pos = new Vector3f(this.getEntity().getPositionX(), this.getEntity().getPositionY(),
				this.getEntity().getPositionZ());
		Vector3f dir = (Vector3f) Vector3f.sub(this.getPosition(), pos, null);
		float length = dir.length();
		dir.scale(1 / length);
		Raycasting.raycast(pos, dir, length, new RaycastingCallback() {
			@Override
			public boolean onRaycastCoordinates(int x, int y, int z, Vector3i face) {
				Block block = world.getBlock(x, y, z);
				if (block.isVisible() && !block.bypassRaycast()) {
					float dx = pos.x - x;
					float dy = pos.y - y - face.getY();
					float dz = pos.z - z - face.getZ();
					float minLength = Maths.sqrt(dx * dx + dy * dy + dz * dz) - 1.0f;
					if (minLength < 0) {
						minLength = 0;
					}
					CameraPerspectiveWorldEntity.super.calculateCameraPosition(minLength);
					return (true);
				}
				return (false);
			}
		});
	}

	private final void updateCenter() {
		float x = this.getEntity().getPositionX() + this.getEntity().getSizeX() * 0.5f;
		float y = this.getEntity().getPositionY() + this.getEntity().getSizeY() * 0.5f;
		float z = this.getEntity().getPositionZ() + this.getEntity().getSizeZ() * 1.0f;
		super.setCenter(x, y, z);
	}

	private final void updateRotation() {
		this.getEntity().setRotation(this.getRotX(), this.getRotY(), this.getRotZ());
	}

	private final void updateControls() {
		if (GLH.glhGetWindow().isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			this.getEntity().jump();
		}
		if (GLH.glhGetWindow().isKeyPressed(GLFW.GLFW_KEY_W)) {
			this.getEntity().addControl(Control.FORWARD);
		}
		if (GLH.glhGetWindow().isKeyPressed(GLFW.GLFW_KEY_S)) {
			this.getEntity().addControl(Control.BACKWARD);
		}
		if (GLH.glhGetWindow().isKeyPressed(GLFW.GLFW_KEY_D)) {
			this.getEntity().addControl(Control.STRAFE_RIGHT);
		}
		if (GLH.glhGetWindow().isKeyPressed(GLFW.GLFW_KEY_A)) {
			this.getEntity().addControl(Control.STRAFE_LEFT);
		}
	}

	public void setEntity(WorldEntity entity) {
		this.entity = entity;
		super.setWorld(entity.getWorld());
	}

	public WorldEntity getEntity() {
		return (this.entity);
	}

	@Override
	public void invokeCursorPos(GLFWWindow window, double xpos, double ypos) {
		if (window.isMouseRightPressed()) {
			float pitch = (float) ((window.getMouseDY()) * 0.1f);
			super.increaseRotX(pitch);

			float angle = (float) ((window.getMouseDX()) * 0.3f);
			super.increaseRotZ(-angle);
		} else {

			super.increaseTheta((float) (super.getWindow().getMouseDY()) * 0.005f);
			super.increasePhi((float) ((super.getWindow().getMouseDX()) * 0.005f));
		}
		// this.getEntity().setPosition(0, 0, 16);
		// this.getEntity().setPositionVelocity(0, 0, 0);

	}

	@Override
	public void invokeKeyRelease(GLFWWindow glfwWindow, int key, int scancode, int mods) {
	}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_R) {
			Vector3f pos = new Vector3f();
			pos.set(this.entity.getPositionX(), this.entity.getPositionY(), this.entity.getPositionZ());
			pos.add(this.entity.getViewVector());
			pos.add(this.entity.getViewVector());
			super.setBlock(Blocks.LIGHT, pos);
		}
	}

	@Override
	public void invokeMouseScroll(GLFWWindow window, double xpos, double ypos) {
		float speed = (float) Maths.max(super.getR() * 0.1f, 0.2f);
		this.increaseR((float) (-ypos * speed));
		if (super.getR() < 0) {
			super.setR(0);
		}
	}

	@Override
	public void setRotY(float y) {
		if (this.getEntity() != null) {
			super.setRotY(y + this.getEntity().getRotationY());
		} else {
			super.setRotY(y);
		}
	}
}
