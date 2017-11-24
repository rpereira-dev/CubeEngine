package com.grillecube.client.renderer.gui.components;

import com.grillecube.common.utils.Color;

public class GuiPopUp extends GuiView {

	private static final Color BG_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.5f);
	private static final Color FG_COLOR = new Color(0.9f, 0.9f, 0.9f, 0.9f);

	private final GuiColoredQuad bg;
	private final GuiColoredQuad fg;

	public GuiPopUp() {
		super();
		this.bg = new GuiColoredQuad();
		this.bg.setBox(0, 0, 1, 1, 0);
		this.bg.setColor(BG_COLOR);
		this.addChild(this.bg);

		this.fg = new GuiColoredQuad();
		this.fg.setBox(0.2f, 0.2f, 0.6f, 0.6f, 0);
		this.fg.setColor(FG_COLOR);
		this.addChild(this.fg);
	}

	public final void close() {
		this.setVisible(false);
	}

	public final void open() {
		this.setVisible(true);
	}
}
