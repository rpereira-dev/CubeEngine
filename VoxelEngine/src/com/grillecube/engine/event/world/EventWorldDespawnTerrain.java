package com.grillecube.engine.event.world;

import com.grillecube.engine.world.Terrain;
import com.grillecube.engine.world.World;

public class EventWorldDespawnTerrain extends EventWorld
{
	private Terrain _terrain;

	public EventWorldDespawnTerrain(World world, Terrain terrain)
	{
		super(world);
		this._terrain = terrain;
	}
	
	public Terrain getTerrain()
	{
		return (this._terrain);
	}
}
