package com.grillecube.client.renderer.world;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;

public class Heightmap {

	private static final int FLOAT_PER_VERTICES = 3; // x, y, z

	private Matrix4f _matrix;
	private final Vector3f _pos;
	private final Vector3f _rot;
	private final Vector3f _scale;

	private int _width;
	private int _height;
	private int _vertex_count;

	private GLVertexArray _vao;
	private GLVertexBuffer _vbo;

	public Heightmap() {
		this._matrix = new Matrix4f();
		this._pos = new Vector3f();
		this._rot = new Vector3f();
		this._scale = new Vector3f();
	}

	public Heightmap initGL() {
		this._vao = GLH.glhGenVAO();
		this._vbo = GLH.glhGenVBO();

		this._vao.bind();

		this._vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._vao.setAttribute(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
		this._vao.enableAttribute(0);
		this._vbo.unbind(GL15.GL_ARRAY_BUFFER);
		this._vao.unbind();
		return (this);
	}

	public void setPosition(float x, float y, float z) {
		this._pos.set(x, y, z);
		Matrix4f.createTransformationMatrix(this._matrix, this._pos, this._rot, this._scale);
	}

	public void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public void setRotation(float x, float y, float z) {
		this._rot.set(x, y, z);
		Matrix4f.createTransformationMatrix(this._matrix, this._pos, this._rot, this._scale);
	}

	public void setRotation(Vector3f rot) {
		this.setRotation(rot.x, rot.y, rot.z);
	}

	public void setScale(float x, float y, float z) {
		this._scale.set(x, y, z);
		Matrix4f.createTransformationMatrix(this._matrix, this._pos, this._rot, this._scale);
	}

	public void setScale(Vector3f scale) {
		this.setScale(scale.x, scale.y, scale.z);
	}

	public void generateVertices(float[][] heights) {

		if (this._vao == null) {
			throw new UnsupportedOperationException("Tried to generate vertices on an un-initialized Heightmap");
		}

		this._width = heights.length;
		this._height = heights[0].length;
		this._vertex_count = 6 * this._width * this._height;

		ByteBuffer buffer = BufferUtils.createByteBuffer(this._vertex_count * FLOAT_PER_VERTICES * 4);

		for (int x = 0; x < this._width - 1; x++) {
			for (int z = 0; z < this._height - 1; z++) {

				// position

				buffer.putFloat(x);
				buffer.putFloat(heights[x][z]);
				buffer.putFloat(z);

				buffer.putFloat(x + 1);
				buffer.putFloat(heights[x + 1][z]);
				buffer.putFloat(z);

				buffer.putFloat(x);
				buffer.putFloat(heights[x][z + 1]);
				buffer.putFloat(z + 1);

			}
		}
		buffer.flip();
		this._vbo.bufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	public int getVertexCount() {
		return (this._vertex_count);
	}

	public int getWidth() {
		return (this._width);
	}

	public int getHeight() {
		return (this._height);
	}

	public Matrix4f getTransformationMatrix() {
		return (this._matrix);
	}

	public void delete() {
		this._vao.delete();
		this._vbo.delete();
	}

}
