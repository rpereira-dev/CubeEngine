package com.grillecube.client.renderer.gui;

public interface GuiListenerMouseLeftPress<T extends Gui> {
	public void invokeMouseLeftPress(T gui, double mousex, double mousey);
}
