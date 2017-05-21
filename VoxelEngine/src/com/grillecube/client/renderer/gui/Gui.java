/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.gui;

import java.util.ArrayList;

import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.gui.animations.GuiAnimation;
import com.grillecube.client.renderer.gui.components.GuiView;
import com.grillecube.common.maths.Vector4f;

public abstract class Gui {

	/** default colors */
	public static final Vector4f COLOR_WHITE = new Vector4f(0.9f, 0.9f, 0.9f, 1.0f);
	public static final Vector4f COLOR_BLACK = new Vector4f(0.1f, 0.1f, 0.1f, 1.0f);
	public static final Vector4f COLOR_BLUE = new Vector4f(65 / 255.0f, 105 / 255.0f, 225 / 255.0f, 1.0f);
	public static final Vector4f COLOR_RED = new Vector4f(176 / 255.0f, 23 / 255.0f, 31 / 255.0f, 1.0f);
	public static final Vector4f COLOR_DARK_MAGENTA = new Vector4f(139 / 255.0f, 0 / 255.0f, 139 / 255.0f, 1.0f);

	/**
	 * a pamameter which tell the gui to be centered toward the gui center
	 * position.
	 * 
	 * @see Gui.setCenter(float x, float y);
	 */
	public static final GuiParameter<Gui> PARAM_CENTER = new GuiParameter<Gui>() {

		@Override
		public void run(Gui gui) {
			float x = gui.getCenterX() - gui.getWidth() / 2.0f;
			float y = gui.getCenterY() + gui.getHeight() / 2.0f;
			gui.setPosition(x, y, false);
		}
	};

	/** this rectangle relative to opengl axis (-1;-1) to (1;1) */
	private float _posx;
	private float _posy;
	private float _width;
	private float _height;

	/** background texture */
	private GLTexture _bg_texture;
	private float _uvxmin;
	private float _uvymin;
	private float _uvxmax;
	private float _uvymax;

	/** center , only used if using PARAM_CENTER GuiParameter */
	protected float _ycenter;
	protected float _xcenter;

	private boolean _mouse_in;
	private boolean _left_pressed;
	private boolean _focused;

	private ArrayList<GuiParameter<Gui>> _params;

	/** gui animations */
	private ArrayList<GuiAnimation<Gui>> _animations;

	private ArrayList<GuiListenerMouseHover<?>> _listeners_mouse_hover;
	private ArrayList<GuiListenerMouseEnter<?>> _listeners_mouse_enter;
	private ArrayList<GuiListenerMouseExit<?>> _listeners_mouse_exit;
	private ArrayList<GuiListenerMouseLeftRelease<?>> _listeners_mouse_left_release;
	private ArrayList<GuiListenerMouseLeftPress<?>> _listeners_mouse_left_press;
	private boolean _focus_requested;

	public Gui() {
		this(0, 0);
	}

	public Gui(float posx, float posy) {
		this(posx, posy, 0.1f, 0.1f);
	}

	public Gui(float x, float y, float width, float height) {
		this._posx = x;
		this._posy = y;
		this._width = width;
		this._height = height;
		this._params = new ArrayList<GuiParameter<Gui>>();
		this._animations = new ArrayList<GuiAnimation<Gui>>();
		this._listeners_mouse_hover = new ArrayList<GuiListenerMouseHover<?>>();
		this._listeners_mouse_enter = new ArrayList<GuiListenerMouseEnter<?>>();
		this._listeners_mouse_exit = new ArrayList<GuiListenerMouseExit<?>>();
		this._listeners_mouse_left_release = new ArrayList<GuiListenerMouseLeftRelease<?>>();
		this._listeners_mouse_left_press = new ArrayList<GuiListenerMouseLeftPress<?>>();
		this._focused = false;
	}

	/** a listener to the mouse hovering the gui */
	public void addListener(GuiListenerMouseHover<?> listener) {
		this._listeners_mouse_hover.add(listener);
	}

	/** a listener to the mouse entering the gui */
	public void addListener(GuiListenerMouseEnter<?> listener) {
		this._listeners_mouse_enter.add(listener);
	}

