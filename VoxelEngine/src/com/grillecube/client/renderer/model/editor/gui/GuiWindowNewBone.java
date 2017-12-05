
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
import com.grillecube.client.renderer.model.editor.mesher.EditableModel;
import com.grillecube.common.utils.Color;

public class GuiWindowNewBone extends GuiWindow {

	private final GuiLabel info;
	private final GuiPrompt name;
	private final GuiSpinnerEditor parent;
	private final GuiButton confirm;
	private final GuiButton cancel;

	public GuiWindowNewBone(EditableModel model) {
		super();

		super.setBox(0.3f, 0.3f, 0.4f, 0.4f, 0.0f);

		GuiParameter<GuiText> txtSize = new GuiTextParameterTextFillBox(0.75f);
		GuiParameter<GuiText> txtCenter = new GuiTextParameterTextCenterBox();

		float w = 0.2f;
		float h = w / 1.6f;

		this.info = new GuiLabel();
		this.info.setBox(0.0f, 0.8f, 1.0f, h, 0.0f);
		this.info.setFontColor(Color.WHITE);
		this.info.addTextParameter(txtSize);
		this.info.addTextParameter(txtCenter);
		this.info.setText("Please enter the name of the new bone and select it parent");
		this.addChild(info);

		this.name = new GuiPrompt();
		this.name.setHint("enter bone name");
		this.name.setBox(0.4f, 0.65f, w, h, 0.0f);
		this.name.setHeldTextColor(Color.WHITE);
		this.name.addTextParameter(txtSize);
		this.name.addTextParameter(txtCenter);
		this.addChild(this.name);

		this.parent = new GuiSpinnerEditor();
		this.parent.add(null);
		for (Bone bone : model.getSkeleton().getBones()) {
			this.parent.add(bone.getName());
		}
		this.parent.setBox(0.4f, 0.45f, w, h, 0.0f);
		this.parent.pick(0);
		this.addChild(this.parent);

		this.confirm = new GuiButton();
		this.confirm.setText("Confirm");
		this.confirm.addTextParameter(txtSize);
		this.confirm.addTextParameter(txtCenter);
		this.confirm.setBox(0.25f, 0.15f, w, h, 0.0f);
		this.addChild(this.confirm);
		this.confirm.addListener(new GuiListener<GuiEventPress<GuiButton>>() {
			@Override
			public void invoke(GuiEventPress<GuiButton> event) {
				Bone bone = new Bone(model.getSkeleton(), name.getHeldText());
				bone.setParent((String) parent.getPickedObject());
				model.getSkeleton().addBone(bone);
				close();
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
