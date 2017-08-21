package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseExit<T extends Gui> extends GuiEventMouse<T> {

	public GuiEventMouseExit(T gui) {
		super(gui);
	}
}
