package com.grillecube.client.renderer.model;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.world.terrain.Mesh;

public class ModelMesh extends Mesh {

	/** bytes per vertex */
	public static final int BYTES_PER_VERTEX = (3 + 2 + 3 + 3 + 3) * 4;

	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	protected void setAttributes(GLVertexArray vao, GLVertexBuffer vbo) {

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
	public void setVertices(ByteBuffer vertices) {
		// set vertices
		super.setVertices(vertices, BYTES_PER_VERTEX);
	}
}
