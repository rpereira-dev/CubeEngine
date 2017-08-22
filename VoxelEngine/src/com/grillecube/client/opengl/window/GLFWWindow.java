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

package com.grillecube.client.opengl.window;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLObject;
import com.grillecube.client.opengl.object.ImageUtils;
import com.grillecube.client.opengl.window.event.GLFWEvent;
import com.grillecube.client.opengl.window.event.GLFWEventChar;
import com.grillecube.client.opengl.window.event.GLFWEventKeyPress;
import com.grillecube.client.opengl.window.event.GLFWEventKeyRelease;
import com.grillecube.client.opengl.window.event.GLFWEventMouseCursor;
import com.grillecube.client.opengl.window.event.GLFWEventMouseEnter;
import com.grillecube.client.opengl.window.event.GLFWEventMouseExit;
import com.grillecube.client.opengl.window.event.GLFWEventMousePress;
import com.grillecube.client.opengl.window.event.GLFWEventMouseRelease;
import com.grillecube.client.opengl.window.event.GLFWEventMouseScroll;
import com.grillecube.client.opengl.window.event.GLFWEventWindowResize;
import com.grillecube.client.opengl.window.event.GLFWListener;

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
public class GLFWWindow implements GLObject {
	public static final int OS_LINUX = 0;
	public static final int OS_WINDOWS = 1;
	public static final int OS_MAC = 2;

	public static final String OS_NAME = System.getProperty("os.name");
	private static final String _OS_NAME = OS_NAME.toLowerCase();
	public static final int OS = _OS_NAME.contains("win") ? OS_WINDOWS : _OS_NAME.contains("mac") ? OS_MAC : OS_LINUX;

	/** window pointer */
	private long windowPtr;

	/** window size (in pixels) */
	private int width;
	private int height;
	private float aspectRatio;

	private double mouseX;
	private double mouseY;
	private double prevmouseX;
	private double prevmouseY;

	/** frames data */
	private long prevFrame; // previous frame timer
	private long frames; // total frames flushed
	private int fps_counter; // frame per second counter
	private int fps; // last frame per second calculated

	/** events handler */
	private HashMap<Class<? extends GLFWEvent>, ArrayList<GLFWListener<? extends GLFWEvent>>> listeners;

	/** events callback for garbage collector */
	private GLFWScrollCallback _callback_scroll;
	private GLFWCursorPosCallback _callback_cursor_pos;
	private GLFWMouseButtonCallback _callback_mouse_button;
	private GLFWKeyCallback _callback_key;
	private GLFWCharCallback _callback_char;
	private GLFWWindowSizeCallback _resize_callback;
	private GLFWWindowFocusCallback _focus_callback;

	private static final int STATE_HOVERED = (1 << 0);
	private static final int STATE_FOCUSED = (1 << 1);
	private int state;

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

		this.windowPtr = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if (this.windowPtr == 0) {
			System.err.println("Couldnt create glfw window");
			return;
		}

		this.width = width;
		this.height = height;
		this.aspectRatio = width / (float) height;

		this.mouseX = width / 2;
		this.mouseY = height / 2;

		this.prevmouseX = width / 2;
		this.prevmouseY = height / 2;

		this.prevFrame = System.currentTimeMillis();
		this.frames = 0;
		this.fps_counter = 0;
		this.fps = 0;

