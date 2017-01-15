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

package com.grillecube.engine.opengl;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

import com.grillecube.engine.opengl.object.GLObject;

/**
 * 
 * HOW TO USE:
 * 
 * create a new instance (GLFWWindow window = new GLFWWindow())
 * 
 * call 'window.create()' to set GLContext + resize and error callback
 * 
 * window.prepareScreen(); //clear screen ... // rendering stuff goes here
 * window.flushScreen(); //swap buffer + update fps counter
 */
public class GLFWWindow implements GLFWListenerKeyPress, GLObject {
	public static final int OS_LINUX = 0;
	public static final int OS_WINDOWS = 1;
	public static final int OS_MAC = 2;

	public static final String OS_NAME = System.getProperty("os.name");
	private static final String _OS_NAME = OS_NAME.toLowerCase();
	public static final int OS = _OS_NAME.contains("win") ? OS_WINDOWS : _OS_NAME.contains("mac") ? OS_MAC : OS_LINUX;

	/** screen resolution */
	private int _screenx;
	private int _screeny;

	/** window pointer */
	private long _ptr;

	/** window size (in pixels) */
	private int _width;
	private int _height;

	/** window size (in pixels) */
	private DoubleBuffer _bufferX;
	private DoubleBuffer _bufferY;

	private double _mouseX;
	private double _mouseY;
	private double _prev_mouseX;
	private double _prev_mouseY;

	/** frames data */
	private long _prev_frame; // previous frame timer
	private long _frames; // total frames flushed
	private int _fps_counter; // frame per second counter
	private int _fps; // last frame per second calculated

	/** events handler */
	private ArrayList<GLFWListenerResize> _listeners_resize;
	private ArrayList<GLFWListenerKeyPress> _listeners_key_press;
	private ArrayList<GLFWListenerChar> _listeners_char;
	private ArrayList<GLFWListenerKeyRelease> _listeners_key_release;
	private ArrayList<GLFWListenerCursorPos> _listeners_cursor_pos;
	private ArrayList<GLFWListenerMouseScroll> _listeners_mouse_scroll;
	private ArrayList<GLFWListenerMousePress> _listeners_mouse_press;
	private ArrayList<GLFWListenerMouseRelease> _listeners_mouse_release;
	private ArrayList<GLFWListenerMouseEnter> _listeners_mouse_enter;
	private ArrayList<GLFWListenerMouseExit> _listeners_mouse_exit;

	/** events callback for garbage collector */
	private GLFWScrollCallback _callback_scroll;
	private GLFWCursorPosCallback _callback_cursor_pos;
	private GLFWMouseButtonCallback _callback_mouse_button;
	private GLFWKeyCallback _callback_key;
	private GLFWCharCallback _callback_char;
	private GLFWWindowSizeCallback _resize_callback;
	private GLFWWindowFocusCallback _focus_callback;

	private boolean _mouse_is_in;

	private boolean _focus;

	public GLFWWindow() {
	}

	/** create the window and initialize gl context */

	public void create(int width, int height, String title) {

		if (width == 0) {
			width = 1;
		}

		if (height == 0) {
			height = 1;
		}

		if (GLFWWindow.OS == GLFWWindow.OS_LINUX || GLFWWindow.OS == GLFWWindow.OS_MAC) {
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		}

		this._ptr = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if (this._ptr == 0) {
			System.err.println("Couldnt create glfw window");
			return;
		}

		this._width = width;
		this._height = height;

		this._mouseX = width / 2;
		this._mouseY = height / 2;

		this._prev_mouseX = width / 2;
		this._prev_mouseY = height / 2;

		this._bufferX = BufferUtils.createDoubleBuffer(1);
		this._bufferY = BufferUtils.createDoubleBuffer(1);
		this._screenx = (int) this._bufferX.get();
		this._screeny = (int) this._bufferY.get();
		this._bufferX.clear();
		this._bufferY.clear();

		this._prev_frame = System.currentTimeMillis();
		this._frames = 0;
		this._fps_counter = 0;
		this._fps = 0;

		this.initEvents();
	}

	/** return screen resolution (width) */
	public int getScreenWidth() {
		return (this._screenx);
	}

	/** return screen resolution (height) */
	public int getScreenHeight() {
		return (this._screeny);
	}

