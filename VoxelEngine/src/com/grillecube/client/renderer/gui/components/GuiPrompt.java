package com.grillecube.client.renderer.gui.components;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.gui.event.GuiEventChar;
import com.grillecube.client.renderer.gui.event.GuiEventGainFocus;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventLooseFocus;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.client.renderer.gui.font.FontFile;
import com.grillecube.common.maths.Vector4f;

public class GuiPrompt extends GuiLabel {

	private static final GuiListener<GuiEventChar<GuiPrompt>> CHAR_LISTENER = new GuiListener<GuiEventChar<GuiPrompt>>() {
		@Override
		public void invoke(GuiEventChar<GuiPrompt> event) {
			event.getGui().onChar(event.getGLFWWindow(), event.getCharacter());
		}
	};

	private static final GuiListener<GuiEventKeyPress<GuiPrompt>> KEY_PRESS_LISTENER = new GuiListener<GuiEventKeyPress<GuiPrompt>>() {
		@Override
		public void invoke(GuiEventKeyPress<GuiPrompt> event) {
			event.getGui().onKeyPressed(event.getGLFWWindow(), event.getKey(), event.getScancode(), event.getMods());
		}
	};

	private static final GuiListener<GuiEventGainFocus<GuiPrompt>> GAIN_FOCUS_LISTENER = new GuiListener<GuiEventGainFocus<GuiPrompt>>() {
		@Override
		public void invoke(GuiEventGainFocus<GuiPrompt> event) {
			event.getGui().onFocusGained();
		}
	};

	private static final GuiListener<GuiEventLooseFocus<GuiPrompt>> LOST_FOCUS_LISTENER = new GuiListener<GuiEventLooseFocus<GuiPrompt>>() {
		@Override
		public void invoke(GuiEventLooseFocus<GuiPrompt> event) {
			event.getGui().onFocusLost();
		}
	};

	private static final String DEFAULT_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 123456789\n";
	private static final int CURSOR_MS = 500;

	/** the text held by the prompt */
	private String heldText;
	private float r, g, b, a;

	/** max number of character to be hold */
	private int maxChars;

	/** charset (allowed chars) */
	private String charset;
	private float rh, gh, bh, ah;

	/** hint text to be shown when prompt is empty */
	private String hintText;

	/** cursor position */
	private int cursor;

	/** cursor timers */
	private long lastCursorUpdate;
	private long cursorTimer;

	public GuiPrompt() {
		super();
		this.addListener(CHAR_LISTENER);
		this.addListener(KEY_PRESS_LISTENER);
		this.addListener(LOST_FOCUS_LISTENER);
		this.addListener(LOST_FOCUS_LISTENER);
		this.addListener(GAIN_FOCUS_LISTENER);
		this.addListener(ON_PRESS_FOCUS_LISTENER);

		this.charset = new String(DEFAULT_CHARSET);
		this.maxChars = Integer.MAX_VALUE;
		this.setHintColor(0.5f, 0.5f, 0.5f, 0.5f);
		this.setHeldTextColor(Gui.COLOR_BLUE);
	}

	protected void onFocusLost() {
		this.updateVisibleText();
	}

	protected void onFocusGained() {
		this.tickCursor();
		this.updateVisibleText();
	}

	protected void onChar(GLFWWindow glfwWindow, char character) {
		this.addChar(character);
	}

