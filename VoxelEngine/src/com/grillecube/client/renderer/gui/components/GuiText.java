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
import com.grillecube.client.renderer.gui.animations.GuiParameter;
import com.grillecube.client.renderer.gui.font.Font;
import com.grillecube.client.renderer.gui.font.FontModel;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector2f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

public abstract class GuiText extends Gui {

	/**
	 * a parameter which should used to recalculate the rect size when changing
	 * the font size
	 */
	public static final GuiParameter<GuiText> PARAM_AUTO_ADJUST_RECT = new GuiParameter<GuiText>() {

		@Override
		public void run(GuiText gui) {
			float width = gui.getFontModel().getTextWidth();
			float height = gui.getFontModel().getTextHeight();
			gui.setSize(width, height, false);
		}
	};

	/** a parameter which tell the gui font model to fill the rectangle */
	public static final GuiParameter<GuiText> PARAM_TEXT_FILL_RECT = new GuiParameter<GuiText>() {

		@Override
		public void run(GuiText gui) {
			float scalex = gui.getWidth() / (gui.getFontModel().getTextWidth() * 0.5f);
			float scaley = gui.getHeight() / (gui.getFontModel().getTextHeight() * 0.5f);
			float scale = Maths.min(scalex, scaley);
			gui.getFontModel().setScale(scale, scale, 0);
		}
	};

	public static final GuiParameter<GuiText> PARAM_TEXT_CENTER = new GuiParameter<GuiText>() {

		@Override
		public void run(GuiText gui) {
			float width = gui.getFontModel().getTextWidth();
			float height = gui.getFontModel().getTextHeight();
			float x = gui.getX() + (gui.getWidth() - width) / 2.0f;
			float y = gui.getY() - (gui.getHeight() - height) / 2.0f;
			gui.getFontModel().setPosition(x, y, 0);
		}
	};

	/**
	 * a parameter which tell the text to be centered X to the gui quad
	 */
	public static final GuiParameter<GuiText> PARAM_TEXT_CENTER_X = new GuiParameter<GuiText>() {

		@Override
		public void run(GuiText gui) {
			float width = gui.getFontModel().getTextWidth();
			gui.getFontModel().setX(gui.getX() + (gui.getWidth() - width) / 2.0f);
		}
	};

	/**
	 * a parameter which tell the text to be centered Y to the gui quad
	 */
	public static final GuiParameter<GuiText> PARAM_TEXT_CENTER_Y = new GuiParameter<GuiText>() {

		@Override
		public void run(GuiText gui) {
			float height = gui.getFontModel().getTextHeight();
			gui.getFontModel().setY(gui.getY() - (gui.getHeight() - height) / 2.0f);
		}
	};

	/** the font model for this text */
	private FontModel fontModel;

	public GuiText() {
		super();
		this.fontModel = new FontModel(GuiRenderer.DEFAULT_FONT);
	}

	/** set text to render */
	public void setText(String str) {
		if (this.fontModel.getText().equals(str)) {
			return;
		}
		this.fontModel.setText(str);
		super.runParameters();
	}

	public void setFontColor(Vector4f color) {
		this.setFontColor(color.x, color.y, color.z, color.w);
	}

	/** set the font color */
	public void setFontColor(float r, float g, float b, float a) {
		Vector4f color = this.fontModel.getFontColor();
		if (color.x == r && color.y == g && color.z == b && color.w == a) {
			return;
		}
		this.fontModel.setFontColor(r, g, b, a);
	}

	public void addText(String str) {
		this.setText(this.fontModel.getText() == null ? str : this.fontModel.getText() + str);
	}

	/** set font to use */
	public void setFont(Font font) {
		this.fontModel.setFont(font);
	}

	/**
	 * @param x
	 *            : x font size
	 * @param y
	 *            : y font size
	 */
	public void setFontSize(float x, float y) {
		this.fontModel.setScale(x, y, 0);
		this.runParameters();
	}

	public void setFontSize(Vector2f vec) {
		this.setFontSize(vec.x, vec.y);
	}

	public Vector3f getFontSize() {
		if (this.fontModel == null) {
			return (Vector3f.NULL_VEC);
		}
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
	protected void onSet(float x, float y, float width, float height, float rot) {
		super.onSet(x, y, width, height, rot);
		this.fontModel.setPosition(x, y, 0.0f);
	}

	@Override
	protected void onRender(GuiRenderer renderer) {
		super.onRender(renderer);
		renderer.renderFontModel(this.fontModel, this.fontModel.getTransformationMatrix());
	}

	public FontModel getFontModel() {
		return (this.fontModel);
	}

	@Override
	protected void onInitialized(GuiRenderer guiRenderer) {
	}

	@Override
	protected void onDeinitialized(GuiRenderer guiRenderer) {
		if (this.fontModel != null) {
			this.fontModel.delete();
			this.fontModel = null;
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
	protected void onUpdate(float x, float y, boolean pressed) {
	}
}
