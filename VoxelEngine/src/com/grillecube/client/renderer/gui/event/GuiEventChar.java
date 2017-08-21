package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventChar<T extends Gui> extends GuiEvent<T> {

	private final GLFWWindow glfwWindow;
	private final char character;

	public GuiEventChar(T gui, GLFWWindow glfwWindow, int codepoint) {
		super(gui);
		this.glfwWindow = glfwWindow;
		this.character = (char) codepoint;
	}

	public final GLFWWindow getGLFWWindow() {
		return (this.glfwWindow);
	}

	public final char getCharacter() {
		return (this.character);
	}
}
