package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter which recalculate the box dimensions automatically when changing
 * the text or the font size
 */
public class GuiTextParameterTextAlignTop extends GuiParameter<GuiText> {

	private final float offset;

	public GuiTextParameterTextAlignTop() {
		this(0.0f);
	}

	public GuiTextParameterTextAlignTop(float offset) {
		super();
		this.offset = offset;
	}

	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();
		float y = 1.0f - fontModel.getTextHeight() * fontModel.getScaleY() - this.offset;
		fontModel.setPosition(fontModel.getX(), y, 1.0f);
	}
}