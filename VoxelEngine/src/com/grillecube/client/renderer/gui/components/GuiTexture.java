package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.common.maths.Matrix4f;

public class GuiTexture extends Gui {

	/** background texture */
	private GLTexture glTexture;
	private float ux;
	private float uy;
	private float vx;
	private float vy;

	public GuiTexture() {
		super();
	}

	public final void setTexture(GLTexture glTexture, float ux, float uy, float vx, float vy) {
		this.setTexture(glTexture);
		this.setTexture(ux, uy, vx, vy);
	}

	public final void setTexture(float ux, float uy, float vx, float vy) {
		this.ux = ux;
		this.uy = uy;
		this.vx = vx;
		this.vy = vy;
	}

	public void setTexture(GLTexture glTexture) {
		this.glTexture = glTexture;
	}

	@Override
	protected void onRender(GuiRenderer guiRenderer) {
		if (this.glTexture != null) {
			Matrix4f matrix = super.getGuiToGLChangeOfBasis();
			guiRenderer.renderTexturedQuad(this.glTexture, this.ux, this.uy, this.vx, this.vy, matrix);
		}
	}

	@Override
	public void onAddedTo(GuiRenderer guiRenderer) {
	}

	@Override
	public void onRemovedFrom(GuiRenderer guiRenderer) {
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean mouse_in) {
	}

	@Override
	protected void onInitialized(GuiRenderer renderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer renderer) {
		// do not delete the texture, as it wasnt create by this object...
	}
}
