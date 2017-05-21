package com.grillecube.client.renderer.gui;

public interface GuiListenerMouseLeftRelease<T extends Gui> {
	public void invokeMouseLeftRelease(T gui, double mousex, double mousey);
}
