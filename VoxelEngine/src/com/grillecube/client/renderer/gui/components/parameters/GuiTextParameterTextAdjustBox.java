package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter which recalculate the box dimensions automatically when changing
 * the text or the font size
 */
public class GuiTextParameterTextAdjustBox extends GuiParameter<GuiText> {
	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();
		float x = (fontModel.getPosition().x + 1) * 0.5f;
		float y = (fontModel.getPosition().y + 1) * 0.5f;
		float width = guiText.getTextWidth();
		float height = guiText.getTextHeight();
		float rot = fontModel.getRotationZ();
		guiText.setBox(x, y, width, height, rot, false);
	}
}