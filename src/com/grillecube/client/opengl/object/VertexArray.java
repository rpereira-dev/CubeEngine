package com.grillecube.client.opengl.object;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VertexArray implements GLObject
{
	/** opengl id */
	private int _id;
	
	public VertexArray()
	{
		this(GL30.glGenVertexArrays());
	}
	
	public VertexArray(int vaoID)
	{
		this._id = vaoID;
	}
	
	/** free the buffer object */
	public void delete()
	{
		GL30.glDeleteVertexArrays(this._id);
		this._id = 0;
	}

	/** bind the vao */
	public void bind()
	{
		GL30.glBindVertexArray(this._id);
	}
	
	/** enable the attributes */
	public void enableAttribute(int id)
	{
		GL20.glEnableVertexAttribArray(id);
	}
	
	/** disable the attributes */
	public void disableAttribute(int id)
	{
		GL20.glDisableVertexAttribArray(id);
	}
	
	/** unbind the vao */
	public void unbind()
	{
		GL30.glBindVertexArray(0);
	}

	/** set VertexArray attribute depending on bounded VertexBuffer */
	public void setAttribute(int attributeID, int length, int type, boolean normalized, int stride, int offset)
	{
		GL20.glVertexAttribPointer(attributeID, length, type, normalized, stride, offset);
	}

	/** bind the given VertexBuffer and set the attribute in the VertexArray */
	public void setAttribute(VertexBuffer vbo, int attributeID, int length, int type, boolean normalized, int stride, int offset)
	{
		vbo.bind(GL15.GL_ARRAY_BUFFER);
		this.setAttribute(attributeID, length, type, normalized, stride, offset);
		vbo.unbind(GL15.GL_ARRAY_BUFFER);
	}
	
	/** GL11.glDrawArrays binding */
	public void draw(int dst, int begin, int vertex_count)
	{
		GL11.glDrawArrays(dst, begin, vertex_count);		
	}
}
