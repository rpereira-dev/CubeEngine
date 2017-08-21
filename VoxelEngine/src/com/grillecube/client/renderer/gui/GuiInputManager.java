package com.grillecube.client.renderer.gui;

import com.grillecube.client.opengl.GLFWListenerChar;
import com.grillecube.client.opengl.GLFWListenerKeyPress;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.event.GuiEventChar;

/** catch inputs and send them back to a gui */
public class GuiInputManager {

	/** the window on which input are catched */
	private GLFWWindow glfwWindow;
	private Gui mainGui;
	private GLFWListenerKeyPress keyListener;
	private GLFWListenerChar charListener;

	/** add listeners to the window */
	public final void initialize(GLFWWindow glfwWindow, Gui pgui) {

		this.glfwWindow = glfwWindow;
		this.mainGui = pgui;

		this.keyListener = new GLFWListenerKeyPress() {
			@Override
			public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
				// TODO stack event on each focused guis
				// mainGui.stackEvent(new GuiEventKeyPress<Gui>(mainGui,
				// glfwWindow, key, scancode, mods));
			}
		};

		this.charListener = new GLFWListenerChar() {
			@Override
			public void invokeChar(GLFWWindow window, int codepoint) {
				// TODO stack event on each focused guis
				mainGui.stackEvent(new GuiEventChar<Gui>(mainGui, window, codepoint));
			}
		};
		this.glfwWindow.addListener(this.charListener);
	}

	/** remove listeners from the window */
	public final void deinitialize() {
		this.glfwWindow.removeListener(this.keyListener);
		this.glfwWindow.removeListener(this.charListener);
	}

	public final void update() {
		double xpos = this.glfwWindow.getMouseX();
		double ypos = this.glfwWindow.getMouseY();
		float mx = (float) (xpos / this.glfwWindow.getWidth());
		float my = (float) (1 - (ypos / this.glfwWindow.getHeight()));
		boolean pressed = this.glfwWindow.isMouseLeftPressed();
		this.mainGui.updateInput(mx, my, pressed);
	}
}
