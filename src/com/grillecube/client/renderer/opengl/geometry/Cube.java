package com.grillecube.client.renderer.opengl.geometry;

public class Cube
{

	/** return a new cube (x, y, z) */
	public static float[] make(float size)
	{
		size /= 2;

		float[] vertices = {
			    
			    //left
				-size, size, -size,
			    -size, -size, -size,
			    -size, -size, size,
			    -size, size, size,
			    
			    //right
				size, size, -size,
			    size, -size, -size,
			    size, -size, size,
			    size, size, size,
			    
			    //top
				-size, size, -size,
			    size, size, -size,
			    size, size, size,
			    -size, size, size,
			    
			    //bot
				-size, -size, -size,
			    size, -size, -size,
			    size, -size, size,
			    -size, -size, size,
			    
				//front
				-size, size, -size,
			    -size, -size, -size,
			    size, -size, -size,
			    size, size, -size,
			    
			    //back
				-size, size, size,
			    -size, -size, size,
			    size, -size, size,
			    size, size, size
		};
		
		return (vertices);
	}
	
	/** return a new cube with faces ID (x, y, z, faceID) */
	public static float[] makeWithFace(float size)
	{
		size /= 2;
		
		float[] vertices = {
			    
			    //left
				-size, size, -size, 0.84f,
			    -size, -size, -size, 0.84f,
			    -size, -size, size, 0.84f,
			    -size, size, size, 0.84f,
			    
			    //right
				size, size, -size, 0.84f,
			    size, -size, -size, 0.84f,
			    size, -size, size, 0.84f,
			    size, size, size, 0.84f,
			    
			    //top
				-size, size, -size, 1.0f,
			    size, size, -size, 1.0f,
			    size, size, size, 1.0f,
			    -size, size, size, 1.0f,
			    
			    //bot
				-size, -size, -size, 1.0f,
			    size, -size, -size, 1.0f,
			    size, -size, size, 1.0f,
			    -size, -size, size, 1.0f,
			    
				//front
				-size, size, -size, 0.7f,
			    -size, -size, -size, 0.7f,
			    size, -size, -size, 0.7f,
			    size, size, -size, 0.7f,
			    
			    //back
				-size, size, size, 0.7f,
			    -size, -size, size, 0.7f,
			    size, -size, size, 0.7f,
			    size, size, size, 0.7f
		};
		
		return (vertices);
	}

}
