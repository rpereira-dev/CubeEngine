package com.grillecube.client.renderer.opengl.object;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class VertexBuffer implements GLObject
{
	/** opengl id */
	private int _id;
	
	public VertexBuffer()
	{
		this(GL15.glGenBuffers());
	}
	
	public VertexBuffer(int vboID)
	{
		this._id = vboID;
	}
	
	/** free the buffer object */
	public void delete()
	{
		GL15.glDeleteBuffers(this._id);
		this._id = 0;
	}

	/** bind the vbo */
	public void bind(int dst)
	{
		GL15.glBindBuffer(dst, this._id);
	}
	
	/** unbind the vbo */
	public void unbind(int dst)
	{
		GL15.glBindBuffer(dst, 0);
	}

	/** set the vbo data */
	public void bufferData(int dst, FloatBuffer data, int usage)
	{
		this.bind(dst);
		GL15.glBufferData(dst, data, usage);		
		this.unbind(dst);
	}
	
	public void bufferData(int dst, float[] data, int usage)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		this.bufferData(dst, buffer, usage);
	}
}
