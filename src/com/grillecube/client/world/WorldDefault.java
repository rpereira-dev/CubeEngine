package com.grillecube.client.world;

public class WorldDefault extends World
{
	@Override
	public String getName()
	{
		return ("Default");
	}

	@Override
	public void start()
	{
		for (int x = -8 ; x < 8 ; x++)
		{
			for (int z = -8 ; z < 8 ; z++)
			{
				for (int y = -2 ; y < 2 ; y++)
				{
					this.spawnTerrain(new Terrain(new TerrainLocation(x, y, z)));
				}
			}
		}	
	}
}
