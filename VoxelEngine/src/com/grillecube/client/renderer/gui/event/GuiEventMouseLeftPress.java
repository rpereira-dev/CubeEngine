package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseLeftPress<T extends Gui> extends GuiEventMouse<T> {

	public GuiEventMouseLeftPress(T gui) {
		super(gui);
	}
}
