package com.grillecube.editor.window.camera;

import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.engine.resources.R;
import com.grillecube.engine.sound.ALH;
import com.grillecube.engine.sound.ALSound;

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
