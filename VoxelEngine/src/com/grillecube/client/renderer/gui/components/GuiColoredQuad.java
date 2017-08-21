package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector4f;

public class GuiColoredQuad extends Gui {

	/** background texture */
	private float r;
	private float g;
	private float b;
	private float a;

	public GuiColoredQuad() {
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
		guiRenderer.renderColoredQuad(this.r, this.g, this.b, this.a, matrix);
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

	@Override
	protected void onUpdate() {
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
		// do not delete the texture, as it wasnt create by this object...
	}
}
