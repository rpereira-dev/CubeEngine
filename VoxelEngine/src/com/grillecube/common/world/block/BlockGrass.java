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

package com.grillecube.common.world.block;

import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class BlockGrass extends BlockCubeOpaque {
	public BlockGrass(int blockID) {
		super(blockID);
	}

	@Override
	public String getName() {
		return ("grass");
	}

	@Override
	public void update(WorldObjectTerrain terrain, int x, int y, int z) {
		// if (terrain.getWorld().getRNG().nextInt(50) == 0) {
		// ParticleCube particle = new ParticleCube();
		// particle.setColor(1, 0, 0, 1);
		// particle.setHealth(150);
		// particle.setPositionVel(0, 0.05f, 0);
		// particle.setPosition(terrain.getWorldPosition());
		// particle.getPosition().add(x + 0.5f, y + 0.5f, z + 0.5f);
		// particle.setScale(0.2f);
		// VoxelEngineClient.instance().getRenderer().getWorldRenderer().getParticleRenderer().spawnParticle(particle);
		// }
	}
}
