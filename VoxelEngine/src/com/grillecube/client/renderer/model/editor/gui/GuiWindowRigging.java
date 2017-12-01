package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.components.GuiWindow;
import com.grillecube.client.renderer.gui.components.parameters.GuiParameter;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.client.renderer.gui.event.GuiEventPress;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.client.renderer.model.editor.camera.CameraToolRigging;
import com.grillecube.client.renderer.model.editor.mesher.EditableModelLayer;
import com.grillecube.client.renderer.model.editor.mesher.ModelBlockData;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3i;
import com.grillecube.common.utils.Color;

public class GuiWindowRigging extends GuiWindow {

	private final GuiLabel info;
	private final GuiSpinnerEditor[] bones;
	private final GuiPrompt[] weight;
	private final GuiButton confirm;
	private final GuiButton cancel;

	public GuiWindowRigging(CameraToolRigging cameraToolRigging) {
		super();

		super.setBox(0.25f, 0.25f, 0.5f, 0.5f, 0.0f);

		GuiParameter<GuiText> txtSize = new GuiTextParameterTextFillBox(0.75f);
		GuiParameter<GuiText> txtCenter = new GuiTextParameterTextCenterBox();

		float w = 0.2f;
		float h = w / 1.6f;

		this.info = new GuiLabel();
		this.info.setBox(0.0f, 0.8f, 1.0f, h, 0.0f);
		this.info.setFontColor(Color.WHITE);
		this.info.addTextParameter(txtSize);
		this.info.addTextParameter(txtCenter);
		this.info.setText("Please select bones and weights for the selected blocks");
		this.addChild(info);

		this.bones = new GuiSpinnerEditor[3];
		this.weight = new GuiPrompt[3];
		for (int i = 0; i < 3; i++) {
			this.bones[i] = new GuiSpinnerEditor();
			this.bones[i].add(null);
			for (Bone bone : cameraToolRigging.getModel().getSkeleton().getBones()) {
				this.bones[i].add(bone.getName());
			}
			this.bones[i].setBox(0.35f, 0.65f - i * h, w, h, 0.0f);
			this.bones[i].pick(0);

			this.weight[i] = new GuiPrompt();
			this.weight[i].setHint("blocks bone-weights");
			this.weight[i].setHeldText("0.0");
			this.weight[i].setBox(0.55f, 0.65f - i * h, w, h, 0.0f);
			this.weight[i].setTextTestFormat(GuiPrompt.FORMAT_FLOAT);
			this.weight[i].setHeldTextColor(Color.WHITE);
			this.weight[i].addTextParameter(txtSize);
			this.weight[i].addTextParameter(txtCenter);

			this.addChild(this.bones[i]);
			this.addChild(this.weight[i]);
		}

		this.confirm = new GuiButton();
		this.confirm.setText("Confirm");
		this.confirm.addTextParameter(txtSize);
		this.confirm.addTextParameter(txtCenter);
		this.confirm.setBox(0.25f, 0.15f, w, h, 0.0f);
		this.addChild(this.confirm);
		final EditableModelLayer modelLayer = cameraToolRigging.getModelLayer();
		final int x0 = Maths.min(cameraToolRigging.getX(), modelLayer.getMinx());
		final int y0 = Maths.min(cameraToolRigging.getY(), modelLayer.getMiny());
		final int z0 = Maths.min(cameraToolRigging.getZ(), modelLayer.getMinz());
		final int width = Maths.min(cameraToolRigging.getWidth(), modelLayer.getMaxx() - modelLayer.getMinx());
		final int height = Maths.min(cameraToolRigging.getHeight(), modelLayer.getMaxy() - modelLayer.getMiny());
		final int depth = Maths.min(cameraToolRigging.getDepth(), modelLayer.getMaxz() - modelLayer.getMinz());
		this.confirm.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				this.setBoneWeight();
				close();
			}

			private final void setBoneWeight() {
				Vector3i tmp = new Vector3i();
				for (int dx = 0; dx <= width; dx++) {
					for (int dy = 0; dy <= height; dy++) {
						for (int dz = 0; dz <= depth; dz++) {
							int x = x0 + dx;
							int y = y0 + dy;
							int z = z0 + dz;
							ModelBlockData blockData = modelLayer.getBlockData(tmp.set(x, y, z));
							if (blockData != null) {
								int i;
								for (i = 0; i < bones.length; i++) {
									String boneName = (String) bones[i].getPickedObject();
									float w = weight[i].asFloat(0.0f);
									blockData.setBone(i, boneName, w);
								}
							}
						}
					}
				}
				modelLayer.requestPlanesUpdate();
			}
		});

		this.cancel = new GuiButton();
		this.cancel.setText("Decline");
		this.cancel.setBox(0.55f, 0.15f, w, h, 0.0f);
		this.cancel.addTextParameter(txtSize);
		this.cancel.addTextParameter(txtCenter);
		this.cancel.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				close();
			}
		});
		this.addChild(this.cancel);
	}
}
