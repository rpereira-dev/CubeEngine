package com.grillecube.client.renderer.gui.animations;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiAnimationAddChild<T extends Gui, V extends Gui> extends GuiAnimation<T> {

	private double duration;
	private V child;

	public GuiAnimationAddChild(V child) {
		this(child, 0.5d);
	}

	public GuiAnimationAddChild(V child, double duration) {
		super();
		this.child = child;
		this.duration = duration;
	}

	@Override
	public boolean run(T gui) {
		float ratio = (float) (this.getTimer().getTime() / this.duration);
		this.child.setTransparency(ratio);
		return (ratio >= 1.0f);
	}

	@Override
	public void onStart(T gui) {
		gui.addChild(this.child);
		this.child.setTransparency(0);
	}

	@Override
	public void onStop(T gui) {
		this.child.setTransparency(1);
	}

}
