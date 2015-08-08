package com.grillecube.client.renderer.opengl.geometry;

/**
 * this implementation is based on this one:
 * 
 * https://github.com/fogleman/Craft/blob/master/src/cube.c
 *
 */

public class Sphere
{
	/** indices are "details" value
	 * 
	 *	triangles : number of triangles
	 *	vertices : number of verices
	 *	floats : number of floats 
	 **/
	@SuppressWarnings("unused")
	private static final int[] triangles = {8, 32, 128, 512, 2048, 8192, 32768, 131072};
	private static final int[] vertices = {24, 96, 384, 1536, 6144, 24576, 98304, 393216};
	private static final int[] floats = {192, 768, 3072, 12288, 49152, 196608, 786432, 3145728};

	/** index in current float buffer */
	private static int _index = 0;
	/** rayon for the current sphere */
	private static float _rayon = 0;
	
	private static void normalize(float[] f)
    {
    	double	norm;
    	
    	norm = Math.sqrt(f[0] * f[0] + f[1] * f[1] + f[2] * f[2]);
    	f[0] /= norm;
    	f[1] /= norm;
    	f[2] /= norm;
	}
    
	private static void make_sphere(
			float[] data, int detail,
		    float[] a, float[] b, float[] c)
	{
	    if (detail == 0) {
	        data[_index++] = a[0] * _rayon; data[_index++] = a[1] * _rayon; data[_index++] = a[2] * _rayon;
	        data[_index++] = b[0] * _rayon; data[_index++] = b[1] * _rayon; data[_index++] = b[2] * _rayon;
	        data[_index++] = c[0] * _rayon; data[_index++] = c[1] * _rayon; data[_index++] = c[2] * _rayon;
	    }
	    else
	    {
	        float ab[], ac[], bc[];
	        
	        ab = new float[3];
	        ac = new float[3];
	        bc = new float[3];
	        for (int i = 0; i < 3; i++) {
	            ab[i] = (a[i] + b[i]) / 2;
	            ac[i] = (a[i] + c[i]) / 2;
	            bc[i] = (b[i] + c[i]) / 2;
	        }
	        normalize(ab);
	        normalize(ac);
	        normalize(bc);
	        
	        make_sphere(data, detail - 1, a, ab, ac);
	        make_sphere(data, detail - 1, b, bc, ab);
	        make_sphere(data, detail - 1, c, ac, bc);
	        make_sphere(data, detail - 1, ab, bc, ac);
	    }
	}

	public static float[] make(int detail, float rayon)
	{
		if (detail < 0 || detail >= 8)
		{
			return (null);
		}
		
		int 	indices[][] = {
				{4, 3, 0}, {1, 4, 0},
				{3, 4, 5}, {4, 1, 5},
		        {0, 3, 2}, {0, 2, 1},
		        {5, 2, 3}, {5, 1, 2}        
		};
		
	    float	positions[][] = {
		        { 0, 0,-1}, { 1, 0, 0},
		        { 0,-1, 0}, {-1, 0, 0},
		        { 0, 1, 0}, { 0, 0, 1}        
	    };
				
		float[] data = new float[floats[detail]];
		_index = 0;
		_rayon = rayon;

		for (int i = 0; i < 8; i++)
		{
			make_sphere(
					data, detail,
		            positions[indices[i][0]],
		            positions[indices[i][1]],
		            positions[indices[i][2]]);
		}
		
		
		
		return (data);
	}
	
	/** return the number of vertices for this detail value */
	public static int getVertexCount(int detail)
	{
		if (detail < 0 || detail >= 8)
		{
			return (0);
		}
		return (vertices[detail]);
	}

}