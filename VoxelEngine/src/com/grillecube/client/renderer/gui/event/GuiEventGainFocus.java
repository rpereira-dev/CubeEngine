package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventGainFocus<T extends Gui> extends GuiEvent<T> {

	public GuiEventGainFocus(T gui) {
		super(gui);
	}

}
