package com.grillecube.client.renderer.font;

public class FontVertex
{
	public float posx;
	public float posy;
	public float posz;
	
	public float uvx;
	public float uvy;
	
	public FontVertex(float px, float py, float pz, float uvx, float uvy)
	{
		this.posx = px;
		this.posy = py;
		this.posz = pz;
		this.uvx = uvx;
		this.uvy = uvy;
	}
}