	/** a listener to the mouse entering the gui */
	public void addListener(GuiListenerMouseExit<?> listener) {
		this._listeners_mouse_exit.add(listener);
	}

	/** a listener to the mouse entering the gui */
	public void addListener(GuiListenerMouseLeftRelease<?> listener) {
		this._listeners_mouse_left_release.add(listener);
	}

	/** a listener to the mouse entering the gui */
	public void addListener(GuiListenerMouseLeftPress<?> listener) {
		this._listeners_mouse_left_press.add(listener);
	}

	/** a listener to the mouse hovering the gui */
	public void removeListener(GuiListenerMouseHover<?> listener) {
		this._listeners_mouse_hover.remove(listener);
	}

	/** a listener to the mouse entering the gui */
	public void removeListener(GuiListenerMouseEnter<?> listener) {
		this._listeners_mouse_enter.remove(listener);
	}

	/** a listener to the mouse entering the gui */
	public void removeListener(GuiListenerMouseExit<?> listener) {
		this._listeners_mouse_exit.remove(listener);
	}

	/** a listener to the mouse entering the gui */
	public void removeListener(GuiListenerMouseLeftRelease<?> listener) {
		this._listeners_mouse_left_release.remove(listener);
	}

	/** a listener to the mouse entering the gui */
	public void removeListener(GuiListenerMouseLeftPress<?> listener) {
		this._listeners_mouse_left_press.remove(listener);
	}

	/**
	 * start an animation
	 * 
	 * @return the animation id
	 */
	@SuppressWarnings("unchecked")
	public int startAnimation(GuiAnimation<? extends Gui> animation) {
		this._animations.add((GuiAnimation<Gui>) animation);
		((GuiAnimation<Gui>) animation).restart(this);
		return (this._animations.size() - 1);
	}

	/** stop an animation by it id */
	public void stopAnimation(int index) {
		if (index >= 0 && index < this._animations.size()) {
			this._animations.remove(index);
		}
	}

	/** stop every animation with the given class */
	public void stopAnimation(Class<?> clazz) {
		int i = 0;
		while (i < this._animations.size()) {
			GuiAnimation<?> animation = this._animations.get(i);

			if (animation == null) {
				continue;
			}

			if (animation.getClass().equals(clazz)) {
				this._animations.remove(i);
				continue;
			}
			++i;
		}
	}

	public void setPosition(float x, float y) {
		this.setPosition(x, y, true);
	}

	protected void setPosition(float x, float y, boolean b) {
		this._posx = x;
		this._posy = y;
		if (b) {
			this.runParameters();
		}
	}

	/** set the gui text center, if using PARAM_CENTER */
	public void setCenter(float xcenter, float ycenter) {
		this._xcenter = xcenter;
		this._ycenter = ycenter;
		this.runParameters();
	}

	/** set the x position, but do not run parameters to adjust it */
	public void setX(float x) {
		this.setPosition(x, this._posy);
	}

	/** set the y position, but do not run parameters to adjust it */
	public void setY(float y) {
		this.setPosition(this._posx, y);
	}

	/** set the width, but do not run parameters to adjust it */
	public void setWidth(float width) {
		this._width = width;
	}

	/** set the height, but do not run parameters to adjust it */
	public void setHeight(float height) {
		this._height = height;
	}

	public void setSize(float width, float height) {
		this._width = width;
		this._height = height;
		this.runParameters();
	}

	public void set(float x, float y, float width, float height) {
		this._posx = x;
		this._posy = y;
		this._width = width;
		this._height = height;
		this.runParameters();
	}

	/**
	 * render the gui
	 * 
	 * @param program
	 */
	public void render(GuiRenderer renderer) {
		if (this._bg_texture != null) {
			renderer.renderQuad(this._bg_texture, this._uvxmin, this._uvymin, this._uvxmax, this._uvymax, this._posx,
					this._posy, this._width, this._height);
		}
	}

	public abstract void onAdded(GuiView view);

	public abstract void onRemoved(GuiView view);

	/**
	 * update this gui
	 * 
	 * @param x
	 *            : mouseX
	 * @param y
	 *            : mouseY
	 * @param pressed
	 *            : if mouse is left pressed
	 */
	public final void update(float x, float y, boolean pressed) {
		this.updateState(x, y, pressed);
		this.onUpdate(x, y, pressed);
		this.updateAnimations();
	}

