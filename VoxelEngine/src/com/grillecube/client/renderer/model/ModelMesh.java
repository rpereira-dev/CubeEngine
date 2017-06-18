package com.grillecube.client.renderer.model;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.model.dae.datastructures.MeshData;
import com.grillecube.client.renderer.world.terrain.Mesh;

public class ModelMesh extends Mesh {

	/** bytes per vertex */
	public static final int BYTES_PER_VERTEX = (3 + 2 + 3 + 3 + 3) * 4;

	/** the index buffer */
	private GLVertexBuffer indexVBO;
	private int indexCount;

	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	protected void setAttributes(GLVertexArray vao, GLVertexBuffer vbo) {

		// indices
		this.indexVBO = GLH.glhGenVBO();
		this.indexVBO.bind(GL15.GL_ELEMENT_ARRAY_BUFFER);

		// attributes
		vao.setAttribute(0, 3, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, 0); // xyz
		vao.setAttribute(1, 2, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, 3 * 4); // uv
		vao.setAttribute(2, 3, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, (3 + 2) * 4); // normals
		vao.setAttributei(3, 3, GL11.GL_INT, BYTES_PER_VERTEX, (3 + 3 + 2) * 4); // jointIDs
		vao.setAttribute(4, 3, GL11.GL_FLOAT, false, BYTES_PER_VERTEX, (3 + 3 + 3 + 2) * 4); // jointWeights

		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
		vao.enableAttribute(3);
		vao.enableAttribute(4);
	}

	/** set mesh data */
	public void set(MeshData meshData) {

		// set indices
		this.indexVBO.bind(GL15.GL_ELEMENT_ARRAY_BUFFER);
		this.indexVBO.bufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, meshData.getIndices(), GL15.GL_STATIC_DRAW);
		this.indexCount = meshData.getIndices().length;

		// set vertices
		int vertexCount = meshData.getVertexCount();
		float[] pos = meshData.getPosition();
		float[] uvs = meshData.getTextureCoords();
		float[] normals = meshData.getNormals();
		int[] jointIDs = meshData.getJointIds();
		float[] weights = meshData.getVertexWeights();

		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertexCount * BYTES_PER_VERTEX);

		for (int i = 0; i < vertexCount; i++) {

			int j = i * 3;
			int k = i * 2;

			byteBuffer.putFloat(pos[j]);
			byteBuffer.putFloat(pos[j + 1]);
			byteBuffer.putFloat(pos[j + 2]);

			byteBuffer.putFloat(uvs[k]);
			byteBuffer.putFloat(uvs[k + 1]);

			byteBuffer.putFloat(normals[j]);
			byteBuffer.putFloat(normals[j + 1]);
			byteBuffer.putFloat(normals[j + 2]);

			byteBuffer.putInt(jointIDs[j]);
			byteBuffer.putInt(jointIDs[j + 1]);
			byteBuffer.putInt(jointIDs[j + 2]);

			byteBuffer.putFloat(weights[j]);
			byteBuffer.putFloat(weights[j + 1]);
			byteBuffer.putFloat(weights[j + 2]);
		}

		byteBuffer.flip();

		super.setVertices(byteBuffer, BYTES_PER_VERTEX);
	}

	/** get the numbre of indices */
	public int getIndexCount() {
		return (this.indexCount);
	}
}
