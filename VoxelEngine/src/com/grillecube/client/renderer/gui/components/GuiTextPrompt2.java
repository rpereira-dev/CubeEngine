package com.grillecube.client.renderer.gui.components;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.GLFWListenerChar;
import com.grillecube.client.opengl.GLFWListenerKeyPress;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseLeftPress;
import com.grillecube.common.maths.Vector4f;

public class GuiTextPrompt2 extends GuiText implements GLFWListenerChar, GLFWListenerKeyPress {

	private static final String DEFAULT_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 123456789\n";
	private static final int CURSOR_MS = 500;
	private String text;
	private String hint;
	private int cursor;
	private boolean showCursor;
	private long lastCursorUpdate;
	private long cursorTimer;
	private String charset;
	/** hint color */
	private float r, g, b, a;
	private float rh, gh, bh, ah;
	private int maxChars;
	private GLFWWindow glfwWindow;

	private static final GuiListenerMouseLeftPress<GuiTextPrompt2> FOCUS_LISTENER = new GuiListenerMouseLeftPress<GuiTextPrompt2>() {
		@Override
		public void invokeMouseLeftPress(GuiTextPrompt2 gui, double mousex, double mousey) {
			gui.setFocusRequest(true);
		}
	};

	public GuiTextPrompt2() {
		super();
		this.addListener(FOCUS_LISTENER);
		this.charset = new String(DEFAULT_CHARSET);
		this.maxChars = Integer.MAX_VALUE;
		this.setHintColor(0.5f, 0.5f, 0.5f, 0.5f);
		this.setHint("...");
		this.setTextColor(Gui.COLOR_DARK_MAGENTA);
		this.setText(null);
	}

	public final String getCharset() {
		return (this.charset);
	}

	public final void setCharset(String str) {
		this.charset = str;
	}

	public final void addCharToCharset(char c) {
		this.charset = this.charset + c;
	}

	@Override
	public void invokeChar(GLFWWindow glfwWindow, int codepoint) {

		// if (!this.hasFocus()) { //TODO
		// return;
		// }

		char c = (char) codepoint;
		this.addChar(c);
		this.showCursor(true);
	}

	private final void addChar(char c) {
		String text = this.getText() == null ? "" : this.getText();
		if (text.length() < this.maxChars && this.charset.indexOf(c) >= 0) {
			if (this.cursor > text.length()) {
				this.cursor = text.length();
			}
			String begin = text.substring(0, this.cursor);
			String end = text.substring(this.cursor, text.length());
			this.setText(begin + c + end, this.cursor + 1);
		}
	}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {

		// if (!this.hasFocus()) { //TODO
		// return;
		// }

		String text = this.getText();
		if (text != null) {
			if (key == GLFW.GLFW_KEY_BACKSPACE && this.getCursor() > 0) {
				this.setText(text.substring(0, this.getCursor() - 1) + text.substring(this.getCursor(), text.length()),
						this.cursor - 1);
			} else if (key == GLFW.GLFW_KEY_DELETE && this.getCursor() < text.length()) {
				this.setText(text.substring(0, this.getCursor()) + text.substring(this.getCursor() + 1, text.length()),
						this.cursor);
			} else if (key == GLFW.GLFW_KEY_LEFT && this.cursor > 0) {
				--this.cursor;
			} else if (key == GLFW.GLFW_KEY_RIGHT && this.cursor < this.getText().length()) {
				++this.cursor;
			}
		}

		if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
			if ((mods & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT) {
				this.addChar('\n');
			} else {
				this.setFocusRequest(false);
			}
		}

		this.showCursor(true);
	}

	private void showCursor(boolean value) {
		this.lastCursorUpdate = System.currentTimeMillis();
		this.cursorTimer = value ? 0 : CURSOR_MS;
	}

	private boolean showCursor() {
		return (this.showCursor);
	}

	private void updateCursor() {
		long now = System.currentTimeMillis();
		this.cursorTimer = (this.cursorTimer + (now - this.lastCursorUpdate)) % (CURSOR_MS * 2);
		this.lastCursorUpdate = now;
		this.showCursor = this.cursorTimer < CURSOR_MS;
	}

	private int getCursor() {
		return (this.cursor);
	}

	// public final void setText(String text) {
	// this.setText(text, text != null ? text.length() : 0);
	// }

	public void setText(String text, int cur) {
		if (text == null || text.length() == 0) {
			super.setText(this.hint);
			super.setFontColor(this.rh, this.gh, this.bh, this.ah);
			this.text = null;
			this.cursor = 0;
		} else {
			super.setText(text);
			super.setFontColor(this.r, this.g, this.b, this.a);
			this.text = text;
			this.cursor = cur;
		}
	}

	/** return the text held by this prompt */
	public final String getText() {
		return (this.text);
	}

	/** get the hint */
	public final String getHint() {
		return (this.hint);
	}

	/** set the hint text */
	public final void setHint(String str) {
		this.hint = str;
	}

	public final void setHintColor(Vector4f color) {
		this.setHintColor(color.x, color.y, color.z, color.w);
	}

	/** set text color of the hint */
	public final void setHintColor(float r, float g, float b, float a) {
		this.rh = r;
		this.gh = g;
		this.bh = b;
		this.ah = a;
	}

	public final void setTextColor(Vector4f color) {
		this.setTextColor(color.x, color.y, color.z, color.w);
	}

	public final void setTextColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/** set the maximum chars this prompt can holds */
	public void setMaxChars(int max) {
		this.maxChars = max;
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		this.updateCursor();
	}

	private final void addListeners(GLFWWindow glfwWindow) {
		this.glfwWindow = glfwWindow;
		if (this.glfwWindow != null) {
			this.glfwWindow.addCharListener(this);
			this.glfwWindow.addKeyPressListener(this);
		}
	}

	private final void removeListeners() {
		this.glfwWindow.removeCharListener(this);
		this.glfwWindow.removeKeyPressListener(this);
	}

	@Override
	public void onAddedTo(GuiRenderer guiRenderer) {
		this.addListeners(guiRenderer.getParent().getGLFWWindow());
	}

	@Override
	public void onRemovedFrom(GuiRenderer guiRenderer) {
		this.removeListeners();
	}

	@Override
	public void onAddedTo(Gui gui) {
		this.addListeners(GLH.glhGetWindow());
	}

	@Override
	public void onRemovedFrom(Gui gui) {
		this.removeListeners();
	}
}
