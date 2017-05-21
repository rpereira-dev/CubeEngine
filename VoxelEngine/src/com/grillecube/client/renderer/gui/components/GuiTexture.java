package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.gui.Gui;
import com.grillecube.client.renderer.gui.GuiRenderer;

public class GuiTexture extends Gui {

	private GLTexture _texture;

	public GuiTexture(GLTexture texture) {
		this._texture = texture;
	}

	@Override
	public void render(GuiRenderer renderer) {
		renderer.renderQuad(this._texture, 0, 0, 1, 1, this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	@Override
	public void onAdded(GuiView view) {

	}

	@Override
	public void onRemoved(GuiView view) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean mouse_in) {

	}
}
