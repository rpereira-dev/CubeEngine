package com.grillecube.client.renderer.gui;

import java.util.ArrayList;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.opengl.window.event.GLFWEventChar;
import com.grillecube.client.opengl.window.event.GLFWEventKeyPress;
import com.grillecube.client.opengl.window.event.GLFWListener;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.event.GuiEventChar;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiEventGainFocus;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventLooseFocus;
import com.grillecube.client.renderer.gui.event.GuiEventMouseEnter;
import com.grillecube.client.renderer.gui.event.GuiEventMouseExit;
import com.grillecube.client.renderer.gui.event.GuiEventMouseHover;
import com.grillecube.client.renderer.gui.event.GuiEventMouseMove;
import com.grillecube.client.renderer.gui.event.GuiEventPress;
import com.grillecube.client.renderer.gui.event.GuiEventUnpress;
import com.grillecube.common.Logger;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector4f;

/** catch inputs and send them back to a gui */
public class GuiInputManager {

	/** the window on which input are catched */
	private GLFWWindow glfwWindow;
	private Gui mainGui;
	private Gui focusedGui;
	private GLFWListener<GLFWEventKeyPress> keyPressListener;
	private GLFWListener<GLFWEventChar> charListener;

	/** add listeners to the window */
	public final void initialize(GLFWWindow glfwWindow, Gui pgui) {
		this.glfwWindow = glfwWindow;
		this.mainGui = pgui;
		this.setFocusedGui(this.mainGui);
		this.keyPressListener = new GLFWListener<GLFWEventKeyPress>() {
			@Override
			public void invoke(GLFWEventKeyPress event) {
				if (focusedGui != null) {
					focusedGui.stackEvent(new GuiEventKeyPress<Gui>(focusedGui, event.getGLFWWindow(), event.getKey(),
							event.getScancode(), event.getMods()));
				}
			}
		};

		this.charListener = new GLFWListener<GLFWEventChar>() {
			@Override
			public void invoke(GLFWEventChar event) {
				if (focusedGui != null) {
					focusedGui
							.stackEvent(new GuiEventChar<Gui>(focusedGui, event.getGLFWWindow(), event.getCharacter()));
				}
			}
		};

		this.glfwWindow.addListener(this.keyPressListener);
		this.glfwWindow.addListener(this.charListener);
	}

	/** remove listeners from the window */
	public final void deinitialize() {
		this.glfwWindow.removeListener(this.keyPressListener);
		this.glfwWindow.removeListener(this.charListener);
	}

	private final void setFocusedGui(Gui gui) {
		if (this.focusedGui == gui) {
			return;
		}

		if (this.focusedGui != null) {
			this.focusedGui.focus(false);
			this.focusedGui.stackEvent(new GuiEventLooseFocus<Gui>(this.focusedGui));
		}
		this.focusedGui = gui;
		Logger.get().log(Logger.Level.DEBUG, "focused gui is now: "
				+ (this.focusedGui != null ? this.focusedGui.getClass().getSimpleName() : "null"));
		if (this.focusedGui != null) {
			this.focusedGui.focus(true);
			this.focusedGui.stackEvent(new GuiEventGainFocus<Gui>(this.focusedGui));
		}
	}

	/** update the given guis, which should be sorted by their layers */
	public final void update(ArrayList<Gui> guis) {

		if (this.focusedGui != null && this.focusedGui.requestedUnfocus()) {
			this.setFocusedGui(null);
		}

		double xpos = this.glfwWindow.getMouseX();
		double ypos = this.glfwWindow.getMouseY();
		float mx = (float) (xpos / this.glfwWindow.getWidth());
		float my = (float) (1 - (ypos / this.glfwWindow.getHeight()));
		boolean pressed = this.glfwWindow.isMouseLeftPressed();
		boolean topestHoveredFound = false;

		int i;
		for (i = guis.size() - 1; i >= 0; i--) {
			Gui gui = guis.get(i);
			gui.update();

			if (gui.requestedFocus()) {
				this.setFocusedGui(gui);
				gui.requestFocus(false);
			}

			// get the coordinates relatively to the gui basis
			Vector4f mouse = new Vector4f(mx, my, 0.0f, 1.0f);
			Matrix4f.transform(gui.getWindowToGuiChangeOfBasis(), mouse, mouse);

			// update gui states (stack events)
			float x = mouse.x;
			float y = mouse.y;
			gui.setMouse(x, y);

			// mouse moving relatively to the gui
			gui.stackEvent(new GuiEventMouseMove<Gui>(gui));

			// if gui is hovered,
			if (!topestHoveredFound && gui.isHoverable() && (x >= 0.0f && x < 1.0f && y >= 0.0f && y <= 1.0f)) {
				topestHoveredFound = true;
				gui.stackEvent(new GuiEventMouseHover<Gui>(gui));
				// if gui wasnt hovered earlier
				if (!gui.isHovered()) {
					gui.setHovered(true);
					gui.stackEvent(new GuiEventMouseEnter<Gui>(gui));
				}
				// if mouse wasnt pressed, and now is pressed, and the gui is
				// hovered
				if (!gui.isPressed() && pressed) {
					gui.setPressed(true);
					gui.stackEvent(new GuiEventPress<Gui>(gui));
					if (gui.isSelectable()) {
						gui.setSelected(!gui.isSelected());
					}
				} else if (gui.isPressed() && !pressed) {
					gui.setPressed(false);
					gui.stackEvent(new GuiEventUnpress<Gui>(gui));
					gui.stackEvent(new GuiEventClick<Gui>(gui));
				}
			} else {
				if (gui.isHovered()) {
					gui.setHovered(false);
					gui.stackEvent(new GuiEventMouseExit<Gui>(gui));
				}

				if (gui.isPressed() && !pressed) {
					gui.setPressed(false);
					gui.stackEvent(new GuiEventUnpress<Gui>(gui));
				}
			}
			gui.unstackEvents();
		}
	}
}
