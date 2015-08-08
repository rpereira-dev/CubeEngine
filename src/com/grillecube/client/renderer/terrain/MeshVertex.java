package com.grillecube.client.renderer.terrain;

import org.lwjgl.util.vector.Vector3f;

public class MeshVertex
{
	public float posx;
	public float posy;
	public float posz;
	
	public float normalx;
	public float normaly;
	public float normalz;
	
	public float uvx;
	public float uvy;
	
	public float ao;

	public MeshVertex(float px, float py, float pz, float nx, float ny, float nz, float ux, float uy, float ao)
	{
		this.posx = px;
		this.posy = py;
		this.posz = pz;
		
		this.normalx = nx;
		this.normaly = ny;
		this.normalz = nz;
		
		this.uvx = ux;
		this.uvy = uy;
		
		this.ao = ao;
	}

	public MeshVertex(float px, float py, float pz, Vector3f normal, float ux, float uy, float ao)
	{
		this.posx = px;
		this.posy = py;
		this.posz = pz;
		
		this.normalx = normal.x;
		this.normaly = normal.y;
		this.normalz = normal.z;
		
		this.uvx = ux;
		this.uvy = uy;
		
		this.ao = ao;
	}
}
