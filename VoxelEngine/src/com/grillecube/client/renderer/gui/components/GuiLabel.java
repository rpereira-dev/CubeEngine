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

import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.components.parameters.GuiParameter;
import com.grillecube.common.maths.Vector4f;

/**
 * A GuiLabel.
 * 
 * It is a GUI element that should be use to render complex Strings (with
 * borders, buttons...)
 * 
 * @author Romain
 *
 */
public class GuiLabel extends Gui {

	/** the gui text */
	private final GuiText guiText;

	public GuiLabel() {
		super();
		this.guiText = new GuiText();
		this.guiText.setPosition(0.0f, 0.0f);
		this.guiText.setFontColor(0, 0, 0, 1.0f);
		this.addChild(this.guiText);
	}

	public void addTextParameter(GuiParameter<GuiText> parameter) {
		this.guiText.addParameter(parameter);
	}

	public final void setText(String value) {
		this.guiText.setText(value);
	}

	public final void addText(String value) {
		this.guiText.addText(value);
	}

	public final String getText() {
		return (this.guiText.getText());
	}

	public void setFontColor(float r, float g, float b, float a) {
		this.guiText.setFontColor(r, g, b, a);
	}

	public void setFontSize(float sx, float sy) {
		this.guiText.setFontSize(sx, sy);
	}

	public void setFontColor(Vector4f color) {
		this.guiText.setFontColor(color);
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

	public float getTextHeight() {
		return (this.guiText.getTextHeight());
	}

	public GuiText getGuiText() {
		return (this.guiText);
	}
}