	protected void onKeyPressed(GLFWWindow glfwWindow, int key, int scancode, int mods) {

		String text = this.getHeldText();
		if (text != null) {
			if (key == GLFW.GLFW_KEY_BACKSPACE && this.getCursor() > 0) {
				this.backspace();
			} else if (key == GLFW.GLFW_KEY_DELETE && this.getCursor() < text.length()) {
				this.delete();
			} else if (key == GLFW.GLFW_KEY_LEFT && this.getCursor() > 0) {
				this.moveCursor(-1);
			} else if (key == GLFW.GLFW_KEY_RIGHT && this.getCursor() < this.getText().length()) {
				this.moveCursor(1);
			}
		}

		if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER)
				&& (mods & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT) {
			this.addChar('\n');
		}
	}

	/** delete the char at the 'right' of the cursor */
	public final void delete() {
		if (this.heldText == null || this.cursor == this.heldText.length()) {
			return;
		}
		String begin = this.heldText.substring(0, this.cursor);
		String end = this.heldText.substring(this.cursor + 1, heldText.length());
		this.setHeldText(begin + end, this.cursor);
	}

	/** delete the char at the 'left' of the cursor */
	public final void backspace() {
		if (this.heldText == null || this.cursor == 0) {
			return;
		}
		String begin = this.heldText.substring(0, this.cursor - 1);
		String end = this.heldText.substring(this.cursor, heldText.length());
		this.setHeldText(begin + end, this.cursor - 1);
	}

	/** add a char to the prompt at the current cursor position */
	public final void addChar(char c) {
		String txt = this.heldText == null ? "" : this.heldText;
		if (txt.length() < this.maxChars && this.charset.indexOf(c) >= 0) {
			String begin = txt.substring(0, this.cursor);
			String end = txt.substring(this.cursor, txt.length());
			this.setHeldText(begin + c + end, this.cursor + 1);
		}
	}

	/** move the cursor position */
	public final void moveCursor(int xoffset) {
		this.setCursor(this.cursor + xoffset);
	}

	/** set the cursor position */
	public final int setCursor(int index) {
		if (this.cursor == index) {
			return (this.cursor);
		}

		if (this.heldText == null) {
			this.cursor = 0;
			return (0);
		}

		this.cursor = index < 0 ? 0 : index > this.heldText.length() ? this.heldText.length() : index;
		this.tickCursor();
		this.setHeldTextAsText();
		return (this.cursor);
	}

	private final int getCursor() {
		return (this.cursor);
	}

	private final void updateCursor() {
		long now = System.currentTimeMillis();
		boolean prevShowCursor = this.showCursor();
		this.cursorTimer = (this.cursorTimer + (now - this.lastCursorUpdate)) % (CURSOR_MS * 2);
		this.lastCursorUpdate = now;
		boolean nextShowCursor = this.showCursor();
		if ((nextShowCursor && !prevShowCursor) || !nextShowCursor && prevShowCursor) {
			this.setHeldTextAsText();
		}
	}

	public final boolean showCursor() {
		return (this.cursorTimer < CURSOR_MS);
	}

	private final void tickCursor() {
		this.cursorTimer = 0;
		this.lastCursorUpdate = System.currentTimeMillis();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.hasFocus()) {
			this.updateCursor();
		}
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
		this.updateVisibleText();
	}

	public final void setHeldTextColor(Vector4f color) {
		this.setHeldTextColor(color.x, color.y, color.z, color.w);
	}

	/** set text color of the held text */
	public final void setHeldTextColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.updateVisibleText();
	}

	/** set the maximum chars this prompt can holds */
	public final void setMaxChars(int max) {
		this.maxChars = max;
	}

	/** get the maximum chars this prompt can holds */
	public final int getMaxChars() {
		return (this.maxChars);
	}

	/** get the hint */
	public final String getHint() {
		return (this.hintText);
	}

	/** set the hint text */
	public final void setHint(String hintText) {
		this.hintText = hintText;
		this.updateVisibleText();
	}

	/** return the text held by this prompt */
	public final String getHeldText() {
		return (this.heldText);
	}

	public final void setHeldText(String text) {
		this.setHeldText(text, text != null ? text.length() : 0);
	}

	public final void setHeldText(String text, int index) {
		if (text == null || text.length() == 0) {
			this.heldText = null;
			this.setCursor(0);
		} else {
			this.heldText = text;
			this.setCursor(index);
		}
		this.updateVisibleText();
	}

	private final void setHintAsText() {
		super.setFontColor(this.rh, this.gh, this.bh, this.ah);
		super.setText(this.hintText);
	}

	private final void setHeldTextAsText() {
		super.setFontColor(this.r, this.g, this.b, this.a);
		if (this.heldText == null) {
			super.setText(this.showCursor() ? "" + FontFile.CURSOR_CHAR : "");
		} else {
			if (this.showCursor()) {
				String begin = this.heldText.substring(0, this.cursor);
				String end = this.heldText.substring(this.cursor, heldText.length());
				super.setText(begin + FontFile.CURSOR_CHAR + end);
			} else {
				super.setText(this.heldText);
			}
		}
	}

	private final void updateVisibleText() {
		if (this.hasFocus() || this.getHeldText() != null) {
			this.setHeldTextAsText();
		} else {
			this.setHintAsText();
		}
	}
}
