package com.grillecube.client.renderer.world.particles;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.geometry.Cube;
import com.grillecube.client.renderer.world.terrain.Mesh;

public class CubeMesh extends Mesh {

	// transf matrix + color + health
	public static final int FLOATS_PER_CUBE_INSTANCE = 16 + 4 + 1;
	public static final int BYTES_PER_VERTEX_INSTANCED = FLOATS_PER_CUBE_INSTANCE * 4;

	public static final int BYTES_PER_CUBE_VERTEX = 4 * 4;

	@Override
	protected void setAttributes(GLVertexArray vao, GLVertexBuffer vbo) {

		float[] vertices = Cube.makeWithTrianglesAndFaces(1);
		ByteBuffer buffer = BufferUtils.createByteBuffer(vertices.length * 4);
		buffer.asFloatBuffer().put(vertices);

		super.setVertices(buffer, BYTES_PER_CUBE_VERTEX);
		vao.setAttribute(0, 4, GL11.GL_FLOAT, false, BYTES_PER_CUBE_VERTEX, 0);
		vao.enableAttribute(0);
	}

	protected void setAttributesInstanced() {
		GLVertexArray vao = super.vao;

		vao.setAttributeInstanced(1, 4, GL11.GL_FLOAT, false, BYTES_PER_VERTEX_INSTANCED, 0 * 4);
		vao.setAttributeInstanced(2, 4, GL11.GL_FLOAT, false, BYTES_PER_VERTEX_INSTANCED, 4 * 4);
		vao.setAttributeInstanced(3, 4, GL11.GL_FLOAT, false, BYTES_PER_VERTEX_INSTANCED, 8 * 4);
		vao.setAttributeInstanced(4, 4, GL11.GL_FLOAT, false, BYTES_PER_VERTEX_INSTANCED, 12 * 4);
		vao.setAttributeInstanced(5, 4, GL11.GL_FLOAT, false, BYTES_PER_VERTEX_INSTANCED, 16 * 4);
		vao.setAttributeInstanced(6, 1, GL11.GL_FLOAT, false, BYTES_PER_VERTEX_INSTANCED, 20 * 4);

		vao.enableAttribute(1);
		vao.enableAttribute(2);
		vao.enableAttribute(3);
		vao.enableAttribute(4);
		vao.enableAttribute(5);
		vao.enableAttribute(6);
	}

}
