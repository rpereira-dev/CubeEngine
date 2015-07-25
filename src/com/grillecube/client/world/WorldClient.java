package com.grillecube.client.world;

import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.common.world.TerrainLocation;
import com.grillecube.common.world.World;

public class WorldClient extends World
{
	private HashMap<TerrainLocation, TerrainClient>	_terrains;
	
	public WorldClient()
	{
		super();
		this._terrains = new HashMap<TerrainLocation, TerrainClient>();
	}
	

	/** called on initiliaztion */
	public void start()
	{
		for (int x = 0 ; x < 4 ; x++)
		{
			for (int z = 0 ; z < 4 ; z++)
			{
				this.addTerrain(new TerrainClient(new TerrainLocation(x, 0, z)));				
			}
		}
	}
	
	/** called in the WorldThread thread, (not in the rendering one) */
	@Override
	public void update()
	{
		for (TerrainClient terrain : this._terrains.values())
		{
			terrain.update();
		}
	}
	
	/** add the terrain to the world */
	public void	addTerrain(TerrainClient terrain)
	{
		this._terrains.put(terrain.getLocation(), terrain);
	}
	
	/** return the terrain with the given location */
	public TerrainClient	getTerrain(TerrainLocation loc)
	{
		return (this._terrains.get(loc));
	}
	
	/** return the terrain with the given location */
	public TerrainClient	getTerrainAt(Vector3f coords)
	{
		return (this._terrains.get(this.getTerrainLocation(coords)));
	}
	
	/** return the terrain hashmap */
	public Collection<TerrainClient>	getTerrains()
	{
		return (this._terrains.values());
	}
}
