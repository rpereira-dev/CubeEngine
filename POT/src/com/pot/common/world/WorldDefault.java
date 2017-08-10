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

package com.pot.common.world;

import com.grillecube.common.world.World;
import com.grillecube.common.world.generator.WorldGeneratorHoles;
import com.pot.common.world.entity.EntityTest;

public class WorldDefault extends World {

	public WorldDefault() {
		super();
	}

	@Override
	public String getName() {
		return ("Default");
	}

	@Override
	public void onSet() {
		this.setWorldGenerator(new WorldGeneratorHoles());
		for (int y = 6; y > 0; y--) {
			for (int x = -4; x < 4; x++) {
				for (int z = -4; z < 4; z++) {
					this.generateTerrain(x, y, z);
				}
			}
		}

		for (int x = 0; x < 4; x++) {
			for (int z = 0; z < 4; z++) {

				EntityTest entityTest = new EntityTest(this);
				entityTest.setPosition(x * 8.0f, 160.0f, z * 8.0f);
				this.spawnEntity(entityTest);
			}
		}
		// this.generateLights(); //TODO
	}

	@Override
	public void onUnset() {
	}
}
