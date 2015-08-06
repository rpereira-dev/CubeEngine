package com.grillecube.client.ressources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import fr.toss.lib.Logger;

public class TextureManager
{	
	/** create opengl textures ID */
	public static int	newGLTexture()
	{
		ByteBuffer	buffer;				
	
		buffer = BufferUtils.createByteBuffer(4 * 1);
		GL11.glGenTextures(1, buffer);
		return (buffer.getInt());			
	}
	

	/** set the given opengl textureID pixels data */
	public static void setGLTextureData(int gl_textureID, BufferedImage img)
	{
		ByteBuffer	buffer;
		byte[]		pixels;
		
		if (img == null)
		{
			return ;
		}
		pixels = TextureManager.getImagePixels(img);
		buffer = BufferUtils.createByteBuffer(pixels.length);
		buffer.put(pixels);
		buffer.flip();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl_textureID);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
							0,
							GL11.GL_RGBA,
							img.getWidth(), img.getHeight(),
							0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	}
	
	/** return an image for this file */
	public static BufferedImage	readImage(String filepath)
	{
		BufferedImage	image;
		
		image = null;
		try
		{
			image = ImageIO.read(new File(filepath));
		}
		catch (IOException e)
		{
			Logger.get().log(Logger.Level.WARNING, "Cant get texture file: " + filepath);
		}
		return (image);
	}
	
	/** return a byte array which contains texture pixels in RGBA format */
	public static byte[] getImagePixels(BufferedImage img)
	{
		byte[]	pixels;
		int[]	buffer;
		
		buffer = new int[img.getWidth() * img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), buffer, 0, img.getWidth());
		
		int	i;
		
		i = 0;
		pixels = new byte[img.getWidth() * img.getHeight() * 4];
		for (int value : buffer)
		{
			pixels[i++] = (byte) (value >> 16 & 0xFF);	//r
			pixels[i++] = (byte) (value >> 8 & 0xFF);	//g
			pixels[i++] = (byte) (value >> 0 & 0xFF);	//b
			pixels[i++] = (byte) (value >> 24 & 0xFF);	//a
		}
		return (pixels);
	}


	public static int loadGLTexture(String filepath)
	{
		int	glID;
		
		glID = TextureManager.newGLTexture();
		TextureManager.setGLTextureData(glID, TextureManager.readImage(filepath));
		return (glID);
	}
}
