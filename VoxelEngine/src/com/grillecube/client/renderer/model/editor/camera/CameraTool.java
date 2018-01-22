
package com.grillecube.client.renderer.model.editor.camera;

public class CameraTool {

	protected final CameraAction cameraAction;
	protected final CameraSelector cameraSelector;

	public CameraTool(CameraAction cameraAction, CameraSelector cameraSelector) {
		this.cameraAction = cameraAction;
		this.cameraSelector = cameraSelector;
	}

	public final CameraAction getCameraAction() {
		return (this.cameraAction);
	}

	public final CameraSelector getCameraSelector() {
		return (this.cameraSelector);
	}

	public final void update() {
		this.onUpdate();
		this.getCameraSelector().update();
		this.getCameraAction().update();
	}

	protected void onUpdate() {
	}
}
