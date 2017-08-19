package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.renderer.gui.components.Gui;

public interface GuiListenerChar<T extends Gui> {
	public void invokeCharPress(T gui, GLFWWindow window, int codepoint);
}
