package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter which recalculate the box dimensions automatically when changing
 * the text or the font size
 */
public class GuiTextParameterTextCenterXBox extends GuiParameter<GuiText> {

	private final float offset;

	public GuiTextParameterTextCenterXBox() {
		this(0.0f);
	}

	public GuiTextParameterTextCenterXBox(float offset) {
		super();
		this.offset = offset;
	}

	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();
		float x = 2.0f * guiText.getBoxCenterX() - guiText.getTextWidth() - 1.0f + this.offset;
		fontModel.setPosition(x, fontModel.getY(), 1.0f);
	}
}