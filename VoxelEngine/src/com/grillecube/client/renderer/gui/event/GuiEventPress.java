package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventPress<T extends Gui> extends GuiEvent<T> {

	public GuiEventPress(T gui) {
		super(gui);
	}
}
