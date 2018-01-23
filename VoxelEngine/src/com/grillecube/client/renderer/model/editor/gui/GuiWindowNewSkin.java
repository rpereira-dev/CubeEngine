
package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.GuiPopUp;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.components.parameters.GuiParameter;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextCenterBox;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;
import com.grillecube.common.utils.Color;

public class GuiWindowNewSkin extends GuiPopUp {

	public final GuiPrompt name;

	public GuiWindowNewSkin(GuiPopUpCallback<GuiWindowNewSkin> callback) {
		super(callback);

		super.setBox(0.3f, 0.3f, 0.4f, 0.4f, 0.0f);

		GuiParameter<GuiText> txtSize = new GuiTextParameterTextFillBox(0.75f);
		GuiParameter<GuiText> txtCenter = new GuiTextParameterTextCenterBox();

		float w = 0.2f;
		float h = w / 1.6f;

		super.getInfoText().setText("Please enter the name of the new skin");

		this.name = new GuiPrompt();
		this.name.setHint("enter skin name");
		this.name.setBox(0.4f, 0.65f, w, h, 0.0f);
		this.name.setHeldTextColor(Color.WHITE);
		this.name.addTextParameter(txtSize);
		this.name.addTextParameter(txtCenter);
		this.addChild(this.name);
	}
}
