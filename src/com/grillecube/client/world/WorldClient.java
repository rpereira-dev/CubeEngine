package com.grillecube.client.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.model.Models;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.world.entity.EntityModeled;
import com.grillecube.common.world.TerrainLocation;
import com.grillecube.common.world.World;

public class WorldClient extends World
{
	/** every loaded terrain are in */
	private HashMap<TerrainLocation, TerrainClient>	_terrains;
	
	/** every world entities */
	private ArrayList<EntityModeled>	_entities;
	
	/** world weather */
	private Weather	_weather;
	
	public WorldClient()
	{
		super();
		this._terrains = new HashMap<TerrainLocation, TerrainClient>();
		this._entities = new ArrayList<EntityModeled>();
		this._weather = new Weather();
	}

	/** called on initiliaztion */
	public void start()
	{
		for (int x = -4 ; x < 4 ; x++)
		{
			for (int z = -4 ; z < 4 ; z++)
			{
				for (int y = 0 ; y < 1 ; y++)
				{
					this.addTerrain(new TerrainClient(this, new TerrainLocation(x, y, z)));
				}
			}
		}

		this._entities.add(new EntityModeled(Models.getModel(Models.PIG))
		{

			@Override
			protected void updateEntity() {}
			
		});
	}
	
	/** add the terrain to the world */
	public void	addTerrain(TerrainClient terrain)
	{
		TerrainClient[]	neighbors;
		
		neighbors = terrain.getNeighboors();
		for (TerrainClient t : neighbors)
		{
			if (t != null)
			{
				t.getMesh().unsetState(TerrainMesh.STATE_VERTICES_UP_TO_DATE);
			}
		}
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

	public void	spawnEntity(EntityModeled entity, Vector3f pos)
	{
		entity.setPosition(pos);
		entity.setWorld(this);
		this._entities.add(entity);
	}
	
	public ArrayList<EntityModeled>	getEntities()
	{
		return (this._entities);
	}

	/** return world weather */
	public Weather	getWeather()
	{
		return (this._weather);
	}
}
