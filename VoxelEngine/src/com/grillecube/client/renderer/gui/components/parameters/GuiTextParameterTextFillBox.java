package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter to make the text fill the Gui bounding box: it fills one of the
 * two axis (x or y), and then resize the other one
 */
public class GuiTextParameterTextFillBox extends GuiParameter<GuiText> {

	private final float correction;

	public GuiTextParameterTextFillBox(float correction) {
		super();
		this.correction = correction;
	}

	public GuiTextParameterTextFillBox() {
		this(1.0f);
	}

	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();

		if (fontModel.getTextWidth() == 0 || fontModel.getTextHeight() == 0) {
			return;
		}
		// System.out.println(guiText.getTotalAspectRatio());

		float scalex = 2.0f * guiText.getBoxWidth() / fontModel.getTextWidth();
		float scaley = 2.0f * guiText.getBoxHeight() / fontModel.getTextHeight();
		float scale = (scalex < scaley ? scalex : scaley) * this.correction;

		float rot = guiText.getBoxRotation();

		float x = 2.0f * guiText.getBoxX() - 1.0f;
		float y = 2.0f * guiText.getBoxY() - 1.0f;

		float rcx = 2.0f * guiText.getBoxCenterX() - 1.0f;
		float rcy = 2.0f * guiText.getBoxCenterY() - 1.0f;
		float rcz = 0;
		fontModel.set(x, y, 0.0f, scale, scale, 1.0f, 0.0f, 0.0f, rot, rcx, rcy, rcz);
	}
}