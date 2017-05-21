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

package com.grillecube.client.renderer.model.json;

import com.grillecube.common.faces.Face;

public class ModelMeshVertex {

	public Face face;

	public int blockx, blocky, blockz;
	public float posx, posy, posz;
	public float normalx, normaly, normalz;

	public float ao;

	public ModelMeshVertex(Face face, int blockx, int blocky, int blockz, float px, float py, float pz, float nx,
			float ny, float nz, float ao) {
		this.blockx = blockx;
		this.blocky = blocky;
		this.blockz = blockz;

		this.face = face;

		this.posx = px;
		this.posy = py;
		this.posz = pz;

		this.normalx = nx;
		this.normaly = ny;
		this.normalz = nz;

		this.ao = ao;
	}
}
