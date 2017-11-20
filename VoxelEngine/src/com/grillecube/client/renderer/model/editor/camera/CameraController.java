
package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.camera.CameraPicker;
import com.grillecube.client.renderer.camera.Raycasting;
import com.grillecube.client.renderer.camera.RaycastingCallback;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.model.EditableModel;
import com.grillecube.client.renderer.model.editor.ModelEditorCamera;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.entity.Entity;

public abstract class CameraController {

	protected final GuiModelView guiModelView;
	protected boolean requestPanelsRefresh;

	public CameraController(GuiModelView guiModelView) {
		this.guiModelView = guiModelView;
	}

	public final void update() {
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
}
