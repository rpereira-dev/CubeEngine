package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.common.maths.Vector4f;

public class GuiWindow extends Gui {
	private static final Vector4f BG_COLOR = new Vector4f(0.9f, 0.9f, 0.9f, 0.9f);
	private final GuiColoredQuad bg;

	public GuiWindow() {
		super();
		this.bg = new GuiColoredQuad();
		this.bg.setBox(0, 0, 1, 1, 0);
		this.bg.setColor(BG_COLOR);
		this.addChild(this.bg);
	}

	public final void setBackgroundColor(float r, float g, float b, float a) {
		this.bg.setColor(r, g, b, a);
	}

	public final void setBackgroundColor(Vector4f rgba) {
		this.bg.setColor(rgba);
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddedTo(Gui gui) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemovedFrom(Gui gui) {
		// TODO Auto-generated method stub

	}

	public final void close() {
		if (this.getParent() != null) {
			this.getParent().removeChild(this);
		}
	}

}
