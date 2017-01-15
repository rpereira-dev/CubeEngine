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

package com.grillecube.engine.resources;

import com.grillecube.engine.VoxelEngineClient;

public class ResourceManagerClient extends ResourceManager {

	public ResourceManagerClient(VoxelEngineClient engine) {
		super(engine);
	}

	@Override
	public void update() {
		for (GenericManager<?> manager : super._managers) {
			manager.updateClientSide();
		}
	}
}
