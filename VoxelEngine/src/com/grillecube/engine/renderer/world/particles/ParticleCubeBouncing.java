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

package com.grillecube.engine.renderer.world.particles;

import com.grillecube.engine.renderer.camera.CameraProjectiveWorld;
import com.grillecube.engine.world.World;

public class ParticleCubeBouncing extends ParticleCube {
	private boolean _is_bouncing;

	public ParticleCubeBouncing() {
		super();
		this._is_bouncing = false;
	}

	/** update the particle (move it) */
	public void update(World world, CameraProjectiveWorld camera) {
		if (!this._is_bouncing) {
			this._pos_vel.y -= 0.001f;
		} else if (world.getBlock(this.getPosition()).isOpaque()) {
			this._is_bouncing = true;
			this._pos_vel.y = 0.1f;
			this._health = 120;
		}
		super.update(world, camera);
	}
}
