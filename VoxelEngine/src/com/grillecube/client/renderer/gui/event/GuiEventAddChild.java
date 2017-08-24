package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventAddChild<T extends Gui, U extends Gui> extends GuiEvent<T> {

	private final U child;

	public GuiEventAddChild(T gui, U child) {
		super(gui);
		this.child = child;
	}

	public final U getChild() {
		return (this.child);
	}
}
