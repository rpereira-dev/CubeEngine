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

package com.grillecube.client.renderer.world;

import java.nio.ByteBuffer;

import com.grillecube.client.renderer.MeshVertex;

public class TerrainMeshVertex extends MeshVertex {

	public float posx; // [-oo, +oo]
	public float posy; // [-oo, +oo]
	public float posz; // [-oo, +oo]

	public float normalx;
	public float normaly;
	public float normalz;

	public float atlasX;
	public float atlasY;
	public float u; // [0, 1]
	public float v; // [0, 1]

	public int color;
	public float brightness;

	public float ao;

	public TerrainMeshVertex() {
		super();
	}

	public TerrainMeshVertex(float posx, float posy, float posz, float normalx, float normaly, float normalz,
			float atlasX, float atlasY, float u, float v, int color, float brightness, float ao) {
		super();

		this.posx = posx;
		this.posy = posy;
		this.posz = posz;

		this.normalx = normalx;
		this.normaly = normaly;
		this.normalz = normalz;

		this.atlasX = atlasX;
		this.atlasY = atlasY;
		this.u = u;
		this.v = v;

		this.color = color;
		this.brightness = brightness;
		this.ao = ao;
	}

	@Override
	public final void store(ByteBuffer buffer) {
		buffer.putFloat(this.posx);
		buffer.putFloat(this.posy);
		buffer.putFloat(this.posz);
		buffer.putFloat(this.normalx);
		buffer.putFloat(this.normaly);
		buffer.putFloat(this.normalz);
		buffer.putFloat(this.atlasX);
		buffer.putFloat(this.atlasY);
		buffer.putFloat(this.u);
		buffer.putFloat(this.v);
		buffer.putInt(this.color);
		buffer.putFloat(this.brightness);
	}

	@Override
	public MeshVertex clone() {
		return (new TerrainMeshVertex(this.posx, this.posy, this.posz, this.normalx, this.normaly, this.normalz,
				this.atlasX, this.atlasY, this.u, this.v, this.color, this.brightness, this.ao));
	}
}
