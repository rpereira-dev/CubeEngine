package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseRightPress<T extends Gui> extends GuiEventMouse<T> {

	public GuiEventMouseRightPress(T gui) {
		super(gui);
	}
}
