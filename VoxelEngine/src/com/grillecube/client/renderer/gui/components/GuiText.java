/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.gui.components;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.font.FontModel;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

public abstract class GuiText extends Gui {

	/** the font model for this text */
	private final FontModel fontModel;

	public GuiText() {
		super();
		this.fontModel = new FontModel(GuiRenderer.DEFAULT_FONT);
	}

	/** set text to render */
	public final void setText(String str) {
		if (this.fontModel.getText() != null && this.fontModel.getText().equals(str)) {
			return;
		}
		this.fontModel.setText(str);
		this.runParameters();
		this.onTextChanged(str);
	}

	protected void onTextChanged(String str) {
	}

	public final void setFontColor(Vector4f color) {
		this.setFontColor(color.x, color.y, color.z, color.w);
	}

	/** set the font color */
	public final void setFontColor(float r, float g, float b, float a) {
		this.fontModel.setFontColor(r, g, b, a);
	}

	public void addText(String str) {
		this.setText(this.fontModel.getText() == null ? str : this.fontModel.getText() + str);
	}

	public Vector3f getFontSize() {
		return (this.fontModel.getScale());
	}

	/** get the text held */
	public String getText() {
		if (this.fontModel == null) {
			return (null);
		}
		return (this.fontModel.getText());
	}

	@Override
	protected void onRender(GuiRenderer renderer) {
		super.onRender(renderer);

		Matrix4f transfMatrix = this.fontModel.getTransformationMatrix();
		if (this.getParent() != null) {
			transfMatrix = Matrix4f.mul(MainRenderer.GL_TO_WINDOW_BASIS, transfMatrix, null);
			transfMatrix = Matrix4f.mul(this.getParent().getGuiToWindowChangeOfBasis(), transfMatrix, null);
			transfMatrix = Matrix4f.mul(MainRenderer.WINDOW_TO_GL_BASIS, transfMatrix, null);
		}
		renderer.renderFontModel(this.fontModel, transfMatrix);
	}

	public final void setPosition(float x, float y) {
		this.fontModel.setPosition(2.0f * x - 1.0f, 2.0f * y - 1.0f, 0.0f);
		this.runParameters();
	}

	public final void setFontSize(float x, float y) {
		this.fontModel.setScale(x, y, 1.0f);
		this.runParameters();
	}

	public FontModel getFontModel() {
		return (this.fontModel);
	}

	@Override
	protected void onInitialized(GuiRenderer guiRenderer) {
		this.fontModel.initialize();
		this.resizeFontModelAspect(1 / 1.60f);
	}

	@Override
	protected void onDeinitialized(GuiRenderer guiRenderer) {
		this.fontModel.deinitialize();
	}

	@Override
	public void onAddedTo(Gui gui) {
	}

	@Override
	public void onRemovedFrom(Gui gui) {
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {
	}

	/**
	 * @return : the text width in the window coordinate system
	 */
	public float getTextWidth() {
		return (this.getFontModel().getTextWidth() * this.getFontModel().getScaleX() * 0.5f);
	}

	/**
	 * @return : the text height in the window coordinate system
	 */
	public float getTextHeight() {
		return (this.getFontModel().getTextHeight() * this.getFontModel().getScaleY() * 0.5f);
	}

	@Override
	public void onWindowResized(int width, int height) {
		super.onWindowResized(width, height);
		this.resizeFontModelAspect(width / (float) height);
	}

	private void resizeFontModelAspect(float windowAspect) {
		if (this.getFontModel() != null) {
			this.getFontModel().setAspect(windowAspect * this.getTotalAspectRatio());
			this.runParameters();
		}
	}
}
