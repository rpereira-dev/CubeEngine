package com.grillecube.client.renderer.gui.components;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.GLFWListenerChar;
import com.grillecube.client.opengl.GLFWListenerKeyPress;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.font.FontChar;
import com.grillecube.client.renderer.gui.listeners.GuiListenerMouseLeftPress;

public class GuiTextPrompt extends GuiLabel implements GLFWListenerChar, GLFWListenerKeyPress {

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

	private static final GuiListenerMouseLeftPress<GuiTextPrompt> LISTENER = new GuiListenerMouseLeftPress<GuiTextPrompt>() {
		@Override
		public void invokeMouseLeftPress(GuiTextPrompt gui, double mousex, double mousey) {
			gui.setFocusRequest(true);
		}
	};

	public GuiTextPrompt() {
		super();
		this.addListener(LISTENER);
		this.charset = new String(DEFAULT_CHARSET);
		this.maxChars = Integer.MAX_VALUE;
		this.setHintColor(0.5f, 0.5f, 0.5f, 0.5f);
	}

	public String getCharset() {
		return (this.charset);
	}

	public void setCharset(String str) {
		this.charset = str;
	}

	public void addCharToCharset(char c) {
		this.charset = this.charset + c;
	}

	@Override
	public void invokeChar(GLFWWindow glfwWindow, int codepoint) {

		if (!this.hasFocus()) {
			return;
		}

		char c = (char) codepoint;
		this.addChar(c);
		this.showCursor(true);

	}

	private void addChar(char c) {
		if (this.text.length() < this.maxChars && this.charset.indexOf(c) >= 0) {
			if (this.getText() != null) {
				this.setText(this.getText().substring(0, this.cursor) + c
						+ this.getText().substring(this.cursor, this.getText().length()));
			} else {
				this.setText("" + c);
			}
			this.cursor++;
		}
	}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {

		if (!this.hasFocus()) {
			return;
		}

		String text = this.getText();
		if (text != null) {
			if (key == GLFW.GLFW_KEY_BACKSPACE && this.getCursor() > 0) {
				this.setText(text.substring(0, this.getCursor() - 1) + text.substring(this.getCursor(), text.length()));
				this.cursor--;
			} else if (key == GLFW.GLFW_KEY_DELETE && this.getCursor() < text.length()) {
				this.setText(text.substring(0, this.getCursor()) + text.substring(this.getCursor() + 1, text.length()));
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

		if (this.getText() != null) {
			if (this.showCursor()) {
				this.setFontModelText(this.getText().substring(0, this.cursor) + "|"
						+ this.getText().substring(this.cursor, this.getText().length()));
			} else {
				this.setFontModelText(this.getText());
			}
		}
	}

	private void setFontModelText(String str) {
		if (this.getFontModel().getText().equals(str)) {
			return;
		}
		ArrayList<FontChar> chars = super.getFontModel().getFontChar();
		for (FontChar ch : chars) {
			if (ch.ascii == '|') {
				ch.xadvance = 0;
				ch.xoffset = 0;
			}
		}
		this.getFontModel().setText(str);
		this.runParameters();
	}

	private int getCursor() {
		return (this.cursor);
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		if (this.hasFocus() || true) {
			this.updateCursor();
		}
	}

	public void setHintAsText() {
		super.setFontColor(this.rh, this.gh, this.bh, this.ah);
		this.setFontModelText(this.getHint());
	}

	@Override
	protected void onTextChanged(String str) {
		this.text = str;
	}

	/** return the text held by this prompt */
	@Override
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

	/** set text color of the hint */
	public final void setHintColor(float r, float g, float b, float a) {
		this.rh = r;
		this.gh = g;
		this.bh = b;
		this.ah = a;
	}

	public final void setPromptColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	@Override
	protected void onFocusGained() {
		if (this.getText() == null) {
			this.setText("");
		}
		super.setFontColor(this.r, this.g, this.b, this.a);
	}

	@Override
	protected void onFocusLost() {
		if (this.getText() != null && this.getText().length() > 0) {
			this.setFontModelText(this.getText());
		} else if (this.getHint() != null) {
			this.setHintAsText();
		}
	}

	/** set the maximum chars this prompt can holds */
	public void setMaxChars(int max) {
		this.maxChars = max;
	}
}
