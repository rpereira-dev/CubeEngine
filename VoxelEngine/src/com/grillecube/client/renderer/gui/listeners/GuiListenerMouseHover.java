package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.renderer.gui.components.Gui;

public interface GuiListenerMouseHover<T extends Gui> {
	public void invokeMouseHover(T gui, boolean leftPressed, double mousex, double mousey);

}
