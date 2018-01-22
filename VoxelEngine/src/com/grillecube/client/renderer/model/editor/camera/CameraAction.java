package com.grillecube.client.renderer.model.editor.camera;

public interface CameraAction {
	/**
	 * do the action of the tool
	 * 
	 * @param modelInstance
	 * @return : true if the model has been modified (the model mesh will be
	 *         re-generated)
	 */
	public boolean action(CameraSelector cameraSelector);

	public void update();
}
