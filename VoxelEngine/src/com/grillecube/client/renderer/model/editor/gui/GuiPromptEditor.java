package com.grillecube.client.renderer.model.editor.gui;

import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiLabel;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.components.parameters.GuiTextParameterTextFillBox;

public class GuiPromptEditor extends Gui {

	private GuiLabel info;
	private GuiPrompt prompt;

	public GuiPromptEditor(String textInfo, String hintText) {
		super();

		this.info = new GuiLabel();
		this.info.setBox(0.0f, 0.0f, 0.25f, 1.0f, 0.0f);
		this.info.setHoverable(false);
		this.info.setFontColor(0, 0, 0, 1.0f);
		this.info.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.info.setText(textInfo);
		this.addChild(this.info);

		this.prompt = new GuiPrompt();
		this.prompt.setHint(hintText);
		this.prompt.setTextTestFormat(GuiPrompt.FORMAT_FLOAT);
		this.prompt.setBox(0.25f, 0.0f, 0.75f, 1.0f, 0.0f);
		this.prompt.addTextParameter(new GuiTextParameterTextFillBox(0.75f));
		this.addChild(this.prompt);
	}

	public final GuiPrompt getPrompt() {
		return (this.prompt);
	}
}
