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

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.world.particles.ParticleCube;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.faces.Face;
import com.grillecube.common.world.terrain.Terrain;

public class BlockGrass extends BlockCubeOpaque {
	public BlockGrass(int blockID) {
		super(blockID, Face.LEFT, Blocks.T_GRASS_SIDE, Face.RIGHT, Blocks.T_GRASS_SIDE, Face.FRONT, Blocks.T_GRASS_SIDE,
				Face.BACK, Blocks.T_GRASS_SIDE, Face.TOP, Blocks.T_GRASS_TOP, Face.BOT, Blocks.T_DIRT);
	}

	@Override
	public String getName() {
		return ("grass");
	}

	@Override
	public void update(Terrain terrain, int x, int y, int z) {
//		if (terrain.getWorld().getRNG().nextInt(50) == 0) {
//			ParticleCube particle = new ParticleCube();
//			particle.setColor(1, 0, 0, 1);
//			particle.setHealth(150);
//			particle.setPositionVel(0, 0.05f, 0);
//			particle.setPosition(terrain.getWorldPosition());
//			particle.getPosition().add(x + 0.5f, y + 0.5f, z + 0.5f);
//			particle.setScale(0.2f);
//			VoxelEngineClient.instance().getRenderer().getWorldRenderer().getParticleRenderer().spawnParticle(particle);
//		}
	}
}
