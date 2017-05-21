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

package com.grillecube.client.renderer.gui.font;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector2f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

public class FontModel {
	/** opengl IDs */
	private GLVertexArray _vao;
	private GLVertexBuffer _vbo;
	private int _vertex_count;

	/** model data */
	private Vector3f _pos;
	private Vector3f _scale;
	private Vector3f _rot;
	private Font _font;

	/** status */
	public static final int STATE_INITIALIZED = 1;
	public static final int STATE_TEXT_UP_TO_DATE = 2;
	public static final Vector2f DEFAULT_FONT_SIZE2 = new Vector2f(1.0f, 1.0f);
	public static final Vector3f DEFAULT_FONT_SIZE3 = new Vector3f(1.0f, 1.0f, 1.0f);
	private int _state;

	/** transf matrix */
	private Matrix4f _matrix;

	/** text */
	private String _text;
	private ArrayList<FontChar> _text_chars;

	/** parameters */
	private Vector4f _color;
	private float _border_width;
	private float _border_edge;
	private Vector2f _outline_offset;
	private Vector3f _outline_color;

	public FontModel(Font font) {
		this._state = 0;
		this._text = new String();
		this._text_chars = new ArrayList<FontChar>();
		this._font = font;
		this._matrix = new Matrix4f();
		this._pos = new Vector3f(0, 0, 0);
		this._rot = new Vector3f(0, 0, 0);
		this._scale = new Vector3f(1, 1, 1);
		this._color = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
		this._border_width = 0.0f;
		this._border_edge = 0.5f;
		this._outline_color = new Vector3f(0, 0, 0);
		this._outline_offset = new Vector2f(0, 0);
		this.updateTransformationMatrix();
	}

	private void initialize() {
		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();

		this._vao.bind();
		{
			this._vbo.bind(GL15.GL_ARRAY_BUFFER);

			this._vao.setAttribute(0, 3, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, 0);
			this._vao.setAttribute(1, 2, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, 3 * 4);
			this._vao.setAttribute(2, 4, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, (3 + 2) * 4);

			this._vbo.unbind(GL15.GL_ARRAY_BUFFER);

			this._vao.enableAttribute(0);
			this._vao.enableAttribute(1);
			this._vao.enableAttribute(2);
		}
		this._vao.unbind();

		this.setState(FontModel.STATE_INITIALIZED);
	}

	/** destroy the model */
	public void delete() {
		if (this.hasState(FontModel.STATE_INITIALIZED)) {
			GLH.glhDeleteObject(this._vao);
			GLH.glhDeleteObject(this._vbo);
			this.unsetState(FontModel.STATE_INITIALIZED);
		}
	}

	/** add a string to the model */
	public void addText(String str) {
		this.setText(this._text == null ? str : this._text + str);
	}

	/** rebuild the mesh depending on the given string */
	public void setText(String str) {
		this._text = str;
		this.updateFontChars();
		this.requestUpdate();
	}

	private void updateFontChars() {
		if (this._font == null || this._text == null) {
			this._text_chars = null;
			return;
		}

		this._text_chars.clear();
		int length = this._text.length();
		for (int i = 0; i < length; i++) {
			this._text_chars.add(this._font.getFile().getCharData(this._text.charAt(i)).clone());
		}
	}

	public void setFont(Font font) {
		this._font = font;
		this.updateFontChars();
		this.requestUpdate();
	}

	/** set the font color */
	public void setFontColor(float r, float g, float b, float a) {
		this._color.set(r, g, b, a);
		this.requestUpdate();
	}

	/** update FontModel GLVertexBuffer vertices depending on 'this._text' */
	private void updateText() {
		float[] vertices = this.generateFontBuffer();
		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		if (vertices != null) {
			this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
			this._vertex_count = vertices.length / 5;
		} else {
			this._vbo.bufferSize(GL15.GL_ARRAY_BUFFER, 0, GL15.GL_STATIC_DRAW);
			this._vertex_count = 0;
		}
		this.setState(FontModel.STATE_TEXT_UP_TO_DATE);
	}

	/** each char is a quad (4 vertex) of 5 floats (pos + uv) */
	private float[] generateFontBuffer() {
		int size = this._text_chars.size();
		if (this._text_chars == null || size == 0 || this._font == null) {
			return (null);
		}

		Vector4f color = this._color;
		float[] vertices = new float[size * (6 * (3 + 2 + 4))];

		float posx = 0;
		float posy = 0;
		float posz = 0;
		int index = 0;

		for (FontChar fchar : this._text_chars) {
			if (fchar == null) {
				continue;
			}
			if (fchar.ascii == '\n') {
				posy = posy - FontFile.LINEHEIGHT;
				posx = 0;
				continue;
			}

			float xsize = fchar.width;
			float ysize = fchar.height;

			// first vertex
			vertices[index++] = posx + fchar.xoffset;
			vertices[index++] = posy - fchar.yoffset;
			vertices[index++] = posz;
			vertices[index++] = fchar.uvx;
			vertices[index++] = fchar.uvy;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;

			// second vertex
			vertices[index++] = posx + fchar.xoffset;
			vertices[index++] = posy - fchar.yoffset - ysize;
			vertices[index++] = posz;
			vertices[index++] = fchar.uvx;
			vertices[index++] = fchar.uvy + fchar.uvheight;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;

			// third vertex
			vertices[index++] = posx + fchar.xoffset + xsize;
			vertices[index++] = posy - fchar.yoffset - ysize;
			vertices[index++] = posz;
			vertices[index++] = fchar.uvx + fchar.uvwidth;
			vertices[index++] = fchar.uvy + fchar.uvheight;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;

			// first vertex
			vertices[index++] = posx + fchar.xoffset;
			vertices[index++] = posy - fchar.yoffset;
			vertices[index++] = posz;
			vertices[index++] = fchar.uvx;
			vertices[index++] = fchar.uvy;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;

			// third vertex
			vertices[index++] = posx + fchar.xoffset + xsize;
			vertices[index++] = posy - fchar.yoffset - ysize;
			vertices[index++] = posz;
			vertices[index++] = fchar.uvx + fchar.uvwidth;
			vertices[index++] = fchar.uvy + fchar.uvheight;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;

			// fourth vertex
			vertices[index++] = posx + fchar.xoffset + xsize;
			vertices[index++] = posy - fchar.yoffset;
			vertices[index++] = posz;
			vertices[index++] = fchar.uvx + fchar.uvwidth;
			vertices[index++] = fchar.uvy;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;

			posx = posx + fchar.xadvance;
		}

		return (vertices);
	}

