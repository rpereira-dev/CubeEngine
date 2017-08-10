package com.grillecube.client.renderer.gui.components;

//TODO
public class GuiButtonTextured extends GuiButton {

	private static final float UV_UNIT = 1 / 3.0f;

	private GuiTexture bgGuiTexture;

	public GuiButtonTextured() {
		super();
		this.bgGuiTexture = new GuiTexture();
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		super.onUpdate(x, y, pressed);

		float uvymin;
		if (super.isLeftPressed()) {
			uvymin = 0;
		} else if (super.isHovered() || super.hasFocus()) {
			uvymin = 1 / 3.0f;
		} else {
			uvymin = 2 / 3.0f;
		}

		// TODO
		// super.setBackgroundTexture(super.getBackgroundTexture(), 0.0f,
		// uvymin, 1.0f, uvymin + UV_UNIT);
	}
}
