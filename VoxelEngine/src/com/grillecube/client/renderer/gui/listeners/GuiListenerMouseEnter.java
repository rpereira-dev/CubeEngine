package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.renderer.gui.components.Gui;

public interface GuiListenerMouseEnter<T extends Gui> {
	public void invokeMouseEnter(T gui, double mousex, double mousey);
}
