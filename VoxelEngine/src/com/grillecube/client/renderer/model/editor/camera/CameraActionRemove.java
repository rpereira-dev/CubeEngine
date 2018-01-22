
package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.common.maths.Vector3i;

public class CameraActionRemove implements CameraAction {

	@Override
	public boolean action(CameraSelector cameraSelector) {
		boolean generate = false;
		Vector3i pos = new Vector3i();
		EditableModelLayer editableModelLayer = cameraSelector.getSelectedModelLayer();
		int x0 = cameraSelector.getX();
		int y0 = cameraSelector.getY();
		int z0 = cameraSelector.getZ();
		for (int dx = 0; dx < cameraSelector.getWidth(); dx++) {
			for (int dy = 0; dy < cameraSelector.getDepth(); dy++) {
				for (int dz = 0; dz < cameraSelector.getHeight(); dz++) {
					generate |= editableModelLayer.unsetBlockData(pos.set(x0 + dx, y0 + dy, z0 + dz));
				}
			}
		}
		return (generate);
	}

	@Override
	public void update() {

	}

}
