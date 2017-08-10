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

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class GLVertexBuffer implements GLObject {
	/** opengl id */
	private int glID;
	private int byteCount;

	public GLVertexBuffer() {
		this(GL15.glGenBuffers());
	}

	public GLVertexBuffer(int vboID) {
		this.glID = vboID;
		this.byteCount = 0;
	}

	/** free the buffer object */
	public void delete() {
		GL15.glDeleteBuffers(this.glID);
		this.glID = 0;
	}

	/** bind the vbo */
	public void bind(int target) {
		GL15.glBindBuffer(target, this.glID);
	}

	/** unbind the vbo */
	public void unbind(int target) {
		GL15.glBindBuffer(target, 0);
	}

	/** set the vbo data */
	public void bufferData(int target, ByteBuffer data, int usage) {
		if (data == null) {
			this.bufferSize(target, 0, GL15.GL_STREAM_DRAW);
			this.byteCount = 0;
		} else {
			GL15.glBufferData(target, data, usage);
			GL15.glBufferSubData(target, 0, data);
			this.byteCount = data.capacity();
		}
	}

	/** update the whole vbo data, using stream draw */
	public void bufferDataUpdate(int target, ByteBuffer data, int capacity) {
		this.bufferData(target, data, GL15.GL_STREAM_DRAW);
	}

	/** set vbo size */
	public void bufferSize(int target, long size, int usage) {
		GL15.glBufferData(target, size, usage);
	}

	public void bufferData(int target, float[] data, int usage) {
		if (data == null) {
			this.bufferData(target, (ByteBuffer) null, usage);
		} else {
			ByteBuffer buffer = BufferUtils.createByteBuffer(data.length * 4);
			for (float f : data) {
				buffer.putFloat(f);
			}
			buffer.flip();
			this.bufferData(target, buffer, usage);
		}
	}

	public void bufferData(int target, int[] data, int usage) {
		if (data == null) {
			this.bufferData(target, (ByteBuffer) null, usage);
		} else {
			ByteBuffer buffer = BufferUtils.createByteBuffer(data.length * 4);
			for (int i : data) {
				buffer.putInt(i);
			}
			buffer.flip();
			this.bufferData(target, buffer, usage);
		}
	}

	public int getByteCount() {
		return (this.byteCount);
	}

	/**
	 * 
	 * @param offset
	 *            : buffer offset
	 * @param floats_count
	 *            : number of floats to get
	 * @return
	 */
	public ByteBuffer getContent(int offset, int byteCount) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(byteCount);
		this.bind(GL15.GL_ARRAY_BUFFER);
		GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, offset, buffer);
		this.unbind(GL15.GL_ARRAY_BUFFER);
		return (buffer);
	}

	public ByteBuffer getContent(int offset) {
		return (this.getContent(offset, this.byteCount));
	}

	public int getGLID() {
		return (this.glID);
	}
}
