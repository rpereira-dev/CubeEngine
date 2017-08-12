package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter which recalculate the box dimensions automatically when changing
 * the text or the font size
 */
public class GuiTextParameterTextCenterXBox extends GuiParameter<GuiText> {
	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();
		float x = 2.0f * guiText.getBoxCenterX() - guiText.getTextWidth() - 1.0f;
		fontModel.setPosition(x, fontModel.getX(), 1.0f);
	}
}