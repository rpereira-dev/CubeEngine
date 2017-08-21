package com.grillecube.client.renderer.gui.event;

import com.grillecube.client.renderer.gui.components.Gui;

public class GuiEventMouse<T extends Gui> extends GuiEvent<T> {

	private float mouseX;
	private float mouseY;
	private float prevMouseX;
	private float prevMouseY;

	public GuiEventMouse(T gui) {
		super(gui);
		this.mouseX = gui.getMouseX();
		this.mouseY = gui.getMouseY();
		this.prevMouseX = gui.getPrevMouseX();
		this.prevMouseY = gui.getPrevMouseY();
	}

	/**
	 * @return : the mouse X coordinate relatively to the gui basis
	 */
	public final float getMouseX() {
		return (this.mouseX);
	}

	/**
	 * @return : the mouse Y coordinate relatively to the gui basis
	 */
	public final float getMouseY() {
		return (this.mouseY);
	}

	/**
	 * @return : the previous mouse X coordinate relatively to the gui basis
	 */
	public final float getPrevMouseX() {
		return (this.prevMouseX);
	}

	/**
	 * @return : the previous mouse Y coordinate relatively to the gui basis
	 */
	public final float getPrevMouseY() {
		return (this.prevMouseY);
	}
}