	/** in percent depending on screen position (-1:1) */
	public void setPosition(float x, float y, float z) {
		this._pos.x = x;
		this._pos.y = y;
		this._pos.z = z;
		this.updateTransformationMatrix();
	}

	public void setX(float x) {
		this.setPosition(x, this._pos.y, this._pos.z);
	}

	public void setY(float y) {
		this.setPosition(this._pos.x, y, this._pos.z);
	}

	public void setZ(float z) {
		this.setPosition(this._pos.x, this._pos.y, z);
	}

	public void setRotation(float x, float y, float z) {
		this._rot.x = x;
		this._rot.y = y;
		this._rot.z = z;
		this.updateTransformationMatrix();
	}

	public void setScale(float x, float y, float z) {
		this._scale.set(x, y, z);
		this.updateTransformationMatrix();
	}

	private void updateTransformationMatrix() {
		this._matrix.setIdentity();
		this._matrix.translate(this._pos);
		this._matrix.rotate(this._rot.x, new Vector3f(1, 0, 0));
		this._matrix.rotate(this._rot.y, new Vector3f(0, 1, 0));
		this._matrix.rotate(this._rot.z, new Vector3f(0, 0, 1));
		this._matrix.scale(this._scale);
	}

	/** render this font model */
	public void render() {
		if (this.hasState(FontModel.STATE_INITIALIZED) == false) {
			this.initialize();
		}

		if (this.hasState(FontModel.STATE_TEXT_UP_TO_DATE) == false) {
			this.updateText();
		}

		if (this._vertex_count == 0 || this._font == null) {
			return;
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this._vao.bind();

		this._font.getTexture().bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		this._vao.draw(GL11.GL_TRIANGLES, 0, this._vertex_count);
	}

	public boolean hasState(int state) {
		return ((this._state & state) == state);
	}

	private void setState(int state) {
		this._state = this._state | state;
	}

	private void unsetState(int state) {
		this._state = this._state & ~(state);
	}

	public Matrix4f getTransformationMatrix() {
		return (this._matrix);
	}

	public String getText() {
		return (this._text);
	}

	@Override
	public String toString() {
		return ("FontModel : " + this.getText());
	}

	public Font getFont() {
		return (this._font);
	}

	/** return text height in gl coordinate system */
	public float getTextWidth() {
		float maxwidth = 0;
		float width = 0;

		for (FontChar fchar : this._text_chars) {

			if (fchar == null) {
				continue;
			}

			if (fchar.ascii == '\n') {
				if (maxwidth < width) {
					maxwidth = width;
				}
				width = 0;
				continue;
			}
			width += fchar.xadvance + fchar.xoffset;
		}

		return ((maxwidth == 0 ? width : maxwidth) * this.getScale().x);
	}

	/** return text height in gl coordinate system */
	public float getTextHeight() {
		int linescount = this.getText().length() - this.getText().replace("\n", "").length() + 1;
		return (linescount * FontFile.LINEHEIGHT * this.getScale().y);
	}

	public Vector3f getScale() {
		return (this._scale);
	}

	public ArrayList<FontChar> getFontChar() {
		return (this._text_chars);
	}

	/** request for an update of this font model */
	public void requestUpdate() {
		this.unsetState(FontModel.STATE_TEXT_UP_TO_DATE);
	}

	public Vector4f getFontColor() {
		return (this._color);
	}

	public Vector3f getPosition() {
		return (this._pos);
	}

	public float getX() {
		return (this._pos.x);
	}

	public float getY() {
		return (this._pos.y);
	}

	public float getZ() {
		return (this._pos.z);
	}

	public float getBorderWidth() {
		return (this._border_width);
	}

	public void setBorderWidth(float value) {
		this._border_width = value;
	}

	public float getBorderEdge() {
		return (this._border_edge);
	}

	public void setBorderEdge(float value) {
		this._border_edge = value;
	}

	public Vector2f getOutlineOffset() {
		return (this._outline_offset);
	}

	public void setOutlineOffset(float x, float y) {
		this._outline_offset.set(x, y);
	}

	public Vector3f getOutlineColor() {
		return (this._outline_color);
	}

	public void setOutlineColor(float r, float g, float b) {
		this._outline_color.set(r, g, b);
	}

	/**
	 * reset the outline and border parameters to default, to remove any effects
	 */
	public void clearEffects() {
		this.setBorderWidth(0.0f);
		this.setOutlineOffset(0, 0);
		this.setOutlineColor(0, 0, 0);
	}
}
