package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseMove<T extends Gui> extends GuiEventMouse<T> {

	public GuiEventMouseMove(T gui) {
		super(gui);
	}
}
