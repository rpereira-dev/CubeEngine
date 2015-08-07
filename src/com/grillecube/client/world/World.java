package com.grillecube.client.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector3i;

import com.grillecube.client.mod.blocks.ResourceBlocks;
import com.grillecube.client.ressources.BlockManager;
import com.grillecube.client.world.blocks.Block;
import com.grillecube.client.world.entity.EntityModeled;

public class World
{
	/** every loaded terrain are in */
	private HashMap<Vector3i, Terrain> _terrains;
	
	/** every world entities */
	private ArrayList<EntityModeled>	_entities;
	
	/** world weather */
	private Weather	_weather;
	
	public World()
	{
		super();
		this._terrains = new HashMap<Vector3i, Terrain>();
		this._entities = new ArrayList<EntityModeled>();
		this._weather = new Weather();
	}

	/** called on initiliaztion */
	public void start()
	{
		for (int x = -2 ; x < 2 ; x++)
		{
			for (int z = -2 ; z < 2 ; z++)
			{
				for (int y = 0 ; y < 1 ; y++)
				{
					this.spawnTerrain(new Terrain(new TerrainLocation(x, y, z)));
				}
			}
		}
		
//		this.addTerrain(new Terrain(this, new TerrainLocation(0, 0, 0)));


//		this._entities.add(new EntityModeled(Models.getModel(Models.PIG))
//		{
//
//			@Override
//			protected void updateEntity() {}
//		});
		
	}
	
	/** get the terrain location (x, y, z) for the given world location */
	public Vector3i getTerrainIndex(float x, float y, float z)
	{
		Vector3i index;

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
		index = new Vector3i((int)x / Terrain.SIZE_X,
							(int)y / Terrain.SIZE_Y,
							(int)z / Terrain.SIZE_Z);
		return (index);
	}
	
	/** get the terrain index (x, y, z) for the given world location */
	public Vector3i getTerrainIndex(Vector3f pos)
	{
		return (this.getTerrainIndex(pos.x, pos.y, pos.z));
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
	
	/** add the terrain to the world */
	public void	spawnTerrain(Terrain terrain)
	{
		terrain.onSpawned(this);
		this._terrains.put(terrain.getLocation().toWorldIndex(), terrain);
	}
	
	/** return the terrain with the given location, or null if the terrain doesnt exists / is empty */
	public Terrain	getTerrain(Vector3i pos)
	{
		return (this._terrains.get(pos));
	}
	
	public Terrain getTerrain(int x, int y, int z)
	{
		return (this.getTerrain(new Vector3i(x, y, z)));
	}
	
	/** return the terrain with the given location */
	public Terrain getTerrainAt(Vector3f coords)
	{
		return (this._terrains.get(this.getTerrainIndex(coords)));
	}
	
	/** return the terrain hashmap */
	public Collection<Terrain>	getTerrains()
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
		Terrain terrain = this.getTerrainAt(pos);
		if (terrain == null)
		{
			return (BlockManager.getBlockByID(ResourceBlocks.AIR));
		}
		Vector3i terrainpos = this.getTerrainRelativePos(pos);
		return (terrain.getBlock(terrainpos));
	}
	
	public void setBlock(Vector3f pos, short blockID)
	{
		Terrain terrain = this.getTerrainAt(pos);
		
		if (terrain == null)
		{
			return ;
		}
		Vector3i vec = this.getTerrainRelativePos(pos);
		terrain.setBlock(vec, blockID);
	}
}
