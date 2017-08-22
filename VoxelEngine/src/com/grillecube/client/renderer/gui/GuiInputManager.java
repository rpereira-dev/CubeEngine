package com.grillecube.client.renderer.gui;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.gui.components.Gui;

/** catch inputs and send them back to a gui */
public class GuiInputManager {

	/** the window on which input are catched */
	private GLFWWindow glfwWindow;
	private Gui mainGui;

	/** add listeners to the window */
	public final void initialize(GLFWWindow glfwWindow, Gui pgui) {

		this.glfwWindow = glfwWindow;
		this.mainGui = pgui;
	}

	/** remove listeners from the window */
	public final void deinitialize() {

	}

	public final void update() {

		// TODO : only update here if any new events appeared (user input, new
		// gui added, removed, focus changed...) since last update
		// for now, it update every frames, meaning if the user doesnt process
		// any input, every calculation bellow will still be executed
		double xpos = this.glfwWindow.getMouseX();
		double ypos = this.glfwWindow.getMouseY();
		float mx = (float) (xpos / this.glfwWindow.getWidth());
		float my = (float) (1 - (ypos / this.glfwWindow.getHeight()));
		boolean pressed = this.glfwWindow.isMouseLeftPressed();
		this.mainGui.updateInput(mx, my, pressed);
	}
}
