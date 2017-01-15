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

package com.grillecube.engine.renderer.world.terrain;

import com.grillecube.engine.maths.Vector3f;

public class MeshVertex
{
	public float posx; //[-oo, +oo]
	public float posy; //[-oo, +oo]
	public float posz; //[-oo, +oo]
	
	public float normalx;
	public float normaly;
	public float normalz;

	public float atlasX;
	public float atlasY;
	public float uvx; //[0, 1]
	public float uvy; //[0, 1]
	
	public float color;
	public float brightness;
	
	private float ao;

	public MeshVertex() {		
	}
	
	public MeshVertex(float posx, float posy, float posz, Vector3f normal, float atlasX, float atlasY, float uvx, float uvy, int color, float brightness, float ao)
	{
		this.posx = posx;
		this.posy = posy;
		this.posz = posz;
		
		this.normalx = normal.x;
		this.normaly = normal.y;
		this.normalz = normal.z;
		
		this.atlasX = atlasX;
		this.atlasY = atlasY;
		this.uvx = uvx;
		this.uvy = uvy;
		
		this.color = color;
		this.brightness = brightness;
		this.ao = ao;
	}
	
	public float getAO() {
		return (this.ao);
	}
}
