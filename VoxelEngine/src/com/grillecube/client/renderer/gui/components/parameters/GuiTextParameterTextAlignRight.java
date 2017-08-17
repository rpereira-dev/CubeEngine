package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter which recalculate the box dimensions automatically when changing
 * the text or the font size
 */
public class GuiTextParameterTextAlignRight extends GuiParameter<GuiText> {

	private final float offset;

	public GuiTextParameterTextAlignRight() {
		this(0.0f);
	}

	public GuiTextParameterTextAlignRight(float offset) {
		super();
		this.offset = offset;
	}

	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();
		float x = 1.0f - fontModel.getTextWidth() * fontModel.getScaleX() + this.offset;
		fontModel.setPosition(x, fontModel.getY(), 1.0f);
	}
}