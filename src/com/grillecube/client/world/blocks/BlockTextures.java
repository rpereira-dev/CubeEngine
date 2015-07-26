package com.grillecube.client.world.blocks;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.grillecube.server.Game;

import fr.toss.lib.Logger;

public class BlockTextures
{
	/** textures ID */
	public static final int NONE 		= 0;
	public static final int DIRT 		= 1;
	public static final int GRASS_TOP	= 2;
	public static final int GRASS_SIDE	= 3;
	public static final int STONE		= 4;
	public static final int MAX_ID 		= 5;
	
	/** texture constants */
	public static final int	TEXTURE_WIDTH	= 16;
	public static final int	TEXTURE_HEIGHT 	= 16;
	
	/** texture map resolution */
	public static final int	RESOLUTION_16 		= 0;
	public static final int	RESOLUTION_8 		= 1;
	public static final int	RESOLUTION_4 		= 2;
	public static final int	RESOLUTION_2		= 3;
	public static final int	RESOLUTION_MAX_ID	= 4;
	
	/** opengl texture id arrays */
	private static int[]	_gl_texture_map;
	
	/** create opengl textures ID */
	private static void allocateGLTextures()
	{
		ByteBuffer	buffer;				
	
		buffer = BufferUtils.createByteBuffer(4 * RESOLUTION_MAX_ID);
		GL11.glGenTextures(RESOLUTION_MAX_ID, buffer);
		_gl_texture_map = new int[RESOLUTION_MAX_ID];
		for (int i = 0 ; i < RESOLUTION_MAX_ID ; i++)
		{
			_gl_texture_map[i] = buffer.getInt();			
		}
	}

	/** return a byte array which contains texture pixels */
	private static byte[]	getAtlasPixels(BufferedImage img)
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

	/** reduce the size of the given image by 2 */
	private static void	generateMipmapAtlas(byte[] mipmap, int width, int height, int resolutionID)
	{
		ByteBuffer	buffer;

		buffer = BufferUtils.createByteBuffer(mipmap.length);
		buffer.put(mipmap);
		buffer.flip();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gl_texture_map[resolutionID]);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
							0,
							GL11.GL_RGBA,
							width, height,
							0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	}
	
	/** set opengl data from img atlas */
	private static void	generateGLTextures(BufferedImage img)
	{
		byte[]	pixels;
		
		pixels = getAtlasPixels(img);
		generateMipmapAtlas(pixels, img.getWidth(), img.getHeight(), BlockTextures.RESOLUTION_16);
	}
	
	/** create block texture map at given resolution */
	private static BufferedImage	createTextureAtlas(BufferedImage[] textures)
	{
		BufferedImage	atlas;
		Graphics		g;
		int				y;
		
		atlas = new BufferedImage(BlockTextures.TEXTURE_WIDTH,
									BlockTextures.TEXTURE_HEIGHT * BlockTextures.MAX_ID,
									BufferedImage.TYPE_INT_ARGB);
		g = atlas.getGraphics();
		y = 0;
		for (BufferedImage img : textures)
		{
			g.drawImage(img, 0, y, null);
			y += BlockTextures.TEXTURE_HEIGHT;
		}
		
		g.dispose();
		try
		{
			ImageIO.write(atlas, "png", new File("./assets/textures/blocks/atlas.png"));
		}
		catch (IOException e)
		{
			Game.getLogger().log(Logger.Level.WARNING, "Cant save texture atlas: " + e.getMessage());
		}
		return (atlas);		
	}

	private static BufferedImage	readImagePixels(String filename)
	{
		BufferedImage	image;
		
		filename = "./assets/textures/blocks/" + filename + ".png";		
		image = null;
		try
		{
			image = ImageIO.read(new File(filename));
		}
		catch (IOException e)
		{
			Game.getLogger().log(Logger.Level.WARNING, "Cant get texture file data: " + filename);
		}
		return (image);
	}
	
	/** initialize opengl textures */
	public static void	initTextures()
	{
		BufferedImage	textures[];	//array which will contains every textures

		textures = new BufferedImage[BlockTextures.MAX_ID];
		
		textures[BlockTextures.NONE] 		= readImagePixels("none");
		textures[BlockTextures.DIRT] 		= readImagePixels("dirt");
		textures[BlockTextures.GRASS_TOP] 	= readImagePixels("grass_top");
		textures[BlockTextures.GRASS_SIDE] 	= readImagePixels("grass_side");
		
		allocateGLTextures();	//allocate opengl texture name for each resolution
		generateGLTextures(createTextureAtlas(textures));
	}
	
	/** return the opengl texture map for the given resolution */
	public static int	getGLTextureAtlas(int resolutionID)
	{
		return (_gl_texture_map[resolutionID]);
	}
}
