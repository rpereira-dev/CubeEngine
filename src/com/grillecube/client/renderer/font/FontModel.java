package com.grillecube.client.renderer.font;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.VertexArray;
import com.grillecube.client.opengl.object.VertexBuffer;

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
	
	public static final Vector3f DEFAULT_FONT_SIZE	= new Vector3f(0.04f, 0.1f, 0);
	
	public static final long DEFAULT_TIMER	= 5000;	//5sec
	public static final long INFINITE_TIMER	= Long.MAX_VALUE;
	
	/** opengl IDs */
	private VertexArray _vao;
	private VertexBuffer _vbo;
	private int	_vertex_count;
	
	/** model data */
	private Vector3f _pos;
	private Vector3f _scale;
	private Vector3f _rot;
	private Font _font;
	
	/** status */
	private boolean _initialized;
	private boolean _text_up_to_date;

	/** transf matrix */
	private Matrix4f 	_matrix;
	
	/** text */
	private String _text;
	
	/** timers */
	private long _timer;
	private long _last_for;
	private long _last_tick;
	
	public FontModel(Font font, String text, long last_for)
	{
		this._initialized = false;
		this._text_up_to_date = false;
		this._text = text;
		this._font = font;
		this._matrix = new Matrix4f();
		this._timer = 0;
		this._last_for = last_for;
		this._last_tick = System.currentTimeMillis();
		this._pos = new Vector3f(0, 0, 0);
		this._rot = new Vector3f(0, 0, 0);
		this._scale = DEFAULT_FONT_SIZE;
	}
	
	private void initialize()
	{		
		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();
		
		this._vao.bind();
		{
			this._vbo.bind(GL15.GL_ARRAY_BUFFER);

			this._vao.setAttribute(0, 3, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, 0);
			this._vao.setAttribute(1, 2, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, 3 * 4);
			this._vao.setAttribute(2, 4, GL11.GL_FLOAT, false, (3 + 2 + 4) * 4, (3 + 2) * 4);

			this._vbo.unbind(GL15.GL_ARRAY_BUFFER);
		}
		this._vao.unbind();

		this._initialized = true;		
	}

	/** destroy the model */
	public void destroy()
	{
		if (this._initialized)
		{
			GLH.glhDeleteObject(this._vao);
			GLH.glhDeleteObject(this._vbo);
			this._initialized = false;
		}
	}
	
	/** rebuild the mesh depending on the given string */
	public void setText(String str)
	{
		this._text = str;
		this._text_up_to_date = false;
	}
	
	/** update FontModel VertexBuffer vertices depending on 'this._text' */
	private void updateText()
	{
		float[] vertices = this.generateFontBuffer(this._text);
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		this._vertex_count = vertices.length / 5;	
		this._text_up_to_date = true;
	}

	/** each char is a quad (4 vertex) of 5 floats (pos + uv) */
	private float[] generateFontBuffer(String str)
	{
		float[] vertices = new float[str.length() * (4 * (3 + 2 + 4))];
		
		int i;
		int charID;
		int line;
		int col;
		float x = 0;
		float y = 0;
		float z = 0;
		int index = 0;
		Vector4f color = new Vector4f(0, 0, 0, 1);

		for (i = 0 ; i < str.length() ; i++)
		{
			char c = str.charAt(i);
			if (c == '\n')
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
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;
			
			vertices[index++] = x;
			vertices[index++] = y - 1;
			vertices[index++] = z;
			vertices[index++] = FONT_UV_UNIT_WIDTH * col;
			vertices[index++] = FONT_UV_UNIT_HEIGHT * line + FONT_UV_UNIT_HEIGHT;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;
			
			vertices[index++] = x + 1;
			vertices[index++] = y - 1;
			vertices[index++] = z;
			vertices[index++] = FONT_UV_UNIT_WIDTH * col + FONT_UV_UNIT_WIDTH;
			vertices[index++] = FONT_UV_UNIT_HEIGHT * line + FONT_UV_UNIT_HEIGHT;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;

			vertices[index++] = x + 1;
			vertices[index++] = y;
			vertices[index++] = z;
			vertices[index++] = FONT_UV_UNIT_WIDTH * col + FONT_UV_UNIT_WIDTH;
			vertices[index++] = FONT_UV_UNIT_HEIGHT * line;
			vertices[index++] = color.x;
			vertices[index++] = color.y;
			vertices[index++] = color.z;
			vertices[index++] = color.w;

			x++;
			x += this._font.getStep();
		}
		
		return (vertices);
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

	private void updateTransformationMatrix()
	{
		this._matrix.setIdentity();
		this._matrix.translate(this._pos);
		this._matrix.rotate(this._rot.x, new Vector3f(1, 0, 0));
		this._matrix.rotate(this._rot.y, new Vector3f(0, 1, 0));
		this._matrix.rotate(this._rot.z, new Vector3f(0, 0, 1));
		this._matrix.scale(this._scale);
	}
	
	/** render this font model */
	public void render()
	{
		if (this._initialized == false)
		{
			this.initialize();
		}
		
		if (this._text_up_to_date == false)
		{
			this.updateText();
		}
	
		if (this._vertex_count == 0)
		{
			return ;
		}
		

		this._vao.bind();
		this._vao.enableAttribute(0);
		this._vao.enableAttribute(1);
		this._vao.enableAttribute(2);

		this._font.getTexture().bind(GL11.GL_TEXTURE_2D);
		this._vao.draw(GL11.GL_QUADS, 0, this._vertex_count);
		this._font.getTexture().unbind(GL11.GL_TEXTURE_2D);

		this._vao.disableAttribute(0);
		this._vao.disableAttribute(1);
		this._vao.disableAttribute(2);
		this._vao.unbind();

		
	}

	public Matrix4f getTransformationMatrix()
	{
		return (this._matrix);
	}

	/** update the timer */
	public void update()
	{
		if (this._timer != FontModel.INFINITE_TIMER)
		{
			long t = System.currentTimeMillis();
			
			this._timer += (System.currentTimeMillis() - this._last_tick);
			this._last_tick = t;
		}
	}
	
	/** return true if the timer has ended */
	public boolean hasTimerEnded()
	{
		return (this._timer >= this._last_for);
	}
	
	/** end the timer */
	public void	endTimer()
	{
		this._timer = this._last_for;
	}
	
	/** restart the timer */
	public void	restartTimer()
	{
		this._timer = 0;
	}
}
