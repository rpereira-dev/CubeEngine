package com.grillecube.client.opengl;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.grillecube.client.opengl.object.GLObject;
import com.grillecube.client.opengl.object.Texture;
import com.grillecube.client.opengl.object.VertexArray;
import com.grillecube.client.opengl.object.VertexBuffer;
import com.grillecube.common.logger.Logger;

/** OpenGL helper class */
public class GLH
{
	private static ArrayList<GLObject> _gl_objects;
	
	/** initialize the helper */
	public static void start()
	{
		_gl_objects = new ArrayList<GLObject>();
	}

	/** generate new buffer object */
	public static VertexBuffer glhGenVBO()
	{
		VertexBuffer vbo = new VertexBuffer();
		_gl_objects.add(vbo);
		return (vbo);
	}
	
	/** delete the vbo */
	public static void glhDeleteVBO(VertexBuffer buffer)
	{
		buffer.delete();
		_gl_objects.remove(buffer);
	}
	
	/** generate new buffer object */
	public static VertexArray glhGenVAO()
	{
		VertexArray vao = new VertexArray();
		_gl_objects.add(vao);
		return (vao);
	}
	
	/** delete the vbo */
	public static void glhDeleteVAO(VertexArray vao)
	{
		vao.delete();
		_gl_objects.remove(vao);
	}
	
	/** clean all generated data */
	public static void clean()
	{
		Logger.get().log(Logger.Level.FINE, "Cleaning GLObjects...");

		for (GLObject object : _gl_objects)
		{
			object.delete();
		}
		Logger.get().log(Logger.Level.FINE, _gl_objects.size() + " objects cleaned!");

		_gl_objects.clear();
	}

	/** add the program to the GLOBject list so it is delete when GLH.clean() is called */
	public static void glhAddObject(GLObject object)
	{
		_gl_objects.add(object);
	}

	/** remove the object */
	public static void glhDeleteObject(GLObject object)
	{
		_gl_objects.remove(object);
		object.delete();
	}
	
	
	/*************************** TEXTURES HELPER STARTS HERE: ************************************/

	/** create opengl textures ID */
	public static Texture glhGenTexture()
	{
		ByteBuffer buffer = BufferUtils.createByteBuffer(4 * 1);
		GL11.glGenTextures(1, buffer);
		Texture texture = new Texture(buffer.getInt());
		_gl_objects.add(texture);
		return (texture);			
	}
	
	/** create opengl textures ID and fill it data with the given bufferedimage (rgba format)*/
	public static Texture glhGenTexture(BufferedImage image)
	{
		Texture texture = GLH.glhGenTexture();
		texture.setData(image);
		return (texture);
	}
	
	/** create opengl textures ID and fill it data with the given file */
	public static Texture glhGenTexture(String filepath)
	{
		return (GLH.glhGenTexture(ImageUtils.readImage(filepath)));			
	}
	
	/** delete a texture! */
	public static void glhDeleteTexture(Texture texture)
	{
		texture.delete();
		_gl_objects.remove(texture);
	}
	
	/*************************** TEXTURES HELPER ENDS HERE: ************************************/

}
