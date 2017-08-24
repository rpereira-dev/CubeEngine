package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventLooseFocus<T extends Gui> extends GuiEvent<T> {

	public GuiEventLooseFocus(T gui) {
		super(gui);
	}

}
