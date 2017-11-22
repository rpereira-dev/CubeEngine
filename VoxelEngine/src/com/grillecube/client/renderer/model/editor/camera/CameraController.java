
package com.grillecube.client.renderer.model.editor.camera;

import java.util.Stack;

import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.model.editor.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.Logger;

public abstract class CameraController {

	private Stack<Action> actions;
	private Selection selection;
	private Class<? extends Action> actionClass;
	protected final GuiModelView guiModelView;
	protected boolean requestPanelsRefresh;

	public CameraController(GuiModelView guiModelView) {
		this.guiModelView = guiModelView;
		this.actions = new Stack<Action>();
	}

	public abstract String getName();

	public final void update() {
		this.setAction(this.guiModelView.getToolbox().getPickedAction());
		this.selection.update();
		this.onUpdate();
	}

	protected void onUpdate() {
	}

	public void onKeyPress(GuiEventKeyPress<GuiModelView> event) {
	}

	public void onLeftPressed() {
	}

	public void onLeftReleased() {
	}

	public void onMouseScroll(GuiEventMouseScroll<GuiModelView> event) {
	}

	public void onRightPressed() {
	}

	public void onRightReleased() {
	}

	public void onMouseMove() {
	}

	public final GuiModelView getGuiModelView() {
		return (this.guiModelView);
	}

	public final float getBlockSizeUnit() {
		return (this.getModel() == null ? 1.0f : this.getModel().getBlockSizeUnit());
	}

	public final ModelInstance getModelInstance() {
		return (this.guiModelView.getSelectedModelInstance());
	}

	public final EditableModel getModel() {
		return (this.guiModelView.getSelectedModel());
	}

	public final void requestPanelsRefresh() {
		this.requestPanelsRefresh = true;
	}

	public final boolean requestedPanelRefresh() {
		return (this.requestPanelsRefresh);
	}

	public final ModelEditorCamera getCamera() {
		return (this.guiModelView.getCamera());
	}

	public final void actionDo() {
		if (this.actionClass == null) {
			return;
		}
		try {
			Action action = this.actionClass.getConstructor(EditableModel.class, Selection.class)
					.newInstance(this.getModel(), this.selection);
			action.actionDo();
			this.actions.push(action);
		} catch (Exception e) {
			e.printStackTrace(Logger.get().getPrintStream());
		}
	}

	protected final void setAction(Class<? extends Action> action) {
		this.actionClass = action;
	}

	public final void setSelection(Selection selection) {
		this.selection = selection;
	}

	public final Selection getSelection() {
		return (this.selection);
	}

	protected Class<? extends Action> getAction() {
		return (this.actionClass);
	}
}
