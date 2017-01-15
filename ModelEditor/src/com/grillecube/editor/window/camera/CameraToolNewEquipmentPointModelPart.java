package com.grillecube.editor.window.camera;

import com.grillecube.engine.maths.Vector4f;
import com.grillecube.engine.renderer.model.Model;
import com.grillecube.engine.renderer.model.builder.ModelPartBuilder;
import com.grillecube.engine.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.engine.resources.R;
import com.grillecube.engine.sound.ALH;
import com.grillecube.engine.sound.ALSound;

public class CameraToolNewEquipmentPointModelPart extends CameraTool {
	public CameraToolNewEquipmentPointModelPart() {
		super(new Vector4f(0.5f, 0.5f, 1.0f, 1.0f));
	}

	@Override
	protected ALSound getSound() {
		return (ALH.alhLoadSound(R.getResPath("sounds/pop.wav")));
	}

	@Override
	public String getName() {
		return ("Add Equipment point (Model Part)");
	}

	@Override
	public Action newAction(Model model, ModelPartBuilder part, ModelPartSkinBuilder skin, CameraEditor camera) {
		// TODO Auto-generated method stub
		return null;
	}
}
