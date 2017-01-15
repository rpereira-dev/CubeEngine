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

package com.grillecube.engine.geometry;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/** generate float buffer for opengl*/
public class GLGeometry
{
	public static FloatBuffer generateSphere(int detail, float size)
	{
		return (toFloatBuffer(Sphere.make(detail, size)));
	}
	
	public static FloatBuffer generateSphere(int detail)
	{
		return (GLGeometry.generateSphere(detail, 1));
	}

	/** generate a new quad FloatBuffer (4 * xyz) */
	public static FloatBuffer generateQuad(float size)
	{
		return (toFloatBuffer(Quad.make(size)));
	}
	
	/** generate a new quad FloatBuffer (made with triangles) (6 * xyz) */
	public static FloatBuffer generateQuadTriangles(float size)
	{
		return (toFloatBuffer(Quad.makeWithTriangle(size)));
	}

	/** generate a new quad FloatBuffer (made with triangles with uv) (6 * xyzuv) */
	public static FloatBuffer generateQuadTrianglesUV(float size) 
	{
		return (toFloatBuffer(Quad.makeWithTriangleUV(size)));
	}
	
	private static FloatBuffer toFloatBuffer(float[] vertices)
	{
		FloatBuffer	buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
		return (buffer);
	}
}
