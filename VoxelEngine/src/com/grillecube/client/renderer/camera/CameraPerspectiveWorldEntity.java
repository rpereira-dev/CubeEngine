package com.grillecube.client.renderer.camera;

import org.lwjgl.glfw.GLFW;

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
		float y = this.getEntity().getPositionY() + this.getEntity().getSizeY() * 1.0f;
		float z = this.getEntity().getPositionZ() + this.getEntity().getSizeZ() * 0.5f;
		super.setCenter(x, y, z);
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
			super.increasePitch(pitch);

			float angle = (float) ((window.getMouseDX()) * 0.3f);
			super.setAngleAroundCenter((float) (super.getAngleAroundCenter() - angle));
		} else {
			double dy = -(window.getMouseDX() * 0.2f);
			this.entity.setRotationY(this.entity.getRotationY() + (float) dy);
			super.increasePitch((float) (window.getMouseDY() * 0.1f));
			super.increaseAngleAroundCenter((float) dy);
		}
	}

	@Override
	public void invokeKeyRelease(GLFWWindow glfwWindow, int key, int scancode, int mods) {
	}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_W) {
			this.entity.addControl(Control.FORWARD);
		}
		if (key == GLFW.GLFW_KEY_S) {
			this.entity.addControl(Control.BACKWARD);
		}
		if (key == GLFW.GLFW_KEY_D) {
			this.entity.addControl(Control.STRAFE_LEFT);
		}
		if (key == GLFW.GLFW_KEY_A) {
			this.entity.addControl(Control.STRAFE_RIGHT);
		}
		if (key == GLFW.GLFW_KEY_SPACE && !this.entity.isJumping()) {
			this.entity.jump();
		}
		if (key == GLFW.GLFW_KEY_R) {
			Vector3f pos = new Vector3f();
			pos.set(this.entity.getPositionX(), this.entity.getPositionY(), this.entity.getPositionZ());
			pos.add(this.entity.getViewVector());
			pos.add(this.entity.getViewVector());
			super.setBlock(Blocks.LIQUID_WATER, pos);
		}
	}

	@Override
	public void invokeMouseScroll(GLFWWindow window, double xpos, double ypos) {
		float speed = (float) Maths.max(super.getDistanceFromCenter() * 0.1f, 0.2f);
		this.increaseDistanceFromCenter((float) (-ypos * speed));
	}

	@Override
	public void setYaw(float yaw) {
		super.setYaw(yaw + this.getEntity().getRotationY());
	}
}
