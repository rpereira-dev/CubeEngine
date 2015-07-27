package com.grillecube.common.world;

import org.lwjgl.util.vector.Vector3f;

import fr.toss.lib.Vector3i;

public abstract class World
{	
	public World()
	{

	}

	/** get the terrain location (x, y, z) for the given world location */
	public TerrainLocation	getTerrainLocation(float x, float y, float z)
	{
		TerrainLocation	location;

		location = new TerrainLocation();
		if (x < 0)
		{
			x -= Terrain.SIZE_X;
		}
		if (y < 0)
		{
			y -= Terrain.SIZE_Y;
		}
		if (z < 0)
		{
			z -= Terrain.SIZE_Z;
		}
		location.set((int)x / Terrain.SIZE_X,
					(int)y / Terrain.SIZE_Y,
					(int)z / Terrain.SIZE_Z);
		return (location);
	}
	
	/** get the terrain location (x, y, z) for the given world location */
	public TerrainLocation	getTerrainLocation(Vector3f pos)
	{
		return (getTerrainLocation(pos.x, pos.y, pos.z));
	}
	
	
	/** return positions relative to the terrain */
	public Vector3i	getTerrainRelativePos(int x, int y, int z)
	{
		x = (int)x % Terrain.SIZE_X;
		y = (int)y % Terrain.SIZE_Y; 
		z = (int)z % Terrain.SIZE_Z;
		if (x < 0)
		{
			x += Terrain.SIZE_X;
		}
		if (y < 0)
		{
			y += Terrain.SIZE_Y;
		}
		if (z < 0)
		{
			z += Terrain.SIZE_Z;
		}
		return (new Vector3i(x, y, z));
	}
}
