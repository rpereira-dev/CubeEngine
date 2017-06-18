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

package com.grillecube.client.renderer.geometry;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/** generate float buffer for opengl */
public class GLGeometry {
	public static ByteBuffer generateSphere(int detail, float size) {
		return (toByteBuffer(Sphere.make(detail, size)));
	}

	public static ByteBuffer generateSphere(int detail) {
		return (GLGeometry.generateSphere(detail, 1));
	}

	/** generate a new quad FloatBuffer (4 * xyz) */
	public static ByteBuffer generateQuad(float size) {
		return (toByteBuffer(Quad.make(size)));
	}

	/** generate a new quad FloatBuffer (made with triangles) (6 * xyz) */
	public static ByteBuffer generateQuadTriangles(float size) {
		return (toByteBuffer(Quad.makeWithTriangle(size)));
	}

	/**
	 * generate a new quad FloatBuffer (made with triangles with uv) (6 * xyzuv)
	 */
	public static ByteBuffer generateQuadTrianglesUV(float size) {
		return (toByteBuffer(Quad.makeWithTriangleUV(size)));
	}

	private static ByteBuffer toByteBuffer(float[] vertices) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(vertices.length * 4);
		for (float f : vertices) {
			buffer.putFloat(f);
		}
		buffer.flip();
		return (buffer);
	}
}
