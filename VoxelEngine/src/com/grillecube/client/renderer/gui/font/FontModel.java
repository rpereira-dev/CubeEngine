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

/**
 * 
 * every coordinates are here relative to opengl screen space
 * 
 * @author Romain
 *
 */
public class FontModel {
	/** opengl IDs */
	private GLVertexArray vao;
	private GLVertexBuffer vbo;
	private int vertexCount;

	/** model data */
	private Vector3f pos;
	private Vector3f scale;
	private Vector3f rot;
	private Font font;

	/** status */
	public static final int STATE_INITIALIZED = 1;
	public static final int STATE_TEXT_UP_TO_DATE = 2;
	private int state;

	/** transf transfMatrix */
	private Matrix4f transfMatrix;

	/** text */
	private String text;
	private ArrayList<FontChar> textChars;

	/** parameters */
	private Vector4f color;
	private float borderWidth;
	private float borderEdge;
	private Vector2f outlineOffset;
	private Vector3f outlineColor;

	/** text width and height */
	private float textWidth;
	private float textHeight;

	public FontModel(Font font) {
		this.state = 0;
		this.text = new String();
		this.textChars = new ArrayList<FontChar>();
		this.font = font;
		this.transfMatrix = new Matrix4f();
		this.pos = new Vector3f();
		this.rot = new Vector3f();
		this.scale = new Vector3f();
		this.set(0, 0, 0, 1.0f, 1.0f, 0.0f, 0, 0, 0);
		this.color = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
		this.borderWidth = 0.0f;
		this.borderEdge = 0.5f;
		this.outlineColor = new Vector3f(0, 0, 0);
		this.outlineOffset = new Vector2f(0, 0);
	}

	private void initialize() {
		this.vao = GLH.glhGenVAO();
		this.vbo = GLH.glhGenVBO();

		this.vao.bind();
		{
			this.vbo.bind(GL15.GL_ARRAY_BUFFER);

			this.vao.setAttribute(0, 3, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, 0);
			this.vao.setAttribute(1, 2, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, 3 * 4);
			this.vao.setAttribute(2, 4, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, (3 + 2) * 4);

			this.vbo.unbind(GL15.GL_ARRAY_BUFFER);

			this.vao.enableAttribute(0);
			this.vao.enableAttribute(1);
			this.vao.enableAttribute(2);
		}
		this.vao.unbind();

		this.setState(FontModel.STATE_INITIALIZED);
	}

	/** destroy the model */
	public void delete() {
		if (this.hasState(FontModel.STATE_INITIALIZED)) {
			GLH.glhDeleteObject(this.vao);
			GLH.glhDeleteObject(this.vbo);
			this.unsetState(FontModel.STATE_INITIALIZED);
		}
	}

	/** add a string to the model */
	public void addText(String str) {
		this.setText(this.text == null ? str : this.text + str);
	}

	/** rebuild the mesh depending on the given string */
	public void setText(String str) {
		this.text = str;
		this.updateFontChars();
		this.requestUpdate();
	}

	private void updateFontChars() {
		if (this.font == null || this.text == null || this.text.length() == 0) {
			this.textChars = null;
			this.textWidth = 0;
			this.textHeight = 0;
			return;
		}

		this.textChars.clear();
		int length = this.text.length();

		float maxwidth = 0;
		float width = 0;
		int lineCount = 1;

		for (int i = 0; i < length; i++) {
			FontChar fchar = this.font.getFile().getCharData(this.text.charAt(i));
			this.textChars.add(fchar);
			width += fchar.xadvance;

			if (fchar.ascii == '\n') {
				++lineCount;
				if (maxwidth < width) {
					maxwidth = width;
				}
				width = 0;
				continue;
			}
		}

		this.textWidth = (maxwidth == 0 ? width : maxwidth)
				+ (this.font.getFile().getCharData(this.text.charAt(this.text.length() - 1)).xadvance);
		this.textHeight = lineCount * FontFile.LINEHEIGHT;
	}

	public void setFont(Font font) {
		this.font = font;
		this.updateFontChars();
		this.requestUpdate();
	}

