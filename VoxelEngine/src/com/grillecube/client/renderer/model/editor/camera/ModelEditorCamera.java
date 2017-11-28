package com.grillecube.client.renderer.model.editor.camera;

import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorldCentered;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.gui.toolbox.GuiToolboxModel;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.maths.Vector3i;

public class ModelEditorCamera extends CameraPerspectiveWorldCentered {

	private CameraTool[] tools;
	private int toolID;

	/** maximum number of step that can be canceled */
	private static final int HISTORIC_MAX_DEPTH = 16;
	private LinkedList<HashMap<Vector3i, ModelBlockData>> oldBlocksData;

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
		this.oldBlocksData = new LinkedList<HashMap<Vector3i, ModelBlockData>>();
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
		ModelInstance modelInstance = event.getGui().getSelectedModelInstance();
		if (modelInstance == null) {
			return ;
		}
		EditableModel model = ((EditableModel) modelInstance.getModel());

		if (this.tools[this.toolID] != null) {
			if (modelInstance != null && event.getKey() == GLFW.GLFW_KEY_Z) {
				// do a deep copy of the current model block data
				HashMap<Vector3i, ModelBlockData> data = model.getCopyRawBlockDatas();
				if (this.tools[this.toolID].action(modelInstance)) {
					// generate mesh, save
					while (this.oldBlocksData.size() >= HISTORIC_MAX_DEPTH) {
						this.oldBlocksData.remove(0);
					}
					this.oldBlocksData.add(data);
					model.generate();
					event.getGui().getToolbox().getSelectedModelPanels().getGuiToolboxModelPanelSkin().refresh();
				}
			}
		}

		GuiToolboxModel modelPanel = event.getGui().getToolbox().getSelectedModelPanels();
		if (modelPanel != null) {
			if (event.getKey() == GLFW.GLFW_KEY_E) {
				modelPanel.getGuiToolboxModelPanelBuild().selectNextTool();
			} else if (event.getKey() == GLFW.GLFW_KEY_Q) {
				modelPanel.getGuiToolboxModelPanelBuild().selectPreviousTool();
			} else if (event.getKey() == GLFW.GLFW_KEY_D) {
				modelPanel.selectNextPanel();
			} else if (event.getKey() == GLFW.GLFW_KEY_A) {
				modelPanel.selectPreviousPanel();
			} else if (event.getKey() == GLFW.GLFW_KEY_W
					&& event.getGLFWWindow().isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)) {
				if (this.oldBlocksData.size() > 0) {
					HashMap<Vector3i, ModelBlockData> data = this.oldBlocksData.remove(this.oldBlocksData.size() - 1);
					model.setRawBlockDatas(data);
					model.generate();
					event.getGui().getToolbox().getSelectedModelPanels().getGuiToolboxModelPanelSkin().refresh();
				} else {
					GuiRenderer guiRenderer = event.getGui().getWorldRenderer().getMainRenderer().getGuiRenderer();
					guiRenderer.toast(event.getGui(), "Nothing to be canceled", false);
				}
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
