package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventClick<T extends Gui> extends GuiEvent<T> {

	public GuiEventClick(T gui) {
		super(gui);
	}
}
