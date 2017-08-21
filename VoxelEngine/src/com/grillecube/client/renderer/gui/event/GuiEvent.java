package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public abstract class GuiEvent<T extends Gui> {

	private final T gui;

	public GuiEvent(T gui) {
		this.gui = gui;
	}

	public final T getGui() {
		return (this.gui);
	}
}
