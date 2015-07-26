package com.grillecube.common.world;

import org.lwjgl.util.vector.Vector3f;

import fr.toss.lib.Vector3i;

public abstract class World
{	
	public World()
	{

	}

	/** get the terrain location (x, y, z) for the given world location */
	public TerrainLocation	getTerrainLocation(Vector3f pos)
	{
		TerrainLocation	location;

		location = new TerrainLocation();
		if (pos.x < 0)
		{
			pos.x -= Terrain.SIZE_X;
		}
		if (pos.y < 0)
		{
			pos.y -= Terrain.SIZE_Y;
		}
		if (pos.z < 0)
		{
			pos.z -= Terrain.SIZE_Z;
		}
		location.set((int)pos.x / Terrain.SIZE_X,
					(int)pos.y / Terrain.SIZE_Y,
					(int)pos.z / Terrain.SIZE_Z);
		return (location);
	}
	
	/** return positions relative to the terrain */
	public Vector3i	getTerrainRelativePos(Vector3f vec)
	{
		int	x;
		int	y;
		int	z;
		
		x = (int)vec.x % Terrain.SIZE_X;
		y = (int)vec.y % Terrain.SIZE_Y; 
		z = (int)vec.z % Terrain.SIZE_Z;
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
