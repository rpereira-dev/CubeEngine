package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseLeftRelease<T extends Gui> extends GuiEventMouse<T> {

	public GuiEventMouseLeftRelease(T gui) {
		super(gui);
	}
}
