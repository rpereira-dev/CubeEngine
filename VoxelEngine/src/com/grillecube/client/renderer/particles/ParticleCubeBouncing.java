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

package com.grillecube.client.renderer.particles;

import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.common.world.World;

public class ParticleCubeBouncing extends ParticleCube {
	private boolean _is_bouncing;

	public ParticleCubeBouncing() {
		super();
		this._is_bouncing = false;
	}

	/** update the particle (move it) */
	public void update(World world, CameraProjectiveWorld camera) {

//		if (world.getBlock(this.getPosition()).isOpaque()) {
//			this._is_bouncing = true;
//			super.posVel.y = 0.4f;
//			super.health = 60;
//		}

		super.posVel.y -= 0.001f;
		super.update(world, camera);
	}
}
