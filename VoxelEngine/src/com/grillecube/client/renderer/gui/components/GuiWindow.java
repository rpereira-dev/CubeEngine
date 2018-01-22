package com.grillecube.client.renderer.gui.components;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.event.GuiEventKeyPress;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.common.utils.Color;

public class GuiWindow extends Gui {

	public static final Color COLOR_BACKGROUND = new Color(0.0f, 0.0f, 0.0f, 0.5f);

	private static final GuiListener<GuiEventKeyPress<GuiWindow>> GUI_LISTENER_ESC_CLOSE = new GuiListener<GuiEventKeyPress<GuiWindow>>() {

		@Override
		public void invoke(GuiEventKeyPress<GuiWindow> event) {
			System.out.println("pressed: " + event.getKey());
			switch (event.getKey()) {
			case GLFW.GLFW_KEY_ESCAPE:
				event.getGui().close();
				break;
			default:
				break;
			}
		}

	};

	/** background texture */
	private final GuiColoredQuad bg;

	public GuiWindow() {
		super();
		this.bg = new GuiColoredQuad();
		this.bg.setColor(COLOR_BACKGROUND);
		this.bg.setHoverable(false);
		this.addListener(ON_HOVERED_FOCUS_LISTENER);
		this.addListener(GUI_LISTENER_ESC_CLOSE);
		this.addChild(bg);
	}

	@Override
	protected void onUpdate() {
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	public final void open(Gui parent) {
		parent.addChild(this);
		this.setLayer(this.getTopestLayer() + 1);
		this.requestFocus(true);
	}

	public final void close() {
		super.addTask(new GuiTask() {
			@Override
			public final boolean run() {
				pop();
				return (true);
			}
		});
	}

}
