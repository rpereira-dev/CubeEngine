package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.renderer.gui.components.Gui;

public interface GuiListenerMouseExit<T extends Gui> {
	public void invokeMouseExit(T gui, double mousex, double mousey);
}
