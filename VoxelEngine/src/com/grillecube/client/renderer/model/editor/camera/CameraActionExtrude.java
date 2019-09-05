
package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;

public class CameraActionExtrude implements CameraAction {

	@Override
	public boolean action(CameraSelector cameraSelector) {

		boolean updated = false;
		Face face = cameraSelector.getFace();
		EditableModelLayer modelLayer = cameraSelector.getModelLayer();

		int stepx = -face.getVector().x;
		int stepy = -face.getVector().y;
		int stepz = -face.getVector().z;

		int x1 = 0;
		if (stepx < 0) {
			x1 = modelLayer.getMinx() - 1;
		} else if (stepx > 0) {
			x1 = modelLayer.getMaxx() + 1;
		}

		int y1 = 0;
		if (stepy < 0) {
			y1 = modelLayer.getMiny() - 1;
		} else if (stepy > 0) {
			y1 = modelLayer.getMaxy() + 1;
		}

		int z1 = 0;
		if (stepz < 0) {
			z1 = modelLayer.getMinz() - 1;
		} else if (stepz > 0) {
			z1 = modelLayer.getMaxz() + 1;
		}

		Vector3i pos = new Vector3i();

		if (stepx != 0) {
			for (int dy = 0; dy < cameraSelector.getDepth(); dy++) {
				for (int dz = 0; dz < cameraSelector.getHeight(); dz++) {
					int x0 = cameraSelector.getX();
					int y0 = cameraSelector.getY() + dy;
					int z0 = cameraSelector.getZ() + dz;
					int x;
					for (x = x0; x != x1; x += stepx) {
						if (modelLayer.unsetBlockData(pos.set(x, y0, z0))) {
							updated = true;
						}
					}
				}
			}
		}

		if (stepy != 0) {
			for (int dx = 0; dx < cameraSelector.getWidth(); dx++) {
				for (int dz = 0; dz < cameraSelector.getHeight(); dz++) {
					int x0 = cameraSelector.getX() + dx;
					int y0 = cameraSelector.getY();
					int z0 = cameraSelector.getZ() + dz;
					int y;
					for (y = y0; y != y1; y += stepy) {
						if (modelLayer.unsetBlockData(pos.set(x0, y, z0))) {
							updated = true;
						}
					}
				}
			}
		}

		if (stepz != 0) {
			for (int dx = 0; dx < cameraSelector.getWidth(); dx++) {
				for (int dy = 0; dy < cameraSelector.getDepth(); dy++) {
					int x0 = cameraSelector.getX() + dx;
					int y0 = cameraSelector.getY() + dy;
					int z0 = cameraSelector.getZ();
					int z;
					for (z = z0; z != z1; z += stepz) {
						if (modelLayer.unsetBlockData(pos.set(x0, y0, z))) {
							updated = true;
						}
					}
				}
			}
		}

		return (updated);

	}

	@Override
	public void update() {

	}

}
