package com.grillecube.client.renderer.opengl.geometry;

public class Cube
{

	/** return a new cube (x, y, z) */
	public static float[] make(float size)
	{
		float[] vertices = {
			    
			    //left
				0, size, 0,
			    0, 0, 0,
			    0, 0, size,
			    0, size, size,
			    
			    //right
				size, size, 0,
			    size, 0, 0,
			    size, 0, size,
			    size, size, size,
			    
			    //top
				0, size, 0,
			    size, size, 0,
			    size, size, size,
			    0, size, size,
			    
			    //bot
				0, 0, 0,
			    size, 0, 0,
			    size, 0, size,
			    0, 0, size,
			    
				//front
				0, size, 0,
			    0, 0, 0,
			    size, 0, 0,
			    size, size, 0,
			    
			    //back
				0, size, size,
			    0, 0, size,
			    size, 0, size,
			    size, size, 1
		};
		
		return (vertices);
	}
	
	/** return a new cube with faces ID (x, y, z, faceID) */
	public static float[] makeWithFace(float size)
	{
		float[] vertices = {
			    
			    //left
				0, size, 0, 0.84f,
			    0, 0, 0, 0.84f,
			    0, 0, size, 0.84f,
			    0, size, size, 0.84f,
			    
			    //right
				size, size, 0, 0.84f,
			    size, 0, 0, 0.84f,
			    size, 0, size, 0.84f,
			    size, size, size, 0.84f,
			    
			    //top
				0, size, 0, 1.0f,
			    size, size, 0, 1.0f,
			    size, size, size, 1.0f,
			    0, size, size, 1.0f,
			    
			    //bot
				0, 0, 0, 1.0f,
			    size, 0, 0, 1.0f,
			    size, 0, size, 1.0f,
			    0, 0, size, 1.0f,
			    
				//front
				0, size, 0, 0.7f,
			    0, 0, 0, 0.7f,
			    size, 0, 0, 0.7f,
			    size, size, 0, 0.7f,
			    
			    //back
				0, size, size, 0.7f,
			    0, 0, size, 0.7f,
			    size, 0, size, 0.7f,
			    size, size, size, 0.7f
		};
		
		return (vertices);
	}

}
