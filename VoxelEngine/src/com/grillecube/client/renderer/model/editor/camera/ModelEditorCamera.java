package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldCentered;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;

public class ModelEditorCamera extends CameraPerspectiveWorldCentered {

	private CameraTool[] tools;
	private int toolID;
	protected boolean requestPanelsRefresh;

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

	public final void requestPanelsRefresh(boolean v) {
		this.requestPanelsRefresh = v;
	}

	public void setTool(int toolID) {
		this.toolID = toolID;
	}

	public boolean requestedPanelRefresh() {
		return (this.requestPanelsRefresh);
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
	}

	public final void loadTools(GuiModelView guiModelView) {

		this.tools = new CameraTool[] { new CameraToolPlace(guiModelView), new CameraToolPaint(guiModelView),
				new CameraToolFill(guiModelView), new CameraToolRemove(guiModelView),
				new CameraToolExtrude(guiModelView) };
	}
}
