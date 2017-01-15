package com.grillecube.engine.renderer.gui.animations;

import com.grillecube.engine.Logger;
import com.grillecube.engine.renderer.gui.Gui;
import com.grillecube.engine.renderer.gui.GuiListenerMouseEnter;
import com.grillecube.engine.renderer.gui.GuiListenerMouseExit;
import com.grillecube.engine.renderer.gui.components.GuiText;
import com.grillecube.engine.world.Timer;

/** a simple animations which scale the text when hovered by the mouse */
public class GuiAnimationTextHoverScale<T extends GuiText> extends GuiAnimation<T>
		implements GuiListenerMouseEnter<T>, GuiListenerMouseExit<T> {

	private float _hover_scale;

	private float _scalex;
	private float _scaley;

	private float _r;
	private float _g;
	private float _b;
	private float _a;

	public GuiAnimationTextHoverScale(float scale) {
		this._hover_scale = scale;
	}

	@Override
	public boolean run(T gui, Timer timer) {
		return (false);
	}

	@Override
	public void invokeMouseExit(T gui, double mousex, double mousey) {
		gui.setFontSize(this._scalex, this._scaley);
		gui.setFontColor(this._r, this._g, this._b, this._a);
		gui.getFontModel().setOutlineColor(0.0f, 0.0f, 0.0f);
		gui.getFontModel().clearEffects();
	}

	@Override
	public void invokeMouseEnter(T gui, double mousex, double mousey) {
		gui.setFontSize(this._scalex * this._hover_scale, this._scaley * this._hover_scale);
		gui.setFontColor(this._r - 0.1f, this._g - 0.1f, this._b - 0.1f, this._a);
		gui.getFontModel().setBorderWidth(0.5f);
		gui.getFontModel().setBorderEdge(0.3f);
		gui.getFontModel().setOutlineColor(Gui.COLOR_WHITE.x, Gui.COLOR_WHITE.y, Gui.COLOR_WHITE.z);
	}

	@Override
	public void onRestart(T gui) {
		gui.removeListener((GuiListenerMouseEnter<T>) this);
		gui.removeListener((GuiListenerMouseExit<T>) this);
		gui.addListener((GuiListenerMouseEnter<T>) this);
		gui.addListener((GuiListenerMouseExit<T>) this);

		this._scalex = gui.getFontSize().x;
		this._scaley = gui.getFontSize().y;
		this._r = gui.getFontModel().getFontColor().x;
		this._g = gui.getFontModel().getFontColor().y;
		this._b = gui.getFontModel().getFontColor().z;
		this._a = gui.getFontModel().getFontColor().w;
	}
}