	private void initEvents() {
		this._listeners_resize = new ArrayList<GLFWListenerResize>();
		this._listeners_key_press = new ArrayList<GLFWListenerKeyPress>();
		this._listeners_char = new ArrayList<GLFWListenerChar>();
		this._listeners_key_release = new ArrayList<GLFWListenerKeyRelease>();
		this._listeners_cursor_pos = new ArrayList<GLFWListenerCursorPos>();
		this._listeners_mouse_scroll = new ArrayList<GLFWListenerMouseScroll>();
		this._listeners_mouse_press = new ArrayList<GLFWListenerMousePress>();
		this._listeners_mouse_release = new ArrayList<GLFWListenerMouseRelease>();
		this._listeners_mouse_enter = new ArrayList<GLFWListenerMouseEnter>();
		this._listeners_mouse_exit = new ArrayList<GLFWListenerMouseExit>();

		this.initWindowEvents();
		this.initKeyEvents();
		this.initMouseEvents();

		this.addKeyPressListener(this);
	}

	private void initWindowEvents() {
		this._resize_callback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				resize(width, height);
				GLFWWindow.this.invokeWindowResize(width, height);
			}
		};
		GLFW.glfwSetWindowSizeCallback(this._ptr, this._resize_callback);

		this._focus_callback = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focused) {
				_focus = focused;
			}
		};
		GLFW.glfwSetWindowFocusCallback(this._ptr, this._focus_callback);

	}

	/** window resizement */
	private void invokeWindowResize(int width, int height) {
		for (GLFWListenerResize listener : this._listeners_resize) {
			listener.invokeResize(this, width, height);
		}
	}

	/** keyboard key press */
	private void invokeKeyPressListeners(int key, int scancode, int mods) {
		for (GLFWListenerKeyPress listener : this._listeners_key_press) {
			listener.invokeKeyPress(this, key, scancode, mods);
		}
	}

	/** char input */
	private void invokeCharListener(int codepoint) {
		for (GLFWListenerChar listener : this._listeners_char) {
			listener.invokeChar(this, codepoint);
		}
	}

	/** keyboard key release */
	private void invokeKeyReleaseListeners(int key, int scancode, int mods) {
		for (GLFWListenerKeyRelease listener : this._listeners_key_release) {
			listener.invokeKeyRelease(this, key, scancode, mods);
		}
	}

	/** mouse move */
	private void invokeCursorPosListeners(double posx, double posy) {
		for (GLFWListenerCursorPos listener : this._listeners_cursor_pos) {
			listener.invokeCursorPos(this, posx, posy);
		}
	}

	/** mouse move */
	private void invokeScrollListeners(double posx, double posy) {
		for (GLFWListenerMouseScroll listener : this._listeners_mouse_scroll) {
			listener.invokeMouseScroll(this, posx, posy);
		}
	}

	/** mouse release */
	private void invokeMouseReleaseListeners(int button, int mods) {
		for (GLFWListenerMouseRelease listener : this._listeners_mouse_release) {
			listener.invokeMouseRelease(this, button, mods);
		}
	}

	/** mouse press */
	private void invokeMousePressListeners(int button, int mods) {
		for (GLFWListenerMousePress listener : this._listeners_mouse_press) {
			listener.invokeMousePress(this, button, mods);
		}
	}

	/** add listeners */
	public void addResizeListener(GLFWListenerResize listener) {
		this._listeners_resize.add(listener);
	}

	public void addKeyPressListener(GLFWListenerKeyPress listener) {
		this._listeners_key_press.add(listener);
	}

	public void addCharListener(GLFWListenerChar listener) {
		this._listeners_char.add(listener);
	}
	
	public void addKeyReleaseListener(GLFWListenerKeyRelease listener) {
		this._listeners_key_release.add(listener);
	}

	public void addMousePressListener(GLFWListenerMousePress listener) {
		this._listeners_mouse_press.add(listener);
	}

	public void addMouseReleaseListener(GLFWListenerMouseRelease listener) {
		this._listeners_mouse_release.add(listener);
	}

	public void addCursorPosListener(GLFWListenerCursorPos listener) {
		this._listeners_cursor_pos.add(listener);
	}

	public void addMouseScrollListener(GLFWListenerMouseScroll listener) {
		this._listeners_mouse_scroll.add(listener);
	}

	public void addMouseEnterListener(GLFWListenerMouseEnter listener) {
		this._listeners_mouse_enter.add(listener);
	}

	public void addMouseExitListener(GLFWListenerMouseExit listener) {
		this._listeners_mouse_exit.add(listener);
	}

	public void removeResizeListener(GLFWListenerResize listener) {
		this._listeners_resize.remove(listener);
	}

	public void removeKeyPressListener(GLFWListenerKeyPress listener) {
		this._listeners_key_press.remove(listener);
	}

	public void removeKeyReleaseListener(GLFWListenerKeyRelease listener) {
		this._listeners_key_release.remove(listener);
	}

	public void removeCharListener(GLFWListenerChar listener) {
		this._listeners_char.remove(listener);
	}
	
	
	public void removeMousePressListener(GLFWListenerMousePress listener) {
		this._listeners_mouse_press.remove(listener);
	}

	public void removeMouseReleaseListener(GLFWListenerMouseRelease listener) {
		this._listeners_mouse_release.remove(listener);
	}

	public void removeCursorPosListener(GLFWListenerCursorPos listener) {
		this._listeners_cursor_pos.remove(listener);
	}

	public void removeMouseScrollListener(GLFWListenerMouseScroll listener) {
		this._listeners_mouse_scroll.remove(listener);
	}

	public void removeMouseEnterListener(GLFWListenerMouseEnter listener) {
		this._listeners_mouse_enter.remove(listener);
	}

	public void removeMouseExitListener(GLFWListenerMouseExit listener) {
		this._listeners_mouse_exit.remove(listener);
	}

	private void initKeyEvents() {

		this._callback_char = new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				GLFWWindow.this.invokeCharListener(codepoint);
			}
		};
		GLFW.glfwSetCharCallback(this._ptr, this._callback_char);

		this._callback_key = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					GLFWWindow.this.invokeKeyPressListeners(key, scancode, mods);
				} else if (action == GLFW.GLFW_RELEASE) {
					GLFWWindow.this.invokeKeyReleaseListeners(key, scancode, mods);
				}
			}
		};
		GLFW.glfwSetKeyCallback(this._ptr, this._callback_key);
	}

	private void initMouseEvents() {
		this._callback_scroll = new GLFWScrollCallback() {

			@Override
			public void invoke(long window, double xpos, double ypos) {
				GLFWWindow.this.invokeScrollListeners(xpos, ypos);
			}
		};

		this._callback_cursor_pos = new GLFWCursorPosCallback() {

			@Override
			public void invoke(long window, double xpos, double ypos) {
				GLFWWindow.this.invokeCursorPosListeners(xpos, ypos);
			}
		};

		this._callback_mouse_button = new GLFWMouseButtonCallback() {

			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					GLFWWindow.this.invokeMousePressListeners(button, mods);
				} else {
					GLFWWindow.this.invokeMouseReleaseListeners(button, mods);
				}
			}
		};
		GLFW.glfwSetScrollCallback(this._ptr, this._callback_scroll);
		GLFW.glfwSetCursorPosCallback(this._ptr, this._callback_cursor_pos);
		GLFW.glfwSetMouseButtonCallback(this._ptr, this._callback_mouse_button);
	}

	/** in pixels */
	public void setScreenPosition(int px, int py) {
		GLFW.glfwSetWindowPos(this.getPointer(), px, py);
	}

	/** enable or disable cursor */
	public void setCursor(boolean enable) {
		int value = (enable) ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_DISABLED;
		GLFW.glfwSetInputMode(this._ptr, GLFW.GLFW_CURSOR, value);
	}

	public boolean isCursorEnabled() {
		return (GLFW.glfwGetInputMode(this._ptr, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED);
	}

	/** center the mouse on the screen */
	public void setCursorCenter() {
		this.setCursorPos(this._width / 2, this._height / 2);
	}

	/** set cursor position */
	public void setCursorPos(double x, double y) {
		GLFW.glfwSetCursorPos(this._ptr, x, y);
	}

	/** set window title */
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(this._ptr, title);
	}

	/** enable or disable vsync (0 == disable, 1 == enable) */
	public void swapInterval(int v) {
		GLFW.glfwSwapInterval(v);
	}

	/** stop the window */
	@Override
	public void delete() {
		GLFW.glfwDestroyWindow(this._ptr);
	}

	public float getAspectRatio() {
		return (this._width / (float) this._height);
	}

	public void resize(int width, int height) {
		GL11.glViewport(0, 0, width, height);
		this._width = width;
		this._height = height;
	}

	public int getWidth() {
		return (this._width);
	}

	public int getHeight() {
		return (this._height);
	}

	public void setClearColor(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
	}

	/** should be call before rendering */
	public void clearScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void update() {
		GLFW.glfwGetCursorPos(this._ptr, this._bufferX, this._bufferY);
		this._mouseX = this._bufferX.get();
		this._mouseY = this._bufferY.get();
		this._bufferX.clear();
		this._bufferY.clear();

		if (this._mouseX < 0 || this._mouseX >= this._width || this._mouseY < 0 || this._mouseY >= this._height) {
			if (this._mouse_is_in) {
				this.onMouseExit();
			}
			this._mouse_is_in = false;
		} else {
			if (!this._mouse_is_in) {
				this.onMouseEntered();
			}
			this._mouse_is_in = true;
		}
	}

	public void onMouseEntered() {
		for (GLFWListenerMouseEnter listener : this._listeners_mouse_enter) {
			listener.invokeMouseEnter(this, this.isCursorEnabled(), this._mouseX, this._mouseY);
		}
	}

	public void onMouseExit() {
		for (GLFWListenerMouseExit listener : this._listeners_mouse_exit) {
			listener.invokeMouseExit(this, this.isCursorEnabled(), this._mouseX, this._mouseY);
		}
	}

	/** should be call after rendering */
	public void flushScreen() {
		GLFW.glfwSwapBuffers(this._ptr);
		GLFW.glfwPollEvents();

		this._prev_mouseX = this._mouseX;
		this._prev_mouseY = this._mouseY;

		this.updateFpsCounter();

		GLH.glhCheckError("GLFWWindow.flushScreen()");
	}

	private void updateFpsCounter() {
		if (System.currentTimeMillis() - this._prev_frame >= 1000) {
			this._fps = this._fps_counter;
			this._fps_counter = 0;
			this._prev_frame = System.currentTimeMillis();
		} else {
			this._fps_counter++;
		}

		this._frames++;
	}

	/** return true if glfw was close-requested */
	public boolean shouldClose() {
		return (GLFW.glfwWindowShouldClose(this._ptr));
	}

	/** get window GLFW pointer */
	public long getPointer() {
		return (this._ptr);
	}

	/** get mouse X coordinate */
	public double getMouseX() {
		return (this._mouseX);
	}

	/** get mouse Y coordinate */
	public double getMouseY() {
		return (this._mouseY);
	}

	public double getPrevMouseX() {
		return (this._prev_mouseX);
	}

	public double getPrevMouseY() {
		return (this._prev_mouseY);
	}

	/**
	 * return mouse delta X since last cursor move was made and current position
	 */
	public double getMouseDX() {
		return (this._mouseX - this._prev_mouseX);
	}

	/**
	 * return mouse delta Y since last cursor move was made and current position
	 */
	public double getMouseDY() {
		return (this._mouseY - this._prev_mouseY);
	}

	/** get total frames flushed */
	public long getTotalFramesFlushed() {
		return (this._frames);
	}

	public boolean isKeyPressed(int key) {
		return (GLFW.glfwGetKey(this.getPointer(), key) == GLFW.GLFW_PRESS);
	}

	public boolean isKeyReleased(int key) {
		return (GLFW.glfwGetKey(this.getPointer(), key) == GLFW.GLFW_RELEASE);
	}

	public boolean isMousePressed(int button) {
		return (GLFW.glfwGetMouseButton(this.getPointer(), button) == GLFW.GLFW_PRESS);
	}

	public boolean isMouseRightPressed() {
		return (GLFW.glfwGetMouseButton(this.getPointer(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS);
	}

	public boolean isMouseLeftPressed() {
		return (GLFW.glfwGetMouseButton(this.getPointer(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
	}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
//		if (key == GLFW.GLFW_KEY_ESCAPE) {
//			this.close();
//		}
		
		if (key == GLFW.GLFW_KEY_N) {
			this.setCursor(!this.isCursorEnabled());
		}
	}

	public int getFPS() {
		return (this._fps);
	}

	public void setSize(int width, int height) {
		GLFW.glfwSetWindowSize(this.getPointer(), width, height);
	}

	public void close() {
		GLFW.glfwSetWindowShouldClose(this._ptr, true);
	}

	public boolean hasFocus() {
		return (this._focus);
	}

	public void focus(boolean focus) {
		if (focus) {
			GLFW.glfwFocusWindow(this.getPointer());
		}
	}
}
