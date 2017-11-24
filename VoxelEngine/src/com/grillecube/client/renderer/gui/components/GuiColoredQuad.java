package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.utils.Color;

public class GuiColoredQuad extends Gui {

	/** background texture */
	private final Color color;

	public GuiColoredQuad() {
		super();
		this.color = new Color(0);
	}

	public final void setColor(Color color) {
		this.color.set(color.getARGB());
	}

	public final void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}

	@Override
	protected void onRender(GuiRenderer guiRenderer) {
		Matrix4f matrix = super.getGuiToGLChangeOfBasis();
		guiRenderer.renderColoredQuad(this.color.getR(), this.color.getG(), this.color.getB(), this.color.getA(),
				this.getTransparency(), matrix);
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
}
