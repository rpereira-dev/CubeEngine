package com.grillecube.common.world.events;

import com.grillecube.common.world.World;
import com.grillecube.common.world.terrain.Terrain;

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
