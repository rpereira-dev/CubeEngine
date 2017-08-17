package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.common.maths.Vector4f;

public class GuiPopUp extends GuiView {

	private static final Vector4f BG_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 0.5f);

	private final GuiColoredQuad bg;
	private final GuiWindow popUpWindow;

	public GuiPopUp() {
		super();
		this.bg = new GuiColoredQuad();
		this.bg.setBox(0, 0, 1, 1, 0);
		this.bg.setColor(BG_COLOR);
		this.addChild(this.bg);

		this.popUpWindow = new GuiWindow();
		this.popUpWindow.setBox(0.15f, 0.15f, 0.70f, 0.70f, 0);
		this.addChild(this.popUpWindow);
	}

	public final void close() {
		this.popUpWindow.close();
		if (this.getParent() != null) {
			this.getParent().removeChild(this);
		} else {
			// remove from gui renderer TODO
		}
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

}
