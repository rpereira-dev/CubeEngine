package com.grillecube.client.renderer.gui.animations;

import com.grillecube.client.renderer.gui.Gui;
import com.grillecube.common.world.Timer;

/** a simple animation which translate the gui vertically depending on parameter speed */
public class GuiAnimationSlideVertical<T extends Gui> extends GuiAnimation<T> {

	private float _speed;

	public GuiAnimationSlideVertical(float speed) {
		this._speed = speed;
	}
	
	@Override
	public boolean run(T gui, Timer timer) {
		
		gui.setPosition(gui.getX(), gui.getY()  + this._speed);
		return (gui.getY() > 1.0f || gui.getY() < -1.0f);
	}

	@Override
	public void onRestart(T gui) {
		
	}


}
