package com.grillecube.client.renderer.gui;

public interface GuiListenerMouseHover<T extends Gui> {
	public void invokeMouseHover(T gui, double mousex, double mousey);

}
