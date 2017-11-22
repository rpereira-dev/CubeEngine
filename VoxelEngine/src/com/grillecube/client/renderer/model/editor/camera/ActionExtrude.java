package com.grillecube.client.renderer.model.editor.camera;

import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.common.maths.Vector3i;

public class ActionExtrude extends Action {

	public ActionExtrude(EditableModel editableModel, Selection selection) {
		super(editableModel, selection);
	}

	protected final void onActionDo() {
		EditableModel model = super.getEditableModel();
		Selection selection = super.getSelection();
		Vector3i face = selection.getFace();

		boolean updated = false;

		int stepx = -face.x;
		int stepy = -face.y;
		int stepz = -face.z;

		int x1 = 0;
		if (stepx < 0) {
			x1 = model.getMinx() - 1;
		} else if (stepx > 0) {
			x1 = model.getMaxx() + 1;
		}

		int y1 = 0;
		if (stepy < 0) {
			y1 = model.getMiny() - 1;
		} else if (stepy > 0) {
			y1 = model.getMaxy() + 1;
		}

		int z1 = 0;
		if (stepz < 0) {
			z1 = model.getMinz() - 1;
		} else if (stepz > 0) {
			z1 = model.getMaxz() + 1;
		}

		Vector3i pos = new Vector3i();

		if (stepx != 0) {
			for (int dy = 0; dy < selection.getHeight(); dy++) {
				for (int dz = 0; dz < selection.getDepth(); dz++) {
					int x0 = selection.getX();
					int y0 = selection.getY() + dy;
					int z0 = selection.getZ() + dz;
					int x;
					for (x = x0; x != x1; x += stepx) {
						if (model.unsetBlockData(pos.set(x, y0, z0))) {
							updated = true;
						}
					}
				}
			}
		}

		if (stepy != 0) {
			for (int dx = 0; dx < selection.getWidth(); dx++) {
				for (int dz = 0; dz < selection.getDepth(); dz++) {
					int x0 = selection.getX() + dx;
					int y0 = selection.getY();
					int z0 = selection.getZ() + dz;
					int y;
					for (y = y0; y != y1; y += stepy) {
						if (model.unsetBlockData(pos.set(x0, y, z0))) {
							updated = true;
						}
					}
				}
			}
		}

		if (stepz != 0) {
			for (int dx = 0; dx < selection.getWidth(); dx++) {
				for (int dy = 0; dy < selection.getHeight(); dy++) {
					int x0 = selection.getX() + dx;
					int y0 = selection.getY() + dy;
					int z0 = selection.getZ();
					int z;
					for (z = z0; z != z1; z += stepz) {
						if (model.unsetBlockData(pos.set(x0, y0, z))) {
							updated = true;
						}
					}
				}
			}
		}

		if (updated) {
			model.generateMesh();
		}
	}
}
