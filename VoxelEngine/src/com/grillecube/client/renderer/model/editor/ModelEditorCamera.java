package com.grillecube.client.renderer.model.editor;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.GLFWListenerMousePress;
import com.grillecube.client.opengl.GLFWListenerMouseRelease;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldCentered;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;

public class ModelEditorCamera extends CameraPerspectiveWorldCentered
		implements GLFWListenerMousePress, GLFWListenerMouseRelease {

	private final Vector3i blockOne;
	private final Vector3i blockTwo;

	public ModelEditorCamera(GLFWWindow window) {
		super(window);
		this.getWindow().addMousePressListener(this);
		this.getWindow().addMouseReleaseListener(this);

		super.setPosition(0, 8, 0);
		super.setPositionVelocity(0, 0, 0);
		super.setRotationVelocity(0, 0, 0);
		super.setPitch(0);
		super.setYaw((float) Math.PI);
		super.setRoll(0);
		super.setSpeed(0.2f);
		super.setRotSpeed(1);
		super.setFarDistance(Float.MAX_VALUE);
		super.setRenderDistance(Float.MAX_VALUE);
		super.setDistanceFromCenter(16);
		super.setAngleAroundCenter(0);

		this.blockOne = new Vector3i();
		this.blockTwo = new Vector3i();
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
		this.getWindow().setCursor(false);
		this.setCenter(this.getLookCoords());
		this.setDistanceFromCenter((float) Vector3f.distance(this.getCenter(), this.getPosition()));
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
