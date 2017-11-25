package com.grillecube.client.renderer.model.editor.camera;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldCentered;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolboxModel;

public class ModelEditorCamera extends CameraPerspectiveWorldCentered {

	private CameraTool[] tools;
	private int toolID;

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
		this.tools[this.toolID].update();
	}

	public void setTool(int toolID) {
		this.toolID = toolID;
	}

	public void onRightReleased() {
		if (this.tools[this.toolID] != null) {
			this.tools[this.toolID].onRightReleased();
		}
	}

	public void onMouseMove() {
		if (this.tools[this.toolID] != null) {
			this.tools[this.toolID].onMouseMove();
		}
	}

	public void onMouseScroll(GuiEventMouseScroll<GuiModelView> event) {
		if (this.tools[this.toolID] != null) {
			this.tools[this.toolID].onMouseScroll(event);
		}
	}

	public void onRightPressed() {
		if (this.tools[this.toolID] != null) {
			this.tools[this.toolID].onRightPressed();
		}
	}

	public void onLeftReleased() {
		if (this.tools[this.toolID] != null) {
			this.tools[this.toolID].onLeftReleased();
		}
	}

	public void onLeftPressed() {
		if (this.tools[this.toolID] != null) {
			this.tools[this.toolID].onLeftPressed();
		}
	}

	public void onKeyPress(GuiEventKeyPress<GuiModelView> event) {
		if (this.tools[this.toolID] != null) {
			this.tools[this.toolID].onKeyPress(event);
		}

		GuiToolboxModel model = event.getGui().getToolbox().getSelectedModelPanels();
		if (model != null) {
			if (event.getKey() == GLFW.GLFW_KEY_E) {
				model.getGuiToolboxModelPanelBuild().selectNextTool();
			} else if (event.getKey() == GLFW.GLFW_KEY_Q) {
				model.getGuiToolboxModelPanelBuild().selectPreviousTool();
			} else if (event.getKey() == GLFW.GLFW_KEY_D) {
				model.selectNextPanel();
			} else if (event.getKey() == GLFW.GLFW_KEY_A) {
				model.selectPreviousPanel();
			}
		}
	}

	public final void loadTools(GuiModelView guiModelView) {
		this.tools = new CameraTool[] { new CameraToolPlace(guiModelView), new CameraToolPaint(guiModelView),
				new CameraToolRemove(guiModelView), new CameraToolExtrude(guiModelView),
				new CameraToolRigging(guiModelView) };
	}

	public CameraTool getTool() {
		return (this.tools != null ? this.tools[this.toolID] : null);
	}
}
