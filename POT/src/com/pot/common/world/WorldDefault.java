/**
**	This file is part of the project https://githubcreateProjectionMatrix.com/toss-dev/VoxelEngine
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

import com.grillecube.common.world.WorldFlat;
import com.grillecube.common.world.generator.WorldGeneratorFlat;

public class WorldDefault extends WorldFlat {

	public WorldDefault() {
		super();
	}

	@Override
	public void onLoaded() {
		// this.setWorldGenerator(new WorldGeneratorHoles());
		this.setWorldGenerator(new WorldGeneratorFlat());
		for (int z = 1; z > 0; z--) {
			for (int x = -2; x < 2; x++) {
				for (int y = -2; y < 2; y++) {
					this.generateTerrain(x, y, 0);
				}
			}
		}

		// for (int x = 0; x < 4; x++) {
		// for (int z = 0; z < 4; z++) {
		// EntityPlant entityTest = new EntityPlant(this);
		// entityTest.setPosition(x * 8.0f, 140.0f, z * 8.0f);
		// this.spawnEntity(entityTest);
		// }
		// }
	}

	@Override
	public String getName() {
		return ("Default");
	}
}
