package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector4f;

public class GuiWindow extends Gui {

	/** background texture */
	private float r, g, b, a;

	public GuiWindow() {
		super();
	}

	public final void setColor(Vector4f rgba) {
		this.setColor(rgba.x, rgba.y, rgba.z, rgba.w);
	}

	public final void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	@Override
	protected void onRender(GuiRenderer guiRenderer) {
		Matrix4f matrix = super.getGuiToGLChangeOfBasis();
		guiRenderer.renderColoredQuad(this.r, this.g, this.b, this.a, this.getTransparency(), matrix);
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
