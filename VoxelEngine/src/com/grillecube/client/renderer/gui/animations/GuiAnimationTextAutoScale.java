package com.grillecube.client.renderer.gui.animations;

import com.grillecube.client.renderer.gui.components.GuiText;

/** a simple animation which auto rescale the gui on time and loop infinitely */
@SuppressWarnings("unused")
public class GuiAnimationTextAutoScale<T extends GuiText> extends GuiAnimation<T> {

	private float scale;

	public GuiAnimationTextAutoScale() {
		this(0.001f);
	}

	public GuiAnimationTextAutoScale(float scale_factor) {
		this.scale = scale_factor;
	}

	@Override
	public boolean run(T gui) {

		float time = (float) this.getTimer().getTime();
		float sign = (time > 0.5f) ? 1.0f : -1.0f;
		float sizex = gui.getFontSize().x;
		float sizey = gui.getFontSize().y;

		gui.setFontSize(sizex + this.scale * sign, sizey + this.scale * sign);

		if (time > 1.0f) {
			this.restart(gui);
		}
		return (false);
	}

	@Override
	public void onStart(T gui) {
	}

	@Override
	public void onStop(T gui) {
		// TODO Auto-generated method stub

	}
}
