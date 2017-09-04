package com.grillecube.client.renderer.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

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
				focusedGui.stackEvent(new GuiEventChar<Gui>(focusedGui, event.getGLFWWindow(), event.getCharacter()));
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
		if (this.focusedGui != null) {
			this.focusedGui.focus(false);
			this.focusedGui.stackEvent(new GuiEventLooseFocus<Gui>(this.focusedGui));
		}
		this.focusedGui = gui;
		if (this.focusedGui != null) {
			this.focusedGui.focus(true);
			this.focusedGui.stackEvent(new GuiEventGainFocus<Gui>(this.focusedGui));
		}
	}

	private static final Comparator<Integer> LAYER_SORT = new Comparator<Integer>() {
		@Override
		public int compare(Integer a, Integer b) {
			return (b - a);
		}
	};

	// public final void update() {
	// double xpos = this.glfwWindow.getMouseX();
	// double ypos = this.glfwWindow.getMouseY();
	// float mx = (float) (xpos / this.glfwWindow.getWidth());
	// float my = (float) (1 - (ypos / this.glfwWindow.getHeight()));
	// boolean pressed = this.glfwWindow.isMouseLeftPressed();
	// HashMap<Integer, ArrayList<Gui>> guis = new HashMap<Integer,
	// ArrayList<Gui>>();
	// this.getGuis(guis, this.mainGui);
	//
	// ArrayList<Integer> layers = new ArrayList<Integer>(guis.keySet());
	// layers.sort(LAYER_SORT);
	//
	// boolean action = false;
	// int i = 0;
	// while (!action && i < layers.size()) {
	// ArrayList<Gui> layerGuis = guis.get(layers.get(i));
	// i = i + 1;
	//
	// for (Gui gui : layerGuis) {
	// gui.update();
	//
	// if (!gui.isVisible()) {
	// continue;
	// }
	//
	// if (gui.requestedFocus()) {
	// this.setFocusedGui(gui);
	// gui.requestFocus(false);
	// }
	//
	// if (gui.isResponsive() && this.stackGuiInput(gui, mx, my, pressed)) {
	// action = true;
	// gui.unstackEvents();
	// }
	// }
	// }
	// }

	// private void getGuis(HashMap<Integer, ArrayList<Gui>> guis, Gui gui) {
	// ArrayList<Gui> lst;
	// if (!guis.containsKey(gui.getLayer())) {
	// lst = new ArrayList<Gui>();
	// guis.put(gui.getLayer(), lst);
	// } else {
	// lst = guis.get(gui.getLayer());
	// }
	// lst.add(gui);
	// if (gui.getChildren() != null) {
	// for (Gui child : gui.getChildren()) {
	// this.getGuis(guis, child);
	// }
	// }
	// }

	public final void update() {
		double xpos = this.glfwWindow.getMouseX();
		double ypos = this.glfwWindow.getMouseY();
		float mx = (float) (xpos / this.glfwWindow.getWidth());
		float my = (float) (1 - (ypos / this.glfwWindow.getHeight()));
		boolean pressed = this.glfwWindow.isMouseLeftPressed();
		ArrayList<Gui> guis = new ArrayList<Gui>();
		this.getGuis(guis, this.mainGui);

		for (Gui gui : guis) {
			gui.update();

			if (!gui.isVisible()) {
				continue;
			}

			if (gui.requestedFocus()) {
				this.setFocusedGui(gui);
				gui.requestFocus(false);
			}

			if (gui.isResponsive()) {
				this.updateGuiInput(gui, mx, my, pressed);
			}
		}
	}

	private void getGuis(ArrayList<Gui> guis, Gui gui) {
		guis.add(gui);
		if (gui.getChildren() != null) {
			for (Gui child : gui.getChildren()) {
				this.getGuis(guis, child);
			}
		}
	}

	private final void updateGuiInput(Gui gui, float mx, float my, boolean pressed) {
		// get the coordinates relatively to the gui basis
		Vector4f mouse = new Vector4f(mx, my, 0.0f, 1.0f);
		Matrix4f.transform(gui.getWindowToGuiChangeOfBasis(), mouse, mouse);

		// update gui states (stack events)
		float x = mouse.x;
		float y = mouse.y;
		gui.setMouse(x, y);

		// mouse moving relatively to the gui
		gui.stackEvent(new GuiEventMouseMove<Gui>(gui));

		boolean hovered = (x >= 0.0f && x < 1.0f && y >= 0.0f && y <= 1.0f);

		// if mouse is in the gui bounding box
		if (hovered) {
			// raise hover event
			gui.stackEvent(new GuiEventMouseHover<Gui>(gui));
		}

		// if mouse is not in the gui bounding box, but used to be
		if (gui.isHovered() && !hovered) {
			gui.setHovered(false);
			// raise exit event
			gui.stackEvent(new GuiEventMouseExit<Gui>(gui));
		} else if (!gui.isHovered() && hovered) {
			// if mouse is in the gui bounding box, but wasnt earlier
			gui.setHovered(true);
			gui.stackEvent(new GuiEventMouseEnter<Gui>(gui));
		}

		// if mouse wasnt pressed, and now is pressed, and the gui is hovered
		if (!gui.isPressed() && pressed && gui.isHovered()) {
			gui.setPressed(true);
			gui.stackEvent(new GuiEventPress<Gui>(gui));
			if (gui.isSelectable()) {
				gui.setSelected(!gui.isSelected());
			}
		} else if (gui.isPressed() && !pressed) {
			gui.setPressed(false);
			gui.stackEvent(new GuiEventUnpress<Gui>(gui));
			if (gui.isHovered()) {
				gui.stackEvent(new GuiEventClick<Gui>(gui));
			}
		}

		gui.unstackEvents();
	}
}
