package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.common.utils.Color;

public class GuiWindow extends Gui {

	public static final Color COLOR_BACKGROUND = new Color(0.0f, 0.0f, 0.0f, 0.5f);

	/** background texture */
	private final GuiColoredQuad bg;

	public GuiWindow() {
		super();
		this.bg = new GuiColoredQuad();
		this.bg.setColor(COLOR_BACKGROUND);
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
