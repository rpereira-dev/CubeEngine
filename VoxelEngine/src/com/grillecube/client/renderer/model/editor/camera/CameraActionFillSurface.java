
package com.grillecube.client.renderer.model.editor.camera;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.grillecube.client.renderer.model.ModelSkin;
import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;
import com.grillecube.common.faces.Face;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.utils.Color;

public class CameraActionFillSurface implements CameraAction {

	@Override
	public boolean action(CameraSelector cameraSelector) {

		EditableModelLayer editableModelLayer = cameraSelector.getSelectedModelLayer();
		Face face = cameraSelector.getFace();
		boolean generate = false;
		ModelSkin skin = cameraSelector.getSelectedSkin();
		Color color = cameraSelector.getSelectedColor();

		Queue<Vector3i> visitQueue = new LinkedList<Vector3i>();
		HashMap<Vector3i, Boolean> visited = new HashMap<Vector3i, Boolean>(128);

		visitQueue.add(cameraSelector.getBlock());
		visited.put(cameraSelector.getBlock(), true);

		while (!visitQueue.isEmpty()) {
			/** pop block */
			Vector3i block = visitQueue.poll();

			/** mark it as visited */
			/** color it */
			ModelBlockData blockData = editableModelLayer.getBlockData(block);
			if (blockData == null) {
				continue;
			}
			blockData.setColor(skin, color, face);
			generate = true;

			/** pour chaque voisin */
			for (Vector3i d : face.getNeighbors()) {
				Vector3i nextpos = new Vector3i(block.x + d.x, block.y + d.y, block.z + d.z);
				if (visited.containsKey(nextpos)) {
					continue;
				}
				visited.put(nextpos, true);
				visitQueue.add(nextpos);
			}

		}

		return (generate);

	}

	@Override
	public void update() {

	}

}
