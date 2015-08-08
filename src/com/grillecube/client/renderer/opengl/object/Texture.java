package com.grillecube.client.renderer.opengl.object;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.grillecube.client.renderer.opengl.ImageUtils;

public class Texture implements GLObject
{
	/** opengl texture id */
	private int _glID;
	
	/** texture size */
	private int _width;
	private int _height;
	
	public Texture(int glID)
	{
		this._glID = glID;
		this._width = 0;
		this._height = 0;
	}
	
	public Texture()
	{
		this(0);
	}

	@Override
	public void delete()
	{
		GL11.glDeleteTextures(this._glID);
	}

	/** set pixels data */
	public void setData(BufferedImage img)
	{
		if (img == null)
		{
			return ;
		}
		
		byte[] pixels = ImageUtils.getImagePixels(img);
		ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length);
		buffer.put(pixels);
		buffer.flip();
		
		this.bind(GL11.GL_TEXTURE_2D);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
							img.getWidth(), img.getHeight(),
							0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		this.unbind(GL11.GL_TEXTURE_2D);
		
		this._width = img.getWidth();
		this._height = img.getHeight();
	}
	
	public int getWidth()
	{
		return (this._width);
	}
	
	public int getHeight()
	{
		return (this._height);
	}
	
	public void bind(int target)
	{
		GL11.glBindTexture(target, this._glID);
	}
	
	public void unbind(int target)
	{
		GL11.glBindTexture(target, 0);
	}
}
