package com.grillecube.editor.window.camera;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.builder.ModelPartBuilder;
import com.grillecube.client.renderer.model.builder.ModelPartSkinBuilder;
import com.grillecube.client.sound.ALH;
import com.grillecube.client.sound.ALSound;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.resources.R;

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
