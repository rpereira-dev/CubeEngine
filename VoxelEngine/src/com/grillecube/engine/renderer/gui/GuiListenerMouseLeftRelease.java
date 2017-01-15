package com.grillecube.engine.renderer.gui;

public interface GuiListenerMouseLeftRelease<T extends Gui> {
	public void invokeMouseLeftRelease(T gui, double mousex, double mousey);
}
