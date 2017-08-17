package com.grillecube.client.renderer.gui.listeners;

import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.renderer.gui.components.Gui;

public interface GuiListenerKeyPress<T extends Gui> {
	public void invokeKeyPress(T gui, GLFWWindow glfwWindow, int key, int scancode, int mods);
}
