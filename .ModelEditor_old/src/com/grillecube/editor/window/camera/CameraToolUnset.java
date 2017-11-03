package com.grillecube.editor.window.camera;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.client.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.client.sound.ALH;
import com.grillecube.client.sound.ALSound;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.resources.R;

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
