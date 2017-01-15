package com.grillecube.engine.renderer.gui.animations;

import com.grillecube.engine.renderer.gui.components.GuiText;
import com.grillecube.engine.world.Timer;

/** a simple animation which auto rescale the gui on time and loop infinitely */
public class GuiAnimationTextAutoScale<T extends GuiText> extends GuiAnimation<T> {

	private float _scaleF;

	public GuiAnimationTextAutoScale() {
		this(0.001f);
	}

	public GuiAnimationTextAutoScale(float scale_factor) {
		this._scaleF = scale_factor;
	}

	@Override
	public boolean run(T gui, Timer timer) {

		float time = (float) timer.getTime();
		float sign = (time > 0.5f) ? 1.0f : -1.0f;
		float sizex = gui.getFontSize().x;
		float sizey = gui.getFontSize().y;

		gui.setFontSize(sizex + this._scaleF * sign, sizey + this._scaleF * sign);

		if (time > 1.0f) {
			this.restart(gui);
		}
		return (false);
	}

	@Override
	public void onRestart(T gui) {
	}
}
