package com.grillecube.editor.window.camera;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.client.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.client.sound.ALH;
import com.grillecube.client.sound.ALSound;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.resources.R;

public class CameraToolExtrude extends CameraToolPerBlock {
	public CameraToolExtrude() {
		super(new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
	}

	@Override
	protected ALSound getSound() {
		return (ALH.alhLoadSound(R.getResPath("sounds/pop.wav")));
	}

	@Override
	protected boolean applyToolOnBlock(Model model, ModelPartBuilder part, ModelPartSkinBuilder skin,
			CameraEditor camera, int beginx, int beginy, int beginz) {

		Vector3i face = new Vector3i(camera.getLookFace());

		boolean updated = false;
		int x, y, z;
		int dimx, dimy, dimz;
		int stepx, stepy, stepz;
		int endx, endy, endz;

		dimx = part.getBlockDimX();
		dimy = part.getBlockDimY();
		dimz = part.getBlockDimZ();

		stepx = -face.x;
		stepy = -face.y;
		stepz = -face.z;

		endx = (stepx == 0) ? beginx : (stepx < 0) ? -dimx : dimx;
		endy = (stepy == 0) ? beginy : (stepy < 0) ? -dimy : dimy;
		endz = (stepz == 0) ? beginz : (stepz < 0) ? -dimz : dimz;

		for (x = beginx; x != endx; x += stepx) {
			if (part.unsetBlock(x, beginy, beginz)) {
				updated = true;
			}
		}

		for (y = beginy; y != endy; y += stepy) {
			if (part.unsetBlock(beginx, y, beginz)) {
				updated = true;
			}
		}

		for (z = beginz; z != endz; z += stepz) {
			if (part.unsetBlock(beginx, beginy, z)) {
				updated = true;
			}
		}

		return (updated);
	}

	@Override
	public String getName() {
		return ("Extrude lines of block");
	}
}
