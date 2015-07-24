package com.grillecube.common.world;

import java.util.HashMap;

public abstract class World
{
	private HashMap<Terrain, TerrainLocation>	_terrains;
	
	public World()
	{
		this._terrains = new HashMap<Terrain, TerrainLocation>();
	}
	
	/** update the world */
	public void	update()
	{
		for (Terrain terrain : this._terrains.keySet())
		{
			terrain.update();
		}
		this.updateWorld();
	}
	
	public abstract void	updateWorld();
}
