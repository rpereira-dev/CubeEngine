package com.grillecube.client.renderer.terrain;

public class MeshVertex
{
	public float	posx;
	public float	posy;
	public float	posz;
	
	public float	normalx;
	public float	normaly;
	public float	normalz;
	
	public float	uvx;
	public float	uvy;

	public MeshVertex(float px, float py, float pz, float nx, float ny, float nz, float ux, float uy)
	{
		this.posx = px;
		this.posy = py;
		this.posz = pz;
		
		this.normalx = nx;
		this.normaly = ny;
		this.normalz = nz;
		
		this.uvx = ux;
		this.uvy = uy;
	}

}
