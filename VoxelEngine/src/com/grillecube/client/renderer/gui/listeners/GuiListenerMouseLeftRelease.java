package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.renderer.gui.components.Gui;

public interface GuiListenerMouseLeftRelease<T extends Gui> {
	public void invokeMouseLeftRelease(T gui, double mousex, double mousey);
}
