package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiParameter;
import com.grillecube.client.renderer.gui.font.FontModel;
import com.grillecube.common.maths.Maths;

public abstract class GuiButton extends GuiLabel {

	/** a parameter which tell the Gui Button text to fill the rectangle */
	public static final GuiParameter<GuiButton> PARAM_TEXT_FILL_BUTTON = new GuiParameter<GuiButton>() {

		@Override
		public void run(GuiButton gui) {

			FontModel model = gui.getFontModel();

			float scalex = gui.getWidth() / (model.getTextWidth() * 1.2f);
			float scaley = gui.getHeight() / (model.getTextHeight() * 1.2f);
			float scale = Maths.min(scalex, scaley);
			model.setScale(scale, scale, 0);
		}
	};

	/**
	 * a parameter which tell the Gui Button text to be center toward the button
	 * quad
	 */
	public static final GuiParameter<GuiButton> PARAM_TEXT_CENTER_BUTTON = new GuiParameter<GuiButton>() {

		@Override
		public void run(GuiButton gui) {
			FontModel model = gui.getFontModel();

			float width = model.getTextWidth() * model.getScale().x;
			float height = model.getTextHeight() * model.getScale().y;
			model.setPosition(gui.getCenterX() - width * 0.5f, gui.getCenterY() + height * 0.5f, 0);
		}
	};

	public GuiButton() {
		super();
	}
}
