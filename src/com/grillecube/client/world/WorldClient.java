package com.grillecube.client.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.mod.blocks.ResourceBlocks;
import com.grillecube.client.renderer.model.Models;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.ressources.BlockManager;
import com.grillecube.client.world.blocks.Block;
import com.grillecube.client.world.entity.EntityModeled;
import com.grillecube.common.world.TerrainLocation;
import com.grillecube.common.world.World;

import fr.toss.lib.Vector3i;

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
		for (int x = -8 ; x < 8 ; x++)
		{
			for (int z = -8 ; z < 8 ; z++)
			{
				for (int y = -4 ; y < 4 ; y++)
				{
					this.addTerrain(new TerrainClient(this, new TerrainLocation(x, y, z)));
				}
			}
		}
		
//		this.addTerrain(new TerrainClient(this, new TerrainLocation(0, 0, 0)));


//		this._entities.add(new EntityModeled(Models.getModel(Models.PIG))
//		{
//
//			@Override
//			protected void updateEntity() {}
//		});
		
	}
	
	/** add the terrain to the world */
	public void	addTerrain(TerrainClient terrain)
	{
		TerrainClient[][][]	neighbors;
		
		neighbors = terrain.getNeighboors();
		for (TerrainClient[][] t1 : neighbors)
		{
			for (TerrainClient[] t2 : t1)
			{
				for (TerrainClient t3 : t2)
				{
					if (t3 != null)
					{
						t3.getMesh().unsetState(TerrainMesh.STATE_VERTICES_UP_TO_DATE);
					}	
				}
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

	public Block getBlock(Vector3f pos)
	{
		TerrainLocation	location;
		Vector3i		terrainpos;
		TerrainClient	terrain;
		
		location = super.getTerrainLocation(pos);
		terrainpos = super.getTerrainRelativePos(pos);
		terrain = this.getTerrain(location);
		if (terrain == null)
		{
			return (BlockManager.getBlockByID(ResourceBlocks.AIR));
		}
		return (terrain.getBlock(terrainpos));
	}
	
	public void setBlock(Vector3f pos, short blockID)
	{
		TerrainClient terrain = this.getTerrainAt(pos);
		
		if (terrain == null)
		{
			return ;
		}
		Vector3i vec = this.getTerrainRelativePos(pos);
		terrain.setBlock(vec, blockID);
	}
}
