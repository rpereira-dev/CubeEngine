package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;

public class ActionPlace extends Action {

	public ActionPlace(EditableModel editableModel, Selection selection) {
		super(editableModel, selection);
	}

	protected final void onActionDo() {
		EditableModel model = super.getEditableModel();
		Selection selection = super.getSelection();
		int x0 = selection.getX();
		int y0 = selection.getY();
		int z0 = selection.getZ();

		for (int dx = 0; dx < selection.getWidth(); dx++) {
			for (int dy = 0; dy < selection.getHeight(); dy++) {
				for (int dz = 0; dz < selection.getDepth(); dz++) {
					model.setBlockData(new ModelBlockData(x0 + dx, y0 + dy, z0 + dz));
				}
			}
		}
		model.generateMesh();
	}

}
