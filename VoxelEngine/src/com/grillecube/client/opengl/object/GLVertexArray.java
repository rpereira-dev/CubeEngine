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

package com.grillecube.client.opengl.object;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import com.grillecube.client.opengl.GLH;

public class GLVertexArray implements GLObject {

	/** opengl id */
	private int _id;

	public GLVertexArray() {
		this(GL30.glGenVertexArrays());
	}

	public GLVertexArray(int vaoID) {
		this._id = vaoID;
	}

	/** free the buffer object */
	public void delete() {
		GL30.glDeleteVertexArrays(this._id);
		this._id = 0;
	}

	/** bind the vao */
	public void bind() {
		GL30.glBindVertexArray(this._id);
	}

	/** enable the attributes */
	public void enableAttribute(int id) {
		GL20.glEnableVertexAttribArray(id);
	}

	/** disable the attributes */
	public void disableAttribute(int id) {
		GL20.glDisableVertexAttribArray(id);
	}

	/** unbind the vao */
	public void unbind() {
		GL30.glBindVertexArray(0);
	}

	/** set VertexArray attribute depending on bounded VertexBuffer */
	public void setAttribute(int index, int size, int type, boolean normalized, int stride, long pointer) {
		GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
	}

	/** set VertexArray attribute depending on bounded VertexBuffer */
	public void setAttributei(int index, int size, int type, int stride, long pointer) {
		GL30.glVertexAttribIPointer(index, size, type, stride, pointer);
	}

	/** set VertexArray attribute depending on bounded VertexBuffer */
	public void setAttributei(GLVertexBuffer vbo, int index, int size, int type, int stride, long pointer) {
		vbo.bind(GL15.GL_ARRAY_BUFFER);
		GL30.glVertexAttribIPointer(index, size, type, stride, pointer);
//		vbo.unbind(GL15.GL_ARRAY_BUFFER);
	}

	/** bind the given VertexBuffer and set the attribute in the VertexArray */
	public void setAttribute(GLVertexBuffer vbo, int attributeID, int length, int type, boolean normalized, int stride,
			int offset) {
		vbo.bind(GL15.GL_ARRAY_BUFFER);
		this.setAttribute(attributeID, length, type, normalized, stride, offset);
//		vbo.unbind(GL15.GL_ARRAY_BUFFER);
	}

	/** bind the given VertexBuffer and set the attribute in the VertexArray */
	public void setAttribute(float[] floats, int attributeID, int length, int type, boolean normalized, int stride,
			int offset) {
		GLVertexBuffer vbo = GLH.glhGenVBO();
		vbo.bind(GL15.GL_ARRAY_BUFFER);
		vbo.bufferData(GL15.GL_ARRAY_BUFFER, floats, GL15.GL_STATIC_DRAW);
		this.setAttribute(attributeID, length, type, normalized, stride, offset);
//		vbo.unbind(GL15.GL_ARRAY_BUFFER);
	}

	/**
	 * bind the given vertex buffer, and set it as an instanced attribute to the
	 * vertex array
	 */
	public void setAttributeInstanced(GLVertexBuffer vbo, int attributeID, int length, int type, boolean normalized,
			int stride, int offset) {

		vbo.bind(GL15.GL_ARRAY_BUFFER);
		this.setAttributeInstanced(attributeID, length, type, normalized, stride, offset);
//		vbo.unbind(GL15.GL_ARRAY_BUFFER);
	}

	/** Set the bound vbo as an instanced attribute to the vertex array */
	public void setAttributeInstanced(int attributeID, int length, int type, boolean normalized, int stride,
			int offset) {
		this.setAttribute(attributeID, length, type, normalized, stride, offset);
		GL33.glVertexAttribDivisor(attributeID, 1);
	}

	/** GL11.glDrawArrays binding */
	public void draw(int dst, int begin, int vertex_count) {
		// if (GLH.glhGetBoundVertexArray() != this._id) {
		// Logger.get().log(Level.WARNING, "Tried to draw a GLVertexArray which
		// wasnt bound!");
		// return;
		// }
		GLH.glhDrawArrays(dst, begin, vertex_count);
	}

	public void drawInstanced(int mode, int first, int count, int primcount) {

		// if (GLH.glhGetBoundVertexArray() != this._id) {
		// Logger.get().log(Level.WARNING, "Tried to draw a GLVertexArray which
		// wasnt bound!");
		// return;
		// }

		GLH.glhDrawArraysInstanced(mode, first, count, primcount);
	}
}
