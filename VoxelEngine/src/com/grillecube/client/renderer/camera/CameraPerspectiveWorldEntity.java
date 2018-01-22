package com.grillecube.client.renderer.camera;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.block.Blocks;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.control.Control;

/** a camera which follow the given entity, 3rd perso view */
public class CameraPerspectiveWorldEntity extends CameraPerspectiveWorldCentered {

	/** the entity which this camera follows */
	private Entity entity;

	public CameraPerspectiveWorldEntity(GLFWWindow window) {
		super(window);
	}

	@Override
	public void update() {
		super.update();
		float x = this.getEntity().getPositionX() + this.getEntity().getSizeX() * 0.5f;
		float y = this.getEntity().getPositionY() + this.getEntity().getSizeY() * 0.5f;
		float z = this.getEntity().getPositionZ() + this.getEntity().getSizeZ() * 1.0f;
		this.getEntity().setRotation(this.getRotX(), this.getRotY(), this.getRotZ());

		super.setCenter(x, y, z);

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

	public void setEntity(Entity entity) {
		this.entity = entity;
		super.setWorld(entity.getWorld());
	}

	public Entity getEntity() {
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
