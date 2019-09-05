package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;
import com.grillecube.common.maths.Vector3i;

public class CameraActionPaint implements CameraAction {

	@Override
	public boolean action(CameraSelector cs) {

		EditableModelLayer layer = cs.getModelLayer();
		int x0 = cs.getX();
		int y0 = cs.getY();
		int z0 = cs.getZ();

		Vector3i pos = new Vector3i();
		boolean generate = false;
		for (int dx = 0; dx < cs.getWidth(); dx++) {
			for (int dy = 0; dy < cs.getDepth(); dy++) {
				for (int dz = 0; dz < cs.getHeight(); dz++) {
					ModelBlockData blockData = layer.getBlockData(pos.set(x0 + dx, y0 + dy, z0 + dz));
					if (blockData != null) {
						blockData.setColor(cs.getSelectedSkin(), cs.getSelectedColor(), cs.getFace());
						generate = true;
					}
				}
			}
		}
		return (generate);

	}

	@Override
	public void update() {
	}

}
