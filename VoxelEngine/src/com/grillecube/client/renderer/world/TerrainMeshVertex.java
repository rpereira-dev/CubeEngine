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
import com.grillecube.common.maths.Vector3f;

public class TerrainMeshVertex extends MeshVertex {

	public float posx; // [-oo, +oo]
	public float posy; // [-oo, +oo]
	public float posz; // [-oo, +oo]

	public float normalx;
	public float normaly;
	public float normalz;

	public float atlasX;
	public float atlasY;
	public float uvx; // [0, 1]
	public float uvy; // [0, 1]

	public int color;
	public float brightness;

	public float ao;

	public TerrainMeshVertex() {
		super();
	}

	public TerrainMeshVertex(float posx, float posy, float posz, Vector3f normal, float atlasX, float atlasY, float uvx,
			float uvy, int color, float brightness, float ao) {
		super();

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
		buffer.putFloat(this.uvx);
		buffer.putFloat(this.uvy);
		buffer.putInt(this.color);
		buffer.putFloat(this.brightness);

	}
}
