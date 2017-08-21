package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseHover<T extends Gui> extends GuiEventMouse<T> {

	public GuiEventMouseHover(T gui) {
		super(gui);
	}
}
