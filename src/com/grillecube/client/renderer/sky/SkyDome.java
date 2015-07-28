package com.grillecube.client.renderer.sky;

/**
 * this implementation is based on this one:
 * 
 * https://github.com/fogleman/Craft/blob/master/src/cube.c
 *
 */

// detail, triangles, floats
// 0, 8, 192
// 1, 32, 768
// 2, 128, 3072
// 3, 512, 12288
// 4, 2048, 49152
// 5, 8192, 196608
// 6, 32768, 786432
// 7, 131072, 3145728

public class SkyDome
{
	//TODO : edit these 3 lines if you want more details on skybox
	public static final float RAYON			= 256.0f;
	public static final int DETAILS			= 4;
	public static final int TRIANGLES		= 2048;
	public static final int FLOATS_COUNT	= 49152;
	public static final int VERTICES_COUNT	= TRIANGLES * 3;

	private static int _index;
	
	private static void normalize(float[] f)
    {
    	double	norm;
    	
    	norm = Math.sqrt(f[0] * f[0] + f[1] * f[1] + f[2] * f[2]);
    	f[0] /= norm;
    	f[1] /= norm;
    	f[2] /= norm;
	}
    
	private static void _make_sphere(
			float[] data, int detail,
		    float[] a, float[] b, float[] c)
	{
	    if (detail == 0) {
	        data[_index++] = a[0] * SkyDome.RAYON; data[_index++] = a[1] * SkyDome.RAYON; data[_index++] = a[2] * SkyDome.RAYON;
	        data[_index++] = b[0] * SkyDome.RAYON; data[_index++] = b[1] * SkyDome.RAYON; data[_index++] = b[2] * SkyDome.RAYON;
	        data[_index++] = c[0] * SkyDome.RAYON; data[_index++] = c[1] * SkyDome.RAYON; data[_index++] = c[2] * SkyDome.RAYON;
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
	        
	        _make_sphere(data, detail - 1, a, ab, ac);
	        _make_sphere(data, detail - 1, b, bc, ab);
	        _make_sphere(data, detail - 1, c, ac, bc);
	        _make_sphere(data, detail - 1, ab, bc, ac);
	    }
	}

	public static float[] make_sphere()
	{		
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
		
		int	detail;
		
		detail = SkyDome.DETAILS;
		float[] data = new float[SkyDome.FLOATS_COUNT];
		_index = 0;


		for (int i = 0; i < 8; i++)
		{
			_make_sphere(
					data, detail,
		            positions[indices[i][0]],
		            positions[indices[i][1]],
		            positions[indices[i][2]]);
		}
		
		
		
		return (data);
	}
}