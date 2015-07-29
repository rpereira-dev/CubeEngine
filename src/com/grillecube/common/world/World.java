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
		return (this.getTerrainLocation(pos.x, pos.y, pos.z));
	}
	
	
	/** return positions relative to the terrain */
	public Vector3i	getTerrainRelativePos(Vector3f pos)
	{
		pos.x = (int)pos.x % Terrain.SIZE_X;
		pos.y = (int)pos.y % Terrain.SIZE_Y; 
		pos.z = (int)pos.z % Terrain.SIZE_Z;
		if (pos.x < 0)
		{
			pos.x += Terrain.SIZE_X;
		}
		if (pos.y < 0)
		{
			pos.y += Terrain.SIZE_Y;
		}
		if (pos.z < 0)
		{
			pos.z += Terrain.SIZE_Z;
		}
		return (new Vector3i((int)pos.x, (int)pos.y, (int)pos.z));
	}
}
