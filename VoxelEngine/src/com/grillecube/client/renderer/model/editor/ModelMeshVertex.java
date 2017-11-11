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

package com.grillecube.client.renderer.model.editor;

import java.nio.ByteBuffer;

import com.grillecube.client.renderer.MeshVertex;

public class ModelMeshVertex extends MeshVertex {

	public float x;
	public float y;
	public float z;

	public float uvx;
	public float uvy;

	public float nx;
	public float ny;
	public float nz;

	public int b1;
	public int b2;
	public int b3;

	public float w1;
	public float w2;
	public float w3;

	@Override
	public void store(ByteBuffer buffer) {
		buffer.putFloat(this.x);
		buffer.putFloat(this.y);
		buffer.putFloat(this.z);
		buffer.putFloat(this.uvx);
		buffer.putFloat(this.uvy);
		buffer.putFloat(this.nx);
		buffer.putFloat(this.ny);
		buffer.putFloat(this.nz);
		buffer.putInt(this.b1);
		buffer.putInt(this.b2);
		buffer.putInt(this.b3);
		buffer.putFloat(this.w1);
		buffer.putFloat(this.w2);
		buffer.putFloat(this.w3);
	}

	@Override
	public MeshVertex clone() {
		ModelMeshVertex m = new ModelMeshVertex();
		m.x = this.x;
		m.y = this.y;
		m.z = this.z;
		m.uvx = this.uvx;
		m.uvy = this.uvy;
		m.nx = this.nx;
		m.ny = this.ny;
		m.nz = this.nz;
		m.b1 = this.b1;
		m.b2 = this.b2;
		m.b3 = this.b3;
		m.w1 = this.w1;
		m.w2 = this.w2;
		m.w3 = this.w3;
		return (m);
	}
}
