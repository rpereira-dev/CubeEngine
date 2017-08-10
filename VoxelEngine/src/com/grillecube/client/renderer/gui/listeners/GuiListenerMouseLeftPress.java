package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.renderer.gui.components.Gui;

public interface GuiListenerMouseLeftPress<T extends Gui> {
	public void invokeMouseLeftPress(T gui, double mousex, double mousey);
}
