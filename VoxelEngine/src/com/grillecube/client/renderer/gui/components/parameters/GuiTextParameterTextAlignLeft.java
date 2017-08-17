package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter which recalculate the box dimensions automatically when changing
 * the text or the font size
 */
public class GuiTextParameterTextAlignLeft extends GuiParameter<GuiText> {

	private final float offset;

	public GuiTextParameterTextAlignLeft() {
		this(0.0f);
	}

	public GuiTextParameterTextAlignLeft(float offset) {
		super();
		this.offset = offset;
	}

	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();
		fontModel.setPosition(-1.0f + this.offset, fontModel.getY(), 1.0f);
	}
}