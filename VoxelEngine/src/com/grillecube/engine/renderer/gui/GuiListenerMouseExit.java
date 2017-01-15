package com.grillecube.engine.renderer.gui;

public interface GuiListenerMouseExit<T extends Gui> {
	public void invokeMouseExit(T gui, double mousex, double mousey);
}
