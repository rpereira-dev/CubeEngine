package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.font.FontModel;

/**
 * A GuiView is a View. You can add elements to it (size and position of these
 * elements are relative to the GuiView dimensions)
 * 
 * When a GuiView is removed, the "onRemoved()" callback function is called.
 * This is where you can eventually free the allocated memory by calling the
 * "delete()" routine, or freeing every added elements manually
 * 
 * @author Romain
 *
 */
public abstract class GuiView2 {

	/** guis of this view */
	private final ArrayList<Gui> guis;
	/** list of font modeles to render */
	private final ArrayList<FontModel> fontModels;
	/** current gui focused */
	private Gui guiFocused;
	private GLFWWindow window;

	public GuiView2() {
		this.guis = new ArrayList<Gui>();
		this.fontModels = new ArrayList<FontModel>();
		this.guiFocused = null;
	}

	/** called when this view is added to the gui renderer */
	public void onAdded(GuiRenderer renderer) {

	}

	/** called when this view is removed from the gui renderer */
	public void onRemoved(GuiRenderer renderer) {

	}

	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_ESCAPE && guiFocused != null) {
			setGuiFocused(null);
		} else if (key == GLFW.GLFW_KEY_TAB) {
			if (guiFocused == null) {
				if (guis.size() > 0) {
					setGuiFocused(guis.get(0));
				}
			} else {
				int index = guis.indexOf(guiFocused);
				if (index != -1) {
					setGuiFocused(guis.get((index + 1) % guis.size()));
				}
			}
		}
	}

	public void update() {
		GLFWWindow window = this.getGLFWWindow();
		float mx = (float) (this.getGLFWWindow().getMouseX() / window.getWidth() * 2 - 1);
		float my = (float) (window.getMouseY() / window.getHeight() * 2 - 1);
		boolean pressed = window.isMouseLeftPressed();
		for (int i = 0; i < guis.size(); i++) {
			Gui gui = guis.get(i);
			gui.update(mx, -my, pressed);
			if (gui != this.guiFocused && gui.hasFocusRequest()) {
				this.setGuiFocused(gui);
			}
		}

		if (this.guiFocused != null && !this.guiFocused.hasFocusRequest()) {
			this.setGuiFocused(null);
		}
		this.onUpdate();
	}

	protected void onUpdate() {

	}

	public GLFWWindow getGLFWWindow() {
		return (this.window);
	}

	public void setGLFWWindow(GLFWWindow window) {
		this.window = window;
	}

	private void setGuiFocused(Gui gui) {
		if (this.guiFocused != null) {
			this.guiFocused.setFocus(false);
		}
		this.guiFocused = gui;
		if (this.guiFocused != null) {
			this.guiFocused.setFocus(true);
		}
	}

	/** add gui to the renderer */
	public void addGui(Gui gui) {
		if (gui == null) {
			return;
		}
		this.guis.add(gui);
	}

	/** remove gui from the renderer */
	public void removeGui(Gui gui) {
		if (gui == null || this.guis.size() == 0) {
			return;
		}
		this.guis.remove(gui);
	}

	/** remove gui from the renderer */
	public void removeGui() {
		if (this.guis.size() == 0) {
			return;
		}
		this.removeGui(this.guis.get(this.guis.size() - 1));
	}

	/** add the font model to the rendering pipeline */
	public FontModel addFontModel(FontModel model) {
		this.fontModels.add(model);
		return (model);
	}

	/** remove the font model from the rendering pipeline */
	public void removeFontModel(FontModel model) {
		if (model == null) {
			return;
		}
		this.fontModels.remove(model);
	}

	public void render(GuiRenderer renderer) {
		for (Gui gui : this.guis) {
			gui.render(renderer);
		}
	}

	protected String getName() {
		return (this.getClass().getSimpleName());
	}
}