	private void updateAnimations() {
		int i = 0;
		while (i < this._animations.size()) {
			GuiAnimation<Gui> animation = this._animations.get(i);
			if (animation == null || animation.update(this)) {
				this._animations.remove(i);
				continue;
			}
			++i;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateState(float x, float y, boolean pressed) {

		boolean mouse_in = (x >= this._posx && x <= this._posx + this._width && y + this._height >= this._posy
				&& y <= this._posy);

		if (mouse_in) {

			// raise hover event
			for (GuiListenerMouseHover listener : this._listeners_mouse_hover) {
				listener.invokeMouseHover(this, x, y);
			}
		}

		if (this._mouse_in && !mouse_in) {
			this._mouse_in = false;

			// raise exit event
			for (GuiListenerMouseExit listener : this._listeners_mouse_exit) {
				listener.invokeMouseExit(this, x, y);
			}

		} else if (!this._mouse_in && mouse_in) {
			this._mouse_in = true;

			for (GuiListenerMouseEnter listener : this._listeners_mouse_enter) {
				listener.invokeMouseEnter(this, x, y);
			}

		}

		if (this._left_pressed && !pressed) {
			this._left_pressed = false;

			for (GuiListenerMouseLeftRelease listener : this._listeners_mouse_left_release) {
				listener.invokeMouseLeftRelease(this, x, y);
			}

		} else if (!this._left_pressed && pressed && this._mouse_in) {
			this._left_pressed = true;
			for (GuiListenerMouseLeftPress listener : this._listeners_mouse_left_press) {
				listener.invokeMouseLeftPress(this, x, y);
			}
		}
	}

	protected abstract void onUpdate(float x, float y, boolean pressed);

	/** return true if the mouse cursor is inside this gui */
	public boolean isHovered() {
		return (this._mouse_in);
	}

	public boolean isLeftPressed() {
		return (this._left_pressed);
	}

	@SuppressWarnings("unchecked")
	public void addParameter(GuiParameter<?> parameter) {
		this._params.add((GuiParameter<Gui>) parameter);
		((GuiParameter<Gui>) parameter).run(this);
	}

	public void addParameters(GuiParameter<?>... parameters) {
		for (GuiParameter<?> parameter : parameters) {
			this.addParameter(parameter);
		}
	}

	/**
	 * run the parameter to update the gui position and size depending on it
	 * parameters You shall not have to call it, as it is call when setting a
	 * gui's size and position automatically
	 */
	public void runParameters() {
		for (GuiParameter<Gui> param : this._params) {
			param.run(this);
		}
	}

	public boolean hasParameter(GuiParameter<?> param) {
		if (this._params == null) {
			return (false);
		}
		return (this._params.contains(param));
	}

	public float getCenterX() {
		return (this._xcenter);
	}

	public float getCenterY() {
		return (this._ycenter);
	}

	public float getX() {
		return (this._posx);
	}

	public float getY() {
		return (this._posy);
	}

	public float getWidth() {
		return (this._width);
	}

	public float getHeight() {
		return (this._height);
	}

	public boolean hasFocus() {
		return (this._focused);
	}

	public void setFocus(boolean value) {
		this._focused = value;
		this.setFocusRequest(value);
		if (value) {
			this.onFocusGained();
		} else {
			this.onFocusLost();
		}
	}

	protected void onFocusLost() {
	}

	protected void onFocusGained() {
	}

	public void setFocusRequest(boolean value) {
		this._focus_requested = value;
	}

	public boolean hasFocusRequest() {
		return (this._focus_requested);
	}

	public void delete() {
	}

	public void setBackgroundTexture(GLTexture texture) {
		this._bg_texture = texture;
	}

	public void setBackgroundTexture(GLTexture texture, float uvxmin, float uvymin, float uvxmax, float uvymax) {
		this._bg_texture = texture;
		this._uvxmin = uvxmin;
		this._uvymin = uvymin;
		this._uvxmax = uvxmax;
		this._uvymax = uvymax;
	}

	public GLTexture getBackgroundTexture() {
		return (this._bg_texture);
	}
}
