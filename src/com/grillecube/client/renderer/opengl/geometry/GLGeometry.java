package com.grillecube.client.renderer.opengl.geometry;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.grillecube.client.world.Weather;

/** generate float buffer for opengl*/
public class GLGeometry
{
	/** generate cube vertices */
	public static FloatBuffer generateCube(float size)
	{
		return (toFloatBuffer(Cube.makeWithFace(size)));
	}

	public static FloatBuffer generateSphere(int detail)
	{
		return (toFloatBuffer(Sphere.make(detail, Weather.SUN_DIST)));
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
