package com.grillecube.client.renderer.font;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class FontModel
{
	public static final int FONT_TEXTURE_WIDTH  = 512;
	public static final int FONT_TEXTURE_HEIGHT	= 512;
	public static final int FONT_CHAR_WIDTH		= 32;
	public static final int FONT_CHAR_HEIGHT	= 64;
	public static final char FONT_ASCII_OFFSET	= ' ';
	
	public static final int FONT_CHAR_PER_COL		= (FONT_TEXTURE_HEIGHT / FONT_CHAR_HEIGHT);
	public static final int FONT_CHAR_PER_LINE 		= (FONT_TEXTURE_WIDTH / FONT_CHAR_WIDTH);
	public static final float FONT_UV_UNIT_WIDTH	= (FONT_CHAR_WIDTH / (float)FONT_TEXTURE_WIDTH);
	public static final float FONT_UV_UNIT_HEIGHT	= (FONT_CHAR_HEIGHT / (float)FONT_TEXTURE_HEIGHT);
	
	public static final float DEFAULT_FONT_SIZE		= 0.1f;
	
	/** opengl IDs */
	private int	_vaoID;
	private int	_vboID;
	private int	_vertex_count;
	
	/** model data */
	private Vector3f	_pos;
	private Vector3f	_scale;
	private Vector3f	_rot;
	private Vector4f	_color;
	private Font 		_font;
	
	private Matrix4f 	_matrix;

	public FontModel(Font font, String text)
	{		
		this._vaoID = GL30.glGenVertexArrays();
		this._vboID = GL15.glGenBuffers();
		this._matrix = new Matrix4f();
		
		GL30.glBindVertexArray(this._vaoID);
		{
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);

			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, 0);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, 3 * 4);
			GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, (3 + 2) * 4);

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		GL30.glBindVertexArray(0);
		
		this.setFont(font);
		
		this._pos = new Vector3f(0, 0, 0);
		this._rot = new Vector3f(0, 0, 0);
		this._scale = new Vector3f(DEFAULT_FONT_SIZE / 2.0f, DEFAULT_FONT_SIZE, DEFAULT_FONT_SIZE);
		this._color = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
		
		this.setText(text);
	}

	public FontModel(Font font)
	{
		this(font, "");
	}
	
	/** destroy the model */
	public void destroy()
	{
		GL30.glDeleteVertexArrays(this._vaoID);
		GL15.glDeleteBuffers(this._vboID);
	}
	
	/** rebuild the mesh depending on the given string */
	public void setText(String str)
	{
		FloatBuffer	buffer;
		float[]		vertices;
				
		vertices = this.generateFontBuffer(str);

		buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
		
		GL30.glBindVertexArray(this._vaoID);
		{
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this._vboID);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		GL30.glBindVertexArray(0);
		
		this._vertex_count = vertices.length / 5;
	}

	/** each char is a quad (4 vertex) of 5 floats (pos + uv) */
	private float[] generateFontBuffer(String str)
	{
		float[] vertices = new float[str.length() * (4 * (3 + 2 + 4))];
		
		int		i;
		int		charID;
		int		line;
		int		col;
		float	x;
		float	y;
		float 	z;
		int		index;

		x = 0;
		y = 0;
		z = 0;
		index = 0;
		for (i = 0 ; i < str.length() ; i++)
		{
			if (str.charAt(i) == '\n')
			{
				y--;
				x = 0;
				continue ;
			}
			charID = str.charAt(i) - FONT_ASCII_OFFSET;
			line = charID / FONT_CHAR_PER_LINE;
			col = charID % FONT_CHAR_PER_LINE;

			vertices[index++] = x;
			vertices[index++] = y;
			vertices[index++] = z;
			vertices[index++] = FONT_UV_UNIT_WIDTH * col;
			vertices[index++] = FONT_UV_UNIT_HEIGHT * line;
			vertices[index++] = this._color.x;
			vertices[index++] = this._color.y;
			vertices[index++] = this._color.z;
			vertices[index++] = this._color.w;
			
			vertices[index++] = x;
			vertices[index++] = y - 1;
			vertices[index++] = z;
			vertices[index++] = FONT_UV_UNIT_WIDTH * col;
			vertices[index++] = FONT_UV_UNIT_HEIGHT * line + FONT_UV_UNIT_HEIGHT;
			vertices[index++] = this._color.x;
			vertices[index++] = this._color.y;
			vertices[index++] = this._color.z;
			vertices[index++] = this._color.w;
			
			vertices[index++] = x + 1;
			vertices[index++] = y - 1;
			vertices[index++] = z;
			vertices[index++] = FONT_UV_UNIT_WIDTH * col + FONT_UV_UNIT_WIDTH;
			vertices[index++] = FONT_UV_UNIT_HEIGHT * line + FONT_UV_UNIT_HEIGHT;
			vertices[index++] = this._color.x;
			vertices[index++] = this._color.y;
			vertices[index++] = this._color.z;
			vertices[index++] = this._color.w;

			vertices[index++] = x + 1;
			vertices[index++] = y;
			vertices[index++] = z;
			vertices[index++] = FONT_UV_UNIT_WIDTH * col + FONT_UV_UNIT_WIDTH;
			vertices[index++] = FONT_UV_UNIT_HEIGHT * line;
			vertices[index++] = this._color.x;
			vertices[index++] = this._color.y;
			vertices[index++] = this._color.z;
			vertices[index++] = this._color.w;
		
			x++;
			x += this._font.getStep();
		}
		
		return (vertices);
	}
	
	public void setFont(Font font)
	{
		this._font = font;
	}

	public void	setPosition(float x, float y, float z)
	{
		this._pos.x = x;
		this._pos.y = y;
		this._pos.z = z;
		this.updateTransformationMatrix();
	}

	public void	setRotation(float x, float y, float z)
	{
		this._rot.x = x;
		this._rot.y = y;
		this._rot.z = z;
		this.updateTransformationMatrix();
	}
	
	public void	setScale(float x, float y, float z)
	{
		this._scale.x = x;
		this._scale.y = y;
		this._scale.z = z;
		this.updateTransformationMatrix();
	}
	
	public void	setColor(float r, float g, float b)
	{
		this._color.x = r;
		this._color.y = g;
		this._color.z = b;
	}
	
	public void updateTransformationMatrix()
	{
		this._matrix.setIdentity();
		this._matrix.translate(this._pos);
		this._matrix.rotate(this._rot.x, new Vector3f(1, 0, 0));
		this._matrix.rotate(this._rot.y, new Vector3f(0, 1, 0));
		this._matrix.rotate(this._rot.z, new Vector3f(0, 0, 1));
		this._matrix.scale(this._scale);
	}
	
	public void render()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this._font.getTextureID());

		GL30.glBindVertexArray(this._vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL11.glDrawArrays(GL11.GL_QUADS, 0, this._vertex_count);
		
	}

	public Matrix4f getTransformationMatrix()
	{
		return (this._matrix);
	}

	public Vector4f	getFontColor()
	{
		return (this._color);
	}
}
