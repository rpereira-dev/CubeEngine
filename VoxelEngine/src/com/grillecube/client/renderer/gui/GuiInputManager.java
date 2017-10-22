package com.grillecube.client.renderer.gui;

import java.util.ArrayList;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.event.GuiEventGainFocus;
import com.grillecube.client.renderer.gui.event.GuiEventLooseFocus;

/** catch inputs and send them back to a gui */
public abstract class GuiInputManager {

	/** the window on which input are catched */
	private GLFWWindow glfwWindow;
	private Gui focusedGui;

	public GuiInputManager() {
	}

	/** initialize the input manager, to be called in a GL context */
	public final void initialize(GLFWWindow glfwWindow) {
		this.glfwWindow = glfwWindow;
		this.setFocusedGui(null);
		this.onInitialized();
	}

	protected abstract void onInitialized();

	/** de-initialize the input manager, to be called in a GL context */
	public final void deinitialize() {
		this.onDeinitialized();
	}

	protected abstract void onDeinitialized();

	/** update the input manager */
	public final void update(ArrayList<Gui> guis) {
		Gui g = this.getFocusedGui();
		if (g != null && g.requestedUnfocus()) {
			this.setFocusedGui(null);
		}
		this.onUpdate(guis);
	}

	protected abstract void onUpdate(ArrayList<Gui> guis);

	protected final void setFocusedGui(Gui gui) {
		if (this.focusedGui == gui) {
			return;
		}

		if (this.focusedGui != null) {
			this.focusedGui.focus(false);
			this.focusedGui.stackEvent(new GuiEventLooseFocus<Gui>(this.focusedGui));
		}
		this.focusedGui = gui;
		// Logger.get().log(Logger.Level.DEBUG, "focused gui is now: "
		// + (this.focusedGui != null ?
		// this.focusedGui.getClass().getSimpleName() : "null"));
		if (this.focusedGui != null) {
			this.focusedGui.focus(true);
			this.focusedGui.stackEvent(new GuiEventGainFocus<Gui>(this.focusedGui));
		}
	}

	public final Gui getFocusedGui() {
		return (this.focusedGui);
	}

	public final GLFWWindow getGLFWWindow() {
		return (this.glfwWindow);
	}
}
