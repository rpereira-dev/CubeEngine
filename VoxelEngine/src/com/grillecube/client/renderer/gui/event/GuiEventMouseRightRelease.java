package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseRightRelease<T extends Gui> extends GuiEventMouse<T> {

	public GuiEventMouseRightRelease(T gui) {
		super(gui);
	}
}
