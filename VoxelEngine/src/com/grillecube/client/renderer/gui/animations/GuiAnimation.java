package com.grillecube.client.renderer.gui.animations;

import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.common.Timer;

public abstract class GuiAnimation<T extends Gui> {

	private final Timer timer;

	public GuiAnimation() {
		this.timer = new Timer();
	}

	/**
	 * run the animation
	 * 
	 * @param gui
	 *            : the gui to animate
	 * @param timer
	 *            : the timer (started when the animation started)
	 * @return true if the animation is ended, false else way
	 */
	public abstract boolean run(T gui);

	public abstract void onStart(T gui);

	public abstract void onStop(T gui);

	public final boolean update(T gui) {
		this.timer.update();
		return (this.run(gui));
	}

	public final void restart(T gui) {
		this.timer.restart();
	}

	public final Timer getTimer() {
		return (this.timer);
	}

}
