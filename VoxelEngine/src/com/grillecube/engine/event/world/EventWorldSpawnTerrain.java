package com.grillecube.engine.event.world;

import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.World;

public class EventWorldSpawnTerrain extends EventWorld
{
	private Terrain _terrain;

	public EventWorldSpawnTerrain(World world, Terrain terrain)
	{
		super(world);
		this._terrain = terrain;
	}
	
	public Terrain getTerrain()
	{
		return (this._terrain);
	}
}
