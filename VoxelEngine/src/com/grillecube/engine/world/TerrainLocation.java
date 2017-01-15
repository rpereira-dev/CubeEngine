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

package com.grillecube.engine.world;

import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.maths.Vector3i;

public class TerrainLocation {
	private Vector3i _world_index;
	private Vector3f _world_position;
	private Vector3f _world_center;

	public TerrainLocation() {
		this(0, 0, 0);
	}

	public TerrainLocation(int x, int y, int z) {
		this._world_index = new Vector3i(x, y, z);
		this._world_position = new Vector3f(x * Terrain.DIM_SIZE, y * Terrain.DIM_SIZE,
				z * Terrain.DIM_SIZE);
		this._world_center = new Vector3f(this._world_position.x + Terrain.DEMI_DIM_SIZE,
				this._world_position.y + Terrain.DEMI_DIM_SIZE, this._world_position.z + Terrain.DEMI_DIM_SIZE);
	}

	public TerrainLocation(Vector3i index) {
		this(index.x, index.y, index.z);
	}

	/** World index */
	public Vector3i getWorldIndex() {
		return (this._world_index);
	}

	/** World position */
	public Vector3f getWorldPosition() {
		return (this._world_position);
	}

	/** World position */
	public Vector3f getCenter() {
		return (this._world_center);
	}

	@Override
	public boolean equals(Object obj) {
		return (this._world_index.equals(((TerrainLocation) obj).getWorldIndex()));
	}

	public TerrainLocation set(int x, int y, int z) {
		this._world_index.set(x, y, z);
		this._world_position.set(x * Terrain.DIM_SIZE, y * Terrain.DIM_SIZE, z * Terrain.DIM_SIZE);
		this._world_center.set(this._world_position.x + Terrain.DEMI_DIM_SIZE,
				this._world_position.y + Terrain.DEMI_DIM_SIZE, this._world_position.z + Terrain.DEMI_DIM_SIZE);
		return (this);
	}

	@Override
	public String toString() {
		return ("TerrainLocation: " + this._world_index.toString() + " : " + this._world_position.toString());
	}
}