package com.grillecube.client.renderer.gui;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opengl.GLCursor;
import com.grillecube.client.opengl.window.event.GLFWEventChar;
import com.grillecube.client.opengl.window.event.GLFWEventKeyPress;
import com.grillecube.client.opengl.window.event.GLFWEventMouseScroll;
import com.grillecube.client.opengl.window.event.GLFWListener;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.components.GuiButton;
import com.grillecube.client.renderer.gui.components.GuiPrompt;
import com.grillecube.client.renderer.gui.event.GuiEventChar;
import com.grillecube.client.renderer.gui.event.GuiEventClick;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseEnter;
import com.grillecube.client.renderer.gui.event.GuiEventMouseExit;
import com.grillecube.client.renderer.gui.event.GuiEventMouseHover;
import com.grillecube.client.renderer.gui.event.GuiEventMouseLeftPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseLeftRelease;
import com.grillecube.client.renderer.gui.event.GuiEventMouseMove;
import com.grillecube.client.renderer.gui.event.GuiEventMouseRightPress;
import com.grillecube.client.renderer.gui.event.GuiEventMouseRightRelease;
import com.grillecube.client.renderer.gui.event.GuiEventMouseScroll;
import com.grillecube.client.renderer.gui.event.GuiEventPress;
import com.grillecube.client.renderer.gui.event.GuiEventUnpress;
import com.grillecube.client.renderer.model.editor.gui.GuiModelView;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector4f;

/** catch inputs and send them back to a gui */
public class GuiInputManagerDesktop extends GuiInputManager {

	/** the window on which input are catched */
	private GLFWListener<GLFWEventKeyPress> keyPressListener;
	private GLFWListener<GLFWEventChar> charListener;
	private GLFWListener<GLFWEventMouseScroll> scrollListener;
	private Gui topestGui;

	/** default cursors */
	public GLCursor[] cursor = new GLCursor[6];
	private static final int ARROW = 0, IBEAM = 1, CROSSHAIR = 2, HAND = 3, HRESIZE = 4, VRESIZE = 5;

