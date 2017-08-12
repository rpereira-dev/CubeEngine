package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter which recalculate the box dimensions automatically when changing
 * the text or the font size
 */
public class GuiTextParameterTextCenterYBox extends GuiParameter<GuiText> {
	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();
		float y = 2.0f * guiText.getBoxCenterY() - guiText.getTextHeight() - 1.0f;
		fontModel.setPosition(fontModel.getX(), y, 1.0f);
	}
}