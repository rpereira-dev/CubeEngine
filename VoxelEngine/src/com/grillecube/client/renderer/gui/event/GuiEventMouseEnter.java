package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseEnter<T extends Gui> extends GuiEventMouse<T> {

	public GuiEventMouseEnter(T gui) {
		super(gui);
	}

}
