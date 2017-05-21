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

package com.grillecube.common.world.terrain;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector3i;

public class TerrainLocation {
	private Vector3i worldIndex;
	private Vector3f worldPosition;
	private Vector3f worldCenter;

	public TerrainLocation() {
		this(0, 0, 0);
	}

	public TerrainLocation(int x, int y, int z) {
		this.worldIndex = new Vector3i(x, y, z);
		this.worldPosition = new Vector3f(x * Terrain.DIM_SIZE, y * Terrain.DIM_SIZE,
				z * Terrain.DIM_SIZE);
		this.worldCenter = new Vector3f(this.worldPosition.x + Terrain.DEMI_DIM_SIZE,
				this.worldPosition.y + Terrain.DEMI_DIM_SIZE, this.worldPosition.z + Terrain.DEMI_DIM_SIZE);
	}

	public TerrainLocation(Vector3i index) {
		this(index.x, index.y, index.z);
	}

	/** World index */
	public Vector3i getWorldIndex() {
		return (this.worldIndex);
	}

	/** World position */
	public Vector3f getWorldPosition() {
		return (this.worldPosition);
	}

	/** World position */
	public Vector3f getCenter() {
		return (this.worldCenter);
	}

	@Override
	public boolean equals(Object obj) {
		return (this.worldIndex.equals(((TerrainLocation) obj).getWorldIndex()));
	}

	public TerrainLocation set(int x, int y, int z) {
		this.worldIndex.set(x, y, z);
		this.worldPosition.set(x * Terrain.DIM_SIZE, y * Terrain.DIM_SIZE, z * Terrain.DIM_SIZE);
		this.worldCenter.set(this.worldPosition.x + Terrain.DEMI_DIM_SIZE,
				this.worldPosition.y + Terrain.DEMI_DIM_SIZE, this.worldPosition.z + Terrain.DEMI_DIM_SIZE);
		return (this);
	}

	@Override
	public String toString() {
		return ("TerrainLocation: " + this.worldIndex.toString() + " : " + this.worldPosition.toString());
	}
}