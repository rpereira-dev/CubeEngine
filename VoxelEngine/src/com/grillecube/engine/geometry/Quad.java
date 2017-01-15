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
