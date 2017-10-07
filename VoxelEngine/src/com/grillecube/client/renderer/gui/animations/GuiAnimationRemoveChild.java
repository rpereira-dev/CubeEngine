package com.grillecube.client.renderer.gui.animations;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiAnimationRemoveChild<T extends Gui, V extends Gui> extends GuiAnimation<T> {

	private double duration;
	private V child;

	public GuiAnimationRemoveChild(V child) {
		this(child, 0.5d);
	}

	public GuiAnimationRemoveChild(V child, double duration) {
		super();
		this.child = child;
		this.duration = duration;
	}

	@Override
	public boolean run(T gui) {
		float ratio = (float) (this.getTimer().getTime() / this.duration);
		this.child.setTransparency(1.0f - ratio);
		return (ratio >= 1.0f);
	}

	@Override
	public void onStart(T gui) {
		this.child.setTransparency(1);
	}

	@Override
	public void onStop(T gui) {
		gui.removeChild(this.child);
		this.child.setTransparency(0);
	}

}
