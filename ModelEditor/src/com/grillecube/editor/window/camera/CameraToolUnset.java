package com.grillecube.editor.window.camera;

import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.engine.resources.R;
import com.grillecube.engine.sound.ALH;
import com.grillecube.engine.sound.ALSound;

public class CameraToolUnset extends CameraToolPerBlock {
	public CameraToolUnset() {
		super(new Vector4f(1.0f, 0.1f, 0.1f, 1.0f));
	}

	@Override
	protected ALSound getSound() {
		return (ALH.alhLoadSound(R.getResPath("sounds/pop.wav")));
	}

	@Override
	protected boolean applyToolOnBlock(Model model, ModelPartBuilder part, ModelPartSkinBuilder skin,
			CameraEditor camera, int x, int y, int z) {
		if (part.unsetBlock(x, y, z)) {
			skin.setColor(x, y, z, ModelPartSkinBuilder.COLOR_UNSET);
			return (true);
		}
		return (false);
	}

	@Override
	public String getName() {
		return ("Remove blocks");
	}
}
