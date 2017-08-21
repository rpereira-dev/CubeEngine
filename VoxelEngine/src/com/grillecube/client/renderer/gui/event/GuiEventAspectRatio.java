package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

/**
 * a listener called when the Gui box width/height changed, and so it aspect
 * ratio changes too
 */
public class GuiEventAspectRatio<T extends Gui> extends GuiEvent<T> {

	private final float oldAspectRatio;
	private final float newAspectRatio;

	public GuiEventAspectRatio(T gui, float oldAspectRatio, float newAspectRatio) {
		super(gui);
		this.oldAspectRatio = oldAspectRatio;
		this.newAspectRatio = newAspectRatio;
	}

	public final float getOldAspectRatio() {
		return (this.oldAspectRatio);
	}

	public final float getNewAspectRatio() {
		return (this.newAspectRatio);
	}

}
