package com.grillecube.client.renderer.gui.animations;

import com.grillecube.client.renderer.gui.Gui;
import com.grillecube.common.world.Timer;

/** a simple animation which translate the gui horizontaly depending on parameter speed */
public class GuiAnimationSlideHorizontal<T extends Gui> extends GuiAnimation<T> {

	private float _speed;

	public GuiAnimationSlideHorizontal(float speed) {
		this._speed = speed;
	}
	
	@Override
	public boolean run(T gui, Timer timer) {
		
		gui.setPosition(gui.getX() + this._speed, gui.getY());
		return (gui.getX() > 1.0f || gui.getX() < -1.0f);
	}

	@Override
	public void onRestart(T gui) {
		
	}


}
