package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouseScroll<T extends Gui> extends GuiEventMouse<T> {

	private final double xScroll;
	private final double yScroll;

	public GuiEventMouseScroll(T gui, double xScroll, double yScroll) {
		super(gui);
		this.xScroll = xScroll;
		this.yScroll = yScroll;
	}

	public final double getScrollX() {
		return (this.xScroll);
	}

	public final double getScrollY() {
		return (this.yScroll);
	}
}
