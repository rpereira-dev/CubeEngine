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

package com.grillecube.common.world;

import java.util.ArrayList;

import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.world.sky.Sky;
import com.grillecube.common.world.terrain.WorldFlatTerrainStorage;
import com.grillecube.common.world.terrain.WorldTerrainStorage;

public abstract class WorldFlat extends World {

	/** world weather */
	private Sky sky;

	public WorldFlat() {
		super();
		this.sky = new Sky();
	}

	protected final WorldTerrainStorage instanciateTerrainStorage() {
		return (new WorldFlatTerrainStorage(this));
	}

	/** tasks to be run to update the world */
	@Override
	protected void onTasksGet(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {
		this.sky.getTasks(engine, tasks);
		this.sky.setCycleRatio(0.4f);
	}

	/** return world weather */
	public Sky getSky() {
		return (this.sky);
	}

	@Override
	public String toString() {
		return ("WorldFlat: " + this.getName());
	}

	/** return world name */
	public abstract String getName();
}
