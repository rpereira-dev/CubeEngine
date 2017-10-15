package com.grillecube.client.renderer.model.editor;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldCentered;
import com.grillecube.common.maths.Vector3f;

public class ModelEditorCamera extends CameraPerspectiveWorldCentered {

	public ModelEditorCamera(GLFWWindow window) {
		super(window);

		super.setPosition(0, 16, 0);
		super.setPositionVelocity(0, 0, 0);
		super.setRotationVelocity(0, 0, 0);
		super.setPitch(0);
		super.setYaw(0);
		super.setRoll(0);
		super.setSpeed(0.2f);
		super.setRotSpeed(1);
		super.setFarDistance(Float.MAX_VALUE);
		super.setRenderDistance(Float.MAX_VALUE);
		super.setDistanceFromCenter(16);
		super.setAngleAroundCenter(-45);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void invokeCursorPos(GLFWWindow window, double xpos, double ypos) {
		// rotate
		if (window.isMouseRightPressed()) {
			float pitch = (float) ((window.getMouseDY()) * 0.1f);
			this.increasePitch(pitch);

			float angle = (float) ((window.getMouseDX()) * 0.3f);
			this.increaseAngleAroundCenter(-angle);
		}
	}

	@Override
	public void invokeMouseRelease(GLFWWindow window, int button, int mods) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			this.getWindow().setCursor(true);
			this.getWindow().setCursorCenter();
		}
	}

	@Override
	public void invokeMousePress(GLFWWindow window, int button, int mods) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			this.getWindow().setCursor(false);
			this.setCenter(this.getLookCoords());
			this.setDistanceFromCenter((float) Vector3f.distance(this.getCenter(), this.getPosition()));
		}
	}

	@Override
	public void invokeMouseScroll(GLFWWindow window, double xpos, double ypos) {
		if (this.getWindow().isMouseRightPressed()) {
			float speed = this.getDistanceFromCenter() * 0.14f;
			super.increaseDistanceFromCenter((float) (-ypos * speed));
		} else if (this.getWindow().isMouseLeftPressed()) {
			if (ypos < 0) {
				// this._y_selected += 1;
				// this._block_two.y += 1;
			} else {
				// this._y_selected -= 1;
				// this._block_two.y -= 1;
			}
		} else {
			float scrollspeed = 4.0f;
			super.increaseDistanceFromCenter((float) (-ypos * scrollspeed));
		}
	}
}
