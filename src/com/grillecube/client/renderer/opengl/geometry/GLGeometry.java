package com.grillecube.client.renderer.opengl.geometry;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.grillecube.client.world.Weather;

/** generate float buffer for opengl*/
public class GLGeometry
{
	/** generate cube vertices */
	public static FloatBuffer generateCube(int size)
	{
		float[] vertices = Cube.makeWithFace(size);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
		return (buffer);
	}

	public static FloatBuffer generateSphere(int detail)
	{
		float[]	vertices = Sphere.make(detail, Weather.SUN_DIST);
		FloatBuffer	buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices);
		buffer.flip();
		return (buffer);
	}
}
