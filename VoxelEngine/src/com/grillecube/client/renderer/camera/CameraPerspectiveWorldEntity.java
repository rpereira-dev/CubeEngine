package com.grillecube.client.renderer.camera;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.physic.EntityPhysic;

/** a camera which follow the given entity, 3rd perso view */
public class CameraPerspectiveWorldEntity extends CameraPerspectiveWorldCentered {

	/** the entity which this camera follows */
	private Entity _entity;

	public CameraPerspectiveWorldEntity(GLFWWindow window) {
		super(window);
	}

	@Override
	public void update() {
		super.update();
		float x = this.getEntity().getPosition().x;
		float y = this.getEntity().getPosition().y + this.getEntity().getHeight();
		float z = this.getEntity().getPosition().z;
		super.setCenter(x, y, z);
	}

	public void setEntity(Entity entity) {
		this._entity = entity;
		super.setWorld(entity.getWorld());
	}

	public Entity getEntity() {
		return (this._entity);
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
			this._entity.increaseYaw((float) dy);
			super.increasePitch((float) (window.getMouseDY() * 0.1f));
			super.increaseAngleAroundCenter((float) dy);
		}
	}

	@Override
	public void invokeKeyRelease(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_W) {
			this._entity.disablePhysic(EntityPhysic.MOVE_FORWARD);
		}
		if (key == GLFW.GLFW_KEY_S) {
			this._entity.disablePhysic(EntityPhysic.MOVE_BACKWARD);
		}
		if (key == GLFW.GLFW_KEY_D) {
			this._entity.disablePhysic(EntityPhysic.STRAFE_RIGHT);
		}
		if (key == GLFW.GLFW_KEY_A) {
			this._entity.disablePhysic(EntityPhysic.STRAFE_LEFT);
		}
	}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_W) {
			this._entity.enablePhysic(EntityPhysic.MOVE_FORWARD);
		}
		if (key == GLFW.GLFW_KEY_S) {
			this._entity.enablePhysic(EntityPhysic.MOVE_BACKWARD);
		}
		if (key == GLFW.GLFW_KEY_D) {
			this._entity.enablePhysic(EntityPhysic.STRAFE_RIGHT);
		}
		if (key == GLFW.GLFW_KEY_A) {
			this._entity.enablePhysic(EntityPhysic.STRAFE_LEFT);
		}
		if (key == GLFW.GLFW_KEY_SPACE && !this._entity.isJumping()) {
			this._entity.jump();
		}
		if (key == GLFW.GLFW_KEY_R) {
			Vector3f pos = new Vector3f();
			pos.set(this._entity.getPosition());
			pos.add(this._entity.getViewVector());
			pos.add(this._entity.getViewVector());
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
		super.setYaw(yaw + this.getEntity().getYaw());
	}
}
