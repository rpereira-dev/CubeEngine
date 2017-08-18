package com.grillecube.client.renderer.gui.components.parameters;

import com.grillecube.client.renderer.gui.components.GuiText;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * a parameter which recalculate the box dimensions automatically when changing
 * the text or the font size
 */
public class GuiTextParameterTextAlignBot extends GuiParameter<GuiText> {

	private final float offset;

	public GuiTextParameterTextAlignBot() {
		this(0.0f);
	}

	public GuiTextParameterTextAlignBot(float offset) {
		super();
		this.offset = offset;
	}

	@Override
	public void run(GuiText guiText) {
		FontModel fontModel = guiText.getFontModel();
		float y = this.offset;
		fontModel.setPosition(fontModel.getX(), y, 1.0f);
	}
}