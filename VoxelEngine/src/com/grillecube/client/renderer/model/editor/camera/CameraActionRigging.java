
package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.model.editor.gui.GuiWindowRigging;

public class CameraActionRigging implements CameraAction {

	@Override
	public boolean action(CameraSelector cameraSelector) {
		GuiWindowRigging gui = new GuiWindowRigging(cameraSelector);
		gui.open(cameraSelector.getGuiModelView());
		return (false);

	}

	@Override
	public void update() {

	}

}
