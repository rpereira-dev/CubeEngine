package com.grillecube.client.opengl.window.event;

import com.grillecube.client.opengl.window.GLFWWindow;

public abstract class GLFWEventMouse extends GLFWEvent {

	private double mouseX;
	private double mouseY;
	private double prevMouseX;
	private double prevMouseY;

	public GLFWEventMouse(GLFWWindow window) {
		super(window);
		this.mouseX = window.getMouseX();
		this.mouseY = window.getMouseY();
		this.prevMouseX = window.getPrevMouseX();
		this.prevMouseY = window.getPrevMouseY();
	}

	public final double getMouseX() {
		return (this.mouseX);
	}

	/** get mouse Y coordinate */
	public final double getMouseY() {
		return (this.mouseY);
	}

	public final double getPrevMouseX() {
		return (this.prevMouseX);
	}

	public final double getPrevMouseY() {
		return (this.prevMouseY);
	}
}