		this.initEvents();
	}

	private final void initEvents() {
		this.initWindowEvents();
		this.initKeyEvents();
		this.initMouseEvents();
		this.addListener(new GLFWListener<GLFWEventKeyPress>() {
			@Override
			public void invoke(GLFWEventKeyPress event) {
				if (event.getKey() == GLFW.GLFW_KEY_ESCAPE) {
					close();
				}
				if (event.getKey() == GLFW.GLFW_KEY_N) {
					setCursor(!isCursorEnabled());
				}
			}
		});
	}

	private final void initWindowEvents() {
		this._resize_callback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				resize(width, height);
				GLFWWindow.this.invokeEvent(new GLFWEventWindowResize(GLFWWindow.this));
			}
		};
		GLFW.glfwSetWindowSizeCallback(this.windowPtr, this._resize_callback);

		this._focus_callback = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focused) {
				setState(STATE_FOCUSED, focused);
			}
		};
		GLFW.glfwSetWindowFocusCallback(this.windowPtr, this._focus_callback);

	}

	/** invoke a glfw event */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void invokeEvent(GLFWEvent glfwEvent) {
		if (this.listeners == null) {
			return;
		}
		ArrayList<GLFWListener<?>> listeners = this.listeners.get(glfwEvent.getClass());
		if (listeners == null) {
			return;
		}
		for (GLFWListener listener : listeners) {
			listener.invoke(glfwEvent);
		}
	}

	/** add listeners */
	public final void addListener(GLFWListener<? extends GLFWEvent> listener) {
		if (this.listeners == null) {
			this.listeners = new HashMap<Class<? extends GLFWEvent>, ArrayList<GLFWListener<? extends GLFWEvent>>>();
		}
		ArrayList<GLFWListener<? extends GLFWEvent>> lst = this.listeners.get(listener.getEventClass());
		if (lst == null) {
			lst = new ArrayList<GLFWListener<? extends GLFWEvent>>();
			this.listeners.put(listener.getEventClass(), lst);
		}
		lst.add(listener);
	}

	/** remove listeners */
	public final void removeListener(GLFWListener<? extends GLFWEvent> listener) {
		if (this.listeners == null) {
			return;
		}
		ArrayList<GLFWListener<? extends GLFWEvent>> lst = this.listeners.get(listener.getEventClass());
		if (lst == null) {
			return;
		}
		lst.remove(listener);
		if (lst.size() == 0) {
			this.listeners.remove(listener.getEventClass());
		}
		if (this.listeners.size() == 0) {
			this.listeners = null;
		}
	}

	private final void initKeyEvents() {

		this._callback_char = new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				GLFWWindow.this.invokeEvent(new GLFWEventChar(GLFWWindow.this, codepoint));
			}
		};
		GLFW.glfwSetCharCallback(this.windowPtr, this._callback_char);

		this._callback_key = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					GLFWWindow.this.invokeEvent(new GLFWEventKeyPress(GLFWWindow.this, key, scancode, mods));
				} else if (action == GLFW.GLFW_RELEASE) {
					GLFWWindow.this.invokeEvent(new GLFWEventKeyRelease(GLFWWindow.this, key, scancode, mods));
				}
			}
		};
		GLFW.glfwSetKeyCallback(this.windowPtr, this._callback_key);
	}

	private final void initMouseEvents() {
		this._callback_scroll = new GLFWScrollCallback() {

			@Override
			public void invoke(long window, double xpos, double ypos) {
				GLFWWindow.this.invokeEvent(new GLFWEventMouseScroll(GLFWWindow.this, xpos, ypos));
			}
		};

		this._callback_cursor_pos = new GLFWCursorPosCallback() {

			@Override
			public void invoke(long window, double xpos, double ypos) {
				GLFWWindow glfWWindow = GLFWWindow.this;
				glfWWindow.prevmouseX = glfWWindow.mouseX;
				glfWWindow.prevmouseY = glfWWindow.mouseY;
				glfWWindow.mouseX = xpos;
				glfWWindow.mouseY = ypos;
				GLFWWindow.this.invokeEvent(new GLFWEventMouseCursor(glfWWindow));
			}
		};

		this._callback_mouse_button = new GLFWMouseButtonCallback() {

			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					GLFWWindow.this.invokeEvent(new GLFWEventMousePress(GLFWWindow.this, button, mods));
				} else {
					GLFWWindow.this.invokeEvent(new GLFWEventMouseRelease(GLFWWindow.this, button, mods));
				}
			}
		};
		GLFW.glfwSetScrollCallback(this.windowPtr, this._callback_scroll);
		GLFW.glfwSetCursorPosCallback(this.windowPtr, this._callback_cursor_pos);
		GLFW.glfwSetMouseButtonCallback(this.windowPtr, this._callback_mouse_button);
	}

	/** in pixels */
	public void setScreenPosition(int px, int py) {
		GLFW.glfwSetWindowPos(this.getPointer(), px, py);
	}

	/** enable or disable cursor */
	public void setCursor(boolean enable) {
		int value = (enable) ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_DISABLED;
		GLFW.glfwSetInputMode(this.windowPtr, GLFW.GLFW_CURSOR, value);
	}

	public boolean isCursorEnabled() {
		return (GLFW.glfwGetInputMode(this.windowPtr, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED);
	}

	/** center the mouse on the screen */
	public void setCursorCenter() {
		this.setCursorPos(this.width / 2, this.height / 2);
	}

	/** set cursor position */
	public void setCursorPos(double x, double y) {
		GLFW.glfwSetCursorPos(this.windowPtr, x, y);
	}

	/** set window title */
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(this.windowPtr, title);
	}

	/** enable or disable vsync (0 == disable, 1 == enable) */
	public void swapInterval(int v) {
		GLFW.glfwSwapInterval(v);
	}

	/** stop the window */
	@Override
	public void delete() {
		GLFW.glfwDestroyWindow(this.windowPtr);
	}

	public float getAspectRatio() {
		return (this.aspectRatio);
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		this.aspectRatio = this.width / (float) this.height;
	}

	public final int getWidth() {
		return (this.width);
	}

	public final int getHeight() {
		return (this.height);
	}

	public void setClearColor(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
	}

	/** should be call before rendering */
	public void clearScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	private void setHovered(boolean b) {
		this.setState(STATE_HOVERED, b);
	}

	public final boolean isHovered() {
		return (this.hasState(STATE_HOVERED));
	}

	/** should be call after rendering */
	public final void flushScreen() {
		GLFW.glfwSwapBuffers(this.windowPtr);
		this.updateFpsCounter();
		GLH.glhCheckError("GLFWWindow.flushScreen()");
	}

	/** processed polled events */
	public final void pollEvents() {
		GLFW.glfwPollEvents();

		boolean mouseIn = this.getMouseX() >= 0 && this.getMouseX() <= this.getWidth() && this.getMouseY() >= 0
				&& this.getMouseY() <= this.getHeight();
		if (mouseIn && !this.isHovered()) {
			this.setHovered(true);
			this.invokeEvent(new GLFWEventMouseEnter(this));
		} else if (!mouseIn && this.isHovered()) {
			this.setHovered(false);
			this.invokeEvent(new GLFWEventMouseExit(this));
		}
	}

	private final void updateFpsCounter() {
		if (System.currentTimeMillis() - this.prevFrame >= 1000) {
			this.fps = this.fps_counter;
			this.fps_counter = 0;
			this.prevFrame = System.currentTimeMillis();
		} else {
			this.fps_counter++;
		}

		this.frames++;
	}

	/** return true if glfw was close-requested */
	public final boolean shouldClose() {
		return (GLFW.glfwWindowShouldClose(this.windowPtr));
	}

	/** get window GLFW pointer */
	public final long getPointer() {
		return (this.windowPtr);
	}

	/** get mouse X coordinate */
	public final double getMouseX() {
		return (this.mouseX);
	}

	/** get mouse Y coordinate */
	public final double getMouseY() {
		return (this.mouseY);
	}

	public final double getPrevMouseX() {
		return (this.prevmouseX);
	}

	public final double getPrevMouseY() {
		return (this.prevmouseY);
	}

	/**
	 * return mouse delta X since last cursor move was made and current position
	 */
	public final double getMouseDX() {
		return (this.mouseX - this.prevmouseX);
	}

	/**
	 * return mouse delta Y since last cursor move was made and current position
	 */
	public final double getMouseDY() {
		return (this.mouseY - this.prevmouseY);
	}

	/** get total frames flushed */
	public final long getTotalFramesFlushed() {
		return (this.frames);
	}

	public final boolean isKeyPressed(int key) {
		return (GLFW.glfwGetKey(this.getPointer(), key) == GLFW.GLFW_PRESS);
	}

	public final boolean isKeyReleased(int key) {
		return (GLFW.glfwGetKey(this.getPointer(), key) == GLFW.GLFW_RELEASE);
	}

	public final boolean isMousePressed(int button) {
		return (GLFW.glfwGetMouseButton(this.getPointer(), button) == GLFW.GLFW_PRESS);
	}

	public final boolean isMouseRightPressed() {
		return (GLFW.glfwGetMouseButton(this.getPointer(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS);
	}

	public final boolean isMouseLeftPressed() {
		return (GLFW.glfwGetMouseButton(this.getPointer(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
	}

	public final int getFPS() {
		return (this.fps);
	}

	public final void setSize(int width, int height) {
		GLFW.glfwSetWindowSize(this.getPointer(), width, height);
	}

	public final void close() {
		GLFW.glfwSetWindowShouldClose(this.windowPtr, true);
	}

	public final boolean hasFocus() {
		return (this.hasState(STATE_FOCUSED));
	}

	public final void focus(boolean focus) {
		if (focus) {
			GLFW.glfwFocusWindow(this.getPointer());
		}
	}

	/** set the icon of this window */
	public final void setIcon(File file) {
		this.setIcon(file.getAbsolutePath());
	}

	/** set the icon of this window */
	public final void setIcon(String imagePath) {
		BufferedImage bufferedImage = ImageUtils.readImage(imagePath);
		int imwidth = bufferedImage.getWidth();
		int imheight = bufferedImage.getHeight();
		GLFWImage image = GLFWImage.malloc();
		ByteBuffer pixels = BufferUtils.createByteBuffer(imwidth * imheight * 4);
		for (int y = 0; y < imheight; y++) {
			for (int x = 0; x < imwidth; x++) {
				Color color = new Color(bufferedImage.getRGB(x, y), true);
				pixels.put((byte) color.getRed());
				pixels.put((byte) color.getGreen());
				pixels.put((byte) color.getBlue());
				pixels.put((byte) color.getAlpha());
			}
		}
		pixels.flip();
		image.set(imwidth, imheight, pixels);

		GLFWImage.Buffer images = GLFWImage.malloc(1);
		images.put(0, image);

		GLFW.glfwSetWindowIcon(this.windowPtr, images);

		images.free();
		image.free();
	}

	private final boolean hasState(int state) {
		return ((this.state & state) == state);
	}

	private final void setState(int state) {
		this.state = this.state | state;
	}

	private final void setState(int state, boolean enabled) {
		if (enabled) {
			this.setState(state);
		} else {
			this.unsetState(state);
		}
	}

	private final void unsetState(int state) {
		this.state = this.state & ~state;
	}

	@SuppressWarnings("unused")
	private final void swapState(int state) {
		this.state = this.state ^ state;
	}

}
