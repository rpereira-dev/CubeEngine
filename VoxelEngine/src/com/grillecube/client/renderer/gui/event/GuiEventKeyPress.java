package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventKeyPress<T extends Gui> extends GuiEvent<T> {

	private GLFWWindow glfwWindow;
	private int key;
	private int scancode;
	private int mods;

	public GuiEventKeyPress(T gui, GLFWWindow glfwWindow, int key, int scancode, int mods) {
		super(gui);
		this.glfwWindow = glfwWindow;
		this.key = key;
		this.scancode = scancode;
		this.mods = mods;
	}

	public final GLFWWindow getGLFWWindow() {
		return (this.glfwWindow);
	}

	public final int getKey() {
		return (this.key);
	}

	public final int getScancode() {
		return (this.scancode);
	}

	public final int getMods() {
		return (this.mods);
	}
}
