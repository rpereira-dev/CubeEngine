package com.grillecube.engine.renderer.gui.components;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.grillecube.engine.Logger;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.renderer.gui.Gui;
import com.grillecube.engine.renderer.gui.GuiRenderer;
import com.grillecube.engine.renderer.gui.font.FontModel;

public abstract class GuiView {

	/** guis of this view */
	private ArrayList<Gui> _guis;
	/** list of font modeles to render */
	private ArrayList<FontModel> _fonts_model;
	/** current gui focused */
	private Gui _gui_focused;
	private GLFWWindow _window;

	public GuiView() {
		this._guis = new ArrayList<Gui>();
		this._fonts_model = new ArrayList<FontModel>();
		this._gui_focused = null;
	}

	/** load the view (called in an opengl context) */
	public abstract void onAdded(GuiRenderer renderer);

	/** free allocated stuff here */
	public abstract void onRemoved(GuiRenderer renderer);

	public void delete(GuiRenderer renderer) {
		for (FontModel model : this._fonts_model) {
			model.delete();
		}
		for (Gui gui : this._guis) {
			gui.delete();
		}
	}

	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		if (key == GLFW.GLFW_KEY_ESCAPE && _gui_focused != null) {
			setGuiFocused(null);
		} else if (key == GLFW.GLFW_KEY_TAB) {
			if (_gui_focused == null) {
				if (_guis.size() > 0) {
					setGuiFocused(_guis.get(0));
				}
			} else {
				int index = _guis.indexOf(_gui_focused);
				if (index != -1) {
					setGuiFocused(_guis.get((index + 1) % _guis.size()));
				}
			}
		}
	}

	public void update() {
		GLFWWindow window = this.getGLFWWindow();
		float mx = (float) (this.getGLFWWindow().getMouseX() / window.getWidth() * 2 - 1);
		float my = (float) (window.getMouseY() / window.getHeight() * 2 - 1);
		boolean pressed = window.isMouseLeftPressed();
		for (int i = 0; i < _guis.size(); i++) {
			Gui gui = _guis.get(i);
			gui.update(mx, -my, pressed);
			if (gui != this._gui_focused && gui.hasFocusRequest()) {
				this.setGuiFocused(gui);
			}
		}

		if (this._gui_focused != null && !this._gui_focused.hasFocusRequest()) {
			this.setGuiFocused(null);
		}
		this.onUpdate();
	}

	protected void onUpdate() {

	}

	public GLFWWindow getGLFWWindow() {
		return (this._window);
	}

	public void setGLFWWindow(GLFWWindow window) {
		this._window = window;
	}

	private void setGuiFocused(Gui gui) {
		if (this._gui_focused != null) {
			this._gui_focused.setFocus(false);
		}
		this._gui_focused = gui;
		if (this._gui_focused != null) {
			this._gui_focused.setFocus(true);
		}
	}

	/** add gui to the renderer */
	public void addGui(Gui gui) {
		if (gui == null) {
			return;
		}
		this._guis.add(gui);
		gui.onAdded(this);
	}

	/** remove gui from the renderer */
	public void removeGui(Gui gui) {
		if (gui == null || this._guis.size() == 0) {
			return;
		}
		this._guis.remove(gui);
		gui.onRemoved(this);
	}

	/** remove gui from the renderer */
	public void removeGui() {
		if (this._guis.size() == 0) {
			return;
		}
		this.removeGui(this._guis.get(this._guis.size() - 1));
	}

	/** add the font model to the rendering pipeline */
	public FontModel addFontModel(FontModel model) {
		this._fonts_model.add(model);
		return (model);
	}

	/** remove the font model from the rendering pipeline */
	public void removeFontModel(FontModel model) {
		if (model == null) {
			return;
		}
		this._fonts_model.remove(model);
	}

	public void render(GuiRenderer renderer) {
		for (Gui gui : this._guis) {
			gui.render(renderer);
		}
	}

	protected String getName() {
		return (this.getClass().getSimpleName());
	}
}
