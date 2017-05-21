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

import com.grillecube.client.renderer.gui.Gui;
import com.grillecube.client.renderer.gui.GuiParameter;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.gui.font.Font;
import com.grillecube.client.renderer.gui.font.FontModel;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector2f;
import com.grillecube.common.maths.Vector2i;
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
			gui.setWidth(width);
			gui.setHeight(height);
		}
	};

	/** a parameter which tell the gui font model to fill the rectangle */
	public static final GuiParameter<GuiText> PARAM_TEXT_FILL_RECT = new GuiParameter<GuiText>() {

		@Override
		public void run(GuiText gui) {
			float scalex = gui.getWidth() / gui.getFontModel().getTextWidth();
			float scaley = gui.getHeight() / gui.getFontModel().getTextHeight();
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
	private FontModel _font_model;

	public GuiText() {
		this(0, 0, 0, 0);
	}

	public GuiText(float x, float y, float width, float height) {
		super(x, y, width, height);
		this._font_model = new FontModel(GuiRenderer.DEFAULT_FONT);
	}

	public GuiText(float x, float y) {
		this(x, y, 0, 0);
	}

	/** set text to render */
	public void setText(String str) {
		if (this._font_model.getText().equals(str)) {
			return;
		}
		this._font_model.setText(str);
		super.runParameters();
	}

	public void setFontColor(Vector4f color) {
		this.setFontColor(color.x, color.y, color.z, color.w);
	}

	/** set the font color */
	public void setFontColor(float r, float g, float b, float a) {
		Vector4f color = this._font_model.getFontColor();
		if (color.x == r && color.y == g && color.z == b && color.w == a) {
			return;
		}
		this._font_model.setFontColor(r, g, b, a);
	}

	public void addText(String str) {
		this.setText(this._font_model.getText() == null ? str : this._font_model.getText() + str);
	}

	/** set font to use */
	public void setFont(Font font) {
		this._font_model.setFont(font);
	}

	/**
	 * @param x
	 *            : x font size
	 * @param y
	 *            : y font size
	 */
	public void setFontSize(float x, float y) {
		this._font_model.setScale(x, y, 0);
		this.runParameters();
	}

	public void setFontSize(Vector2f vec) {
		this.setFontSize(vec.x, vec.y);
	}

	public Vector3f getFontSize() {
		if (this._font_model == null) {
			return (Vector3f.NULL_VEC);
		}
		return (this._font_model.getScale());
	}

	/** get the text held */
	public String getText() {
		if (this._font_model == null) {
			return (null);
		}
		return (this._font_model.getText());
	}

	@Override
	public void render(GuiRenderer renderer) {
		super.render(renderer);
		renderer.renderFontModel(this._font_model);
	}

	public FontModel getFontModel() {
		return (this._font_model);
	}

	/** cols_lines[0] == cols ; cols_lines[1] == lines */
	public Vector2i countColsLines() {
		int cols = 0;
		int lines = 1;
		int cols_tmp = 0;
		String text = this.getText();
		int length = text.length();
		for (int i = 0; i < length; i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				++lines;
				if (cols_tmp > cols) {
					cols = cols_tmp;
				}
				cols_tmp = 0;
			} else {
				++cols_tmp;
			}
		}
		if (cols_tmp > cols) {
			cols = cols_tmp;
		}
		return (new Vector2i(cols, lines));
	}

	@Override
	public void onAdded(GuiView view) {
		view.addFontModel(this._font_model);
	}

	@Override
	public void onRemoved(GuiView view) {
		view.removeFontModel(this._font_model);
		this._font_model.delete();
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);

		if (this._font_model != null) {
			this._font_model.setPosition(x, y, 0);
		}
	}

	@Override
	public void setPosition(float x, float y, boolean runParameters) {
		super.setPosition(x, y, runParameters);
		if (this._font_model != null) {
			this._font_model.setPosition(x, y, 0);
		}
	}

	@Override
	protected void onUpdate(float x, float y, boolean pressed) {

	}
}
