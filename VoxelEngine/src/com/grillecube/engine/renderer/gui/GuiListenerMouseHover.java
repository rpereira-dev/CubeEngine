package com.grillecube.engine.renderer.gui;

public interface GuiListenerMouseHover<T extends Gui> {
	public void invokeMouseHover(T gui, double mousex, double mousey);

}
