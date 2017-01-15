package com.grillecube.engine.renderer.gui.animations;

import com.grillecube.engine.renderer.gui.Gui;
import com.grillecube.engine.world.Timer;

public abstract class GuiAnimation<T extends Gui> {
		
	private Timer _timer;
	
	public GuiAnimation() {
		this._timer = new Timer();
	}

	/**
	 * run the animation
	 * @param gui : the gui to animate
	 * @param timer : the timer (started when the animation started)
	 * @return true if the animation is ended, false else way
	 */
	public abstract boolean run(T gui, Timer timer);
	public abstract void onRestart(T gui);

	public boolean update(T gui) {
		this._timer.update();
		return (this.run(gui, this._timer));
	}

	public void restart(T gui) {
		this._timer.restart();
		this.onRestart(gui);
	}

}
