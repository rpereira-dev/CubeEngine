package com.grillecube.editor.window.camera;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.client.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.client.sound.ALH;
import com.grillecube.client.sound.ALSound;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.resources.R;

public class CameraToolSet extends CameraToolPerBlock {
	public CameraToolSet() {
		super(new Vector4f(0.2f, 1.0f, 0.0f, 1.0f));
	}

	@Override
	protected ALSound getSound() {
		return (ALH.alhLoadSound(R.getResPath("sounds/pop.wav")));
	}

	@Override
	public int adjustXCoordinate(int x, Vector3f face) {
		return (int) (x + face.getX());
	}

	@Override
	public int adjustYCoordinate(int y, Vector3f face) {
		return (int) (y + face.getY());
	}

	@Override
	public int adjustZCoordinate(int z, Vector3f face) {
		return (int) (z + face.getZ());
	}

	@Override
	protected boolean applyToolOnBlock(Model model, ModelPartBuilder part, ModelPartSkinBuilder skin,
			CameraEditor camera, int x, int y, int z) {
		if (part.setBlock(x, y, z)) {
			skin.setColor(x, y, z, camera.getBuildingColor());
			return (true);
		}
		return (false);
	}
	
	@Override
	public String getName() {
		return ("Place blocks");
	}
}