	/** add listeners to the window */
	@Override
	protected final void onInitialized() {
		this.keyPressListener = new GLFWListener<GLFWEventKeyPress>() {
			@Override
			public void invoke(GLFWEventKeyPress event) {
				Gui g = getFocusedGui();
				if (g != null) {
					g.stackEvent(new GuiEventKeyPress<Gui>(g, event.getGLFWWindow(), event.getKey(),
							event.getScancode(), event.getMods()));
				}
			}
		};

		this.charListener = new GLFWListener<GLFWEventChar>() {
			@Override
			public void invoke(GLFWEventChar event) {
				Gui g = getFocusedGui();
				if (getFocusedGui() != null) {
					g.stackEvent(new GuiEventChar<Gui>(g, event.getGLFWWindow(), event.getCharacter()));
				}
			}
		};

		this.scrollListener = new GLFWListener<GLFWEventMouseScroll>() {
			@Override
			public void invoke(GLFWEventMouseScroll event) {
				Gui g = getFocusedGui();
				if (getFocusedGui() != null) {
					g.stackEvent(new GuiEventMouseScroll<Gui>(g, event.getScrollX(), event.getScrollY()));
				}
			}
		};

		super.getGLFWWindow().addListener(this.keyPressListener);
		super.getGLFWWindow().addListener(this.charListener);
		super.getGLFWWindow().addListener(this.scrollListener);

		this.cursor[ARROW] = new GLCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
		this.cursor[IBEAM] = new GLCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR));
		this.cursor[CROSSHAIR] = new GLCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR));
		this.cursor[HAND] = new GLCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR));
		this.cursor[HRESIZE] = new GLCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR));
		this.cursor[VRESIZE] = new GLCursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR));
	}

	/** remove listeners from the window */
	@Override
	public final void onDeinitialized() {
		super.getGLFWWindow().removeListener(this.keyPressListener);
		super.getGLFWWindow().removeListener(this.charListener);
		super.getGLFWWindow().removeListener(this.scrollListener);
	}

	@Override
	protected void onUpdate() {
		this.updateCursor();
	}

	private final void updateCursor() {
		GLCursor cursor;
		if (this.topestGui instanceof GuiPrompt) {
			cursor = this.cursor[IBEAM];
		} else if (this.topestGui instanceof GuiButton) {
			cursor = this.cursor[HAND];

		} else if (topestGui instanceof GuiModelView) {
			cursor = this.cursor[CROSSHAIR];

		} else {
			cursor = this.cursor[ARROW];
		}
		
		super.getGLFWWindow().setCursor(cursor);
	}

	/** update the given guis, which should be sorted by their layers */
	@Override
	protected void onGuisUpdate(ArrayList<Gui> guis) {
		double xpos = super.getGLFWWindow().getMouseX();
		double ypos = super.getGLFWWindow().getMouseY();
		float mx = (float) (xpos / super.getGLFWWindow().getWidth());
		float my = (float) (1 - (ypos / super.getGLFWWindow().getHeight()));
		boolean leftPressed = super.getGLFWWindow().isMouseLeftPressed();
		boolean rightPressed = super.getGLFWWindow().isMouseRightPressed();
		boolean topestHoveredFound = false;

		int i;
		for (i = guis.size() - 1; i >= 0; i--) {
			Gui gui = guis.get(i);
			gui.update();

			if (gui.requestedFocus()) {
				this.setFocusedGui(gui);
				gui.requestFocus(false);
			}

			// get the coordinates relatively to the gui basis
			Vector4f mouse = new Vector4f(mx, my, 0.0f, 1.0f);
			Matrix4f.transform(gui.getWindowToGuiChangeOfBasis(), mouse, mouse);

			// update gui states (stack events)
			float x = mouse.x;
			float y = mouse.y;

			// mouse moving relatively to the gui
			if (x != gui.getPrevMouseX() || y != gui.getPrevMouseY()) {
				gui.setMouse(x, y);
				gui.stackEvent(new GuiEventMouseMove<Gui>(gui));
			}

			// if topest hovered gui is hovered,
			if (!topestHoveredFound && gui.isHoverable() && (x >= 0.0f && x < 1.0f && y >= 0.0f && y <= 1.0f)) {
				topestHoveredFound = true;
				gui.stackEvent(new GuiEventMouseHover<Gui>(gui));
				this.topestGui = gui;
				// if gui wasnt hovered earlier
				if (!gui.isHovered()) {
					gui.setHovered(true);
					gui.stackEvent(new GuiEventMouseEnter<Gui>(gui));
				}
				// if mouse wasnt left pressed, and now is pressed, and the gui
				// is
				// hovered
				if (!gui.isLeftPressed() && leftPressed) {
					gui.setPressed(true);
					gui.stackEvent(new GuiEventPress<Gui>(gui));
					gui.stackEvent(new GuiEventMouseLeftPress<Gui>(gui));
					if (gui.isSelectable()) {
						gui.setSelected(!gui.isSelected());
					}
				} else if (gui.isLeftPressed() && !leftPressed) {
					gui.setPressed(false);
					gui.stackEvent(new GuiEventUnpress<Gui>(gui));
					gui.stackEvent(new GuiEventMouseLeftRelease<Gui>(gui));
					gui.stackEvent(new GuiEventClick<Gui>(gui));
				}

				// if mouse wasnt right pressed, and now is pressed, and the gui
				// is
				// hovered
				if (!gui.isRightPressed() && rightPressed) {
					gui.setRightPressed(true);
					gui.stackEvent(new GuiEventMouseRightPress<Gui>(gui));
				} else if (gui.isRightPressed() && !rightPressed) {
					gui.setRightPressed(false);
					gui.stackEvent(new GuiEventMouseRightRelease<Gui>(gui));
				}
			} else {
				if (gui.isHovered()) {
					gui.setHovered(false);
					gui.stackEvent(new GuiEventMouseExit<Gui>(gui));
				}

				if (gui.isLeftPressed() && !leftPressed) {
					gui.setPressed(false);
					gui.stackEvent(new GuiEventUnpress<Gui>(gui));
					gui.stackEvent(new GuiEventMouseLeftRelease<Gui>(gui));
				}

				if (gui.isRightPressed() && !rightPressed) {
					gui.setRightPressed(false);
					gui.stackEvent(new GuiEventMouseRightRelease<Gui>(gui));
				}
			}
			gui.unstackEvents();
		}
	}
}
