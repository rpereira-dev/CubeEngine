package com.grillecube.client.renderer.opengl.geometry;

public class Quad
{
	/** make a quad with 6 vertices (GL_TRIANGLES) (6 * xyz)*/
	public static float[] makeWithTriangle(float size)
	{
		size /= 2;
		
		float[] vertices = {
				-size, size, 0,
				-size, -size, 0,
				size, -size, 0,
				
				-size, size, 0,
				size, -size, 0,
				size, size, 0
		};
		return (vertices);
	}
	
	/** make a quad with 6 vertices (GL_TRIANGLES) (6 * xyzuv) */
	public static float[] makeWithTriangleUV(float size)
	{
		size /= 2;
		
		float[] vertices = {
				-size, size, 0, 0, 0,
				-size, -size, 0, 0, 1,
				size, -size, 0, 1, 1,
				
				-size, size, 0, 0, 0,
				size, -size, 0, 1, 1,
				size, size, 0, 1, 0
		};
		return (vertices);
	}

	/** make a quad with 4 vertices (GL_QUAD) (4 * xyz) */
	public static float[] make(float size)
	{
		size /= 2;
		
		float[] vertices = {
				-size, size, 0,
				-size, -size, 0,
				size, -size, 0,
				size, size, 0
		};
		return (vertices);
	}

	/** make a quad with 6 vertices (GL_TRIANGLES) (6 * xyzuv) */
	public static float[] makeUV(float size)
	{
		size /= 2;
		
		float[] vertices = {
				-size, size, 0, 0, 0,
				-size, -size, 0, 0, 1,
				size, -size, 0, 1, 1,
				size, size, 0, 1, 0
		};
		return (vertices);
	}

}
