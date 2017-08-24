package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventUnpress<T extends Gui> extends GuiEvent<T> {

	public GuiEventUnpress(T gui) {
		super(gui);
	}
}
