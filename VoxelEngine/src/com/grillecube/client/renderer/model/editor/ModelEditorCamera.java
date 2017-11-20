package com.grillecube.client.renderer.model.editor;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldCentered;

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
}