	/** set the font color */
	public void setFontColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
		this.requestUpdate();
	}

	/** update FontModel GLVertexBuffer vertices depending on 'this.text' */
	private void updateText() {
		float[] vertices = this.generateFontBuffer();
		this.vbo.bind(GL15.GL_ARRAY_BUFFER);
		if (vertices != null) {
			this.vbo.bufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
			this.vertexCount = vertices.length / 5;
		} else {
			this.vbo.bufferSize(GL15.GL_ARRAY_BUFFER, 0, GL15.GL_STATIC_DRAW);
			this.vertexCount = 0;
		}
		this.setState(FontModel.STATE_TEXT_UP_TO_DATE);
	}

	/** each char is a quad (4 vertex) of 5 floats (pos + uv) */
	private float[] generateFontBuffer() {
		int size = this.textChars.size();
		if (this.textChars == null || size == 0 || this.font == null) {
			return (null);
		}

		Vector4f color = this.color;
		float[] vertices = new float[size * (6 * (3 + 2 + 4))];

		float posx = 0;
		float posy = FontFile.LINEHEIGHT;
		float posz = 0;
		int index = 0;

		for (FontChar fchar : this.textChars) {
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
		this.set(x, y, z, this.getScaleX(), this.getScaleY(), this.getScaleZ(), this.getRotationX(),
				this.getRotationY(), this.getRotationZ());
	}

	public void setX(float x) {
		this.setPosition(x, this.pos.y, this.pos.z);
	}

	public void setY(float y) {
		this.setPosition(this.pos.x, y, this.pos.z);
	}

	public void setZ(float z) {
		this.setPosition(this.pos.x, this.pos.y, z);
	}

	public void setRotation(float rx, float ry, float rz) {
		this.set(this.getX(), this.getY(), this.getZ(), this.getScaleX(), this.getScaleY(), this.getScaleZ(), rx, ry,
				rz);
	}

	public final Vector3f getRotation() {
		return (this.rot);
	}

	public final float getRotationX() {
		return (this.rot.x);
	}

	public final float getRotationY() {
		return (this.rot.y);
	}

	public final float getRotationZ() {
		return (this.rot.z);
	}

	public void setScale(float sx, float sy, float sz) {
		this.set(this.getX(), this.getY(), this.getZ(), sx, sy, sz, this.getRotationX(), this.getRotationY(),
				this.getRotationZ());
	}

	public final void set(float x, float y, float z, float sx, float sy, float sz, float rx, float ry, float rz) {
		this.pos.set(x, y, z);
		this.scale.set(sx, sy, sz);
		this.rot.set(rx, ry, rz);

		this.transfMatrix.setIdentity();
		this.transfMatrix.translate(this.pos);
		this.transfMatrix.rotateX(this.rot.x);
		this.transfMatrix.rotateY(this.rot.y);
		this.transfMatrix.rotateZ(this.rot.z);
		this.transfMatrix.scale(this.scale);
	}

	/** render this font model */
	public void render() {
		if (this.hasState(FontModel.STATE_INITIALIZED) == false) {
			this.initialize();
		}

		if (this.hasState(FontModel.STATE_TEXT_UP_TO_DATE) == false) {
			this.updateText();
		}

		if (this.vertexCount == 0 || this.font == null) {
			return;
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this.vao.bind();

		this.font.getTexture().bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		this.vao.draw(GL11.GL_TRIANGLES, 0, this.vertexCount);
	}

	public boolean hasState(int state) {
		return ((this.state & state) == state);
	}

	private void setState(int state) {
		this.state = this.state | state;
	}

	private void unsetState(int state) {
		this.state = this.state & ~(state);
	}

	public Matrix4f getTransformationMatrix() {
		return (this.transfMatrix);
	}

	public String getText() {
		return (this.text);
	}

	@Override
	public String toString() {
		return ("FontModel : " + this.getText());
	}

	public Font getFont() {
		return (this.font);
	}

	/** return text height in gl coordinate system */
	public float getTextWidth() {
		return (this.textWidth * this.getScaleX());
	}

	/** return text height in gl coordinate system */
	public float getTextHeight() {
		return (this.textHeight * this.getScale().y);
	}

	public Vector3f getScale() {
		return (this.scale);
	}

	public float getScaleX() {
		return (this.scale.x);
	}

	public float getScaleY() {
		return (this.scale.y);
	}

	public float getScaleZ() {
		return (this.scale.z);
	}

	public ArrayList<FontChar> getFontChar() {
		return (this.textChars);
	}

	/** request for an update of this font model */
	public void requestUpdate() {
		this.unsetState(FontModel.STATE_TEXT_UP_TO_DATE);
	}

	public Vector4f getFontColor() {
		return (this.color);
	}

	public Vector3f getPosition() {
		return (this.pos);
	}

	public float getX() {
		return (this.pos.x);
	}

	public float getY() {
		return (this.pos.y);
	}

	public float getZ() {
		return (this.pos.z);
	}

	public float getBorderWidth() {
		return (this.borderWidth);
	}

	public void setBorderWidth(float value) {
		this.borderWidth = value;
	}

	public float getBorderEdge() {
		return (this.borderEdge);
	}

	public void setBorderEdge(float value) {
		this.borderEdge = value;
	}

	public Vector2f getOutlineOffset() {
		return (this.outlineOffset);
	}

	public void setOutlineOffset(float x, float y) {
		this.outlineOffset.set(x, y);
	}

	public Vector3f getOutlineColor() {
		return (this.outlineColor);
	}

	public void setOutlineColor(float r, float g, float b) {
		this.outlineColor.set(r, g, b);
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
