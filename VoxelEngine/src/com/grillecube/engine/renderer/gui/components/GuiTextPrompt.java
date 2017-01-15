package com.grillecube.engine.renderer.gui.components;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.grillecube.engine.opengl.GLFWListenerChar;
import com.grillecube.engine.opengl.GLFWListenerKeyPress;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.renderer.gui.GuiListenerMouseLeftPress;
import com.grillecube.engine.renderer.gui.GuiParameter;
import com.grillecube.engine.renderer.gui.font.FontChar;

public class GuiTextPrompt extends GuiLabel implements GLFWListenerChar, GLFWListenerKeyPress {

	/**
	 * a parameter which make the text not overflow the box
	 */
	public static final GuiParameter<GuiTextPrompt> PARAM_TEXT_DONT_OVERFLOW = new GuiParameter<GuiTextPrompt>() {

		@Override
		public void run(GuiTextPrompt gui) {
			while (gui.getFontModel().getTextWidth() >= gui.getWidth()) {
				gui.getFontModel().setText(gui.getFontModel().getText().substring(1));
			}
		}
	};

	private static final String DEFAULT_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 123456789\n";
	private static final int CURSOR_MS = 500;
	private String _text;
	private String _hint;
	private int _cursor;
	private boolean _show_cursor;
	private long _last_cursor_update;
	private long _cursor_timer;
	private String _charset;
	/** hint color */
	private float _r, _g, _b, _a;
	private float _rh, _gh, _bh, _ah;
	private int _max_chars;

	private static final GuiListenerMouseLeftPress<GuiTextPrompt> LISTENER = new GuiListenerMouseLeftPress<GuiTextPrompt>() {
		@Override
		public void invokeMouseLeftPress(GuiTextPrompt gui, double mousex, double mousey) {
			gui.setFocusRequest(true);
		}
	};

	public GuiTextPrompt() {
		super();
		this.addListener(LISTENER);
		this._charset = new String(DEFAULT_CHARSET);
		this._max_chars = Integer.MAX_VALUE;
	}

	public String getCharset() {
		return (this._charset);
	}

	public void setCharset(String str) {
		this._charset = str;
	}

	public void addCharToCharset(char c) {
		this._charset = this._charset + c;
	}

	@Override
	public void onAdded(GuiView view) {
		view.getGLFWWindow().addCharListener(this);
		view.getGLFWWindow().addKeyPressListener(this);
		super.onAdded(view);
	}

	@Override
	public void onRemoved(GuiView view) {
		view.getGLFWWindow().removeCharListener(this);
		view.getGLFWWindow().removeKeyPressListener(this);
		super.onRemoved(view);
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
		if (this._text.length() < this._max_chars && this._charset.indexOf(c) >= 0) {
			if (this.getText() != null) {
				this.setText(this.getText().substring(0, this._cursor) + c
						+ this.getText().substring(this._cursor, this.getText().length()));
			} else {
				this.setText("" + c);
			}
			this._cursor++;
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
				this._cursor--;
			} else if (key == GLFW.GLFW_KEY_DELETE && this.getCursor() < text.length()) {
				this.setText(text.substring(0, this.getCursor()) + text.substring(this.getCursor() + 1, text.length()));
			} else if (key == GLFW.GLFW_KEY_LEFT && this._cursor > 0) {
				--this._cursor;
			} else if (key == GLFW.GLFW_KEY_RIGHT && this._cursor < this.getText().length()) {
				++this._cursor;
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
		this._last_cursor_update = System.currentTimeMillis();
		this._cursor_timer = value ? 0 : CURSOR_MS;
	}

	private boolean showCursor() {
		return (this._show_cursor);
	}

	private void updateCursor() {
		long now = System.currentTimeMillis();
		this._cursor_timer = (this._cursor_timer + (now - this._last_cursor_update)) % (CURSOR_MS * 2);
		this._last_cursor_update = now;
		this._show_cursor = this._cursor_timer < CURSOR_MS;

		if (this.getText() != null) {
			if (this.showCursor()) {
				this.setFontModelText(this.getText().substring(0, this._cursor) + "|"
						+ this.getText().substring(this._cursor, this.getText().length()));
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
		return (this._cursor);
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		if (this.hasFocus()) {
			this.updateCursor();
		}
	}

	public void setHintAsText() {
		super.setFontColor(this._rh, this._gh, this._bh, this._ah);
		this.setFontModelText(this.getHint());
	}

	@Override
	public void setText(String str) {
		this._text = str;
	}

	/** return the text held by this prompt */
	@Override
	public String getText() {
		return (this._text);
	}

	/** get the hint */
	public String getHint() {
		return (this._hint);
	}

	/** set the hint text */
	public void setHint(String str) {
		this._hint = str;
	}

	/** set text color of the hint */
	public void setHintColor(float r, float g, float b, float a) {
		this._rh = r;
		this._gh = g;
		this._bh = b;
		this._ah = a;
	}

	@Override
	public void setFontColor(float r, float g, float b, float a) {
		this._r = r;
		this._g = g;
		this._b = b;
		this._a = a;
		super.setFontColor(r, g, b, a);
	}

	@Override
	protected void onFocusGained() {
		if (this.getText() == null) {
			this.setText("");
		}
		super.setFontColor(this._r, this._g, this._b, this._a);
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
		this._max_chars = max;
	}
}
