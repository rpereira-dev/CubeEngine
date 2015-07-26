package com.grillecube.client.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.model.Models;
import com.grillecube.client.world.entity.EntityModeled;
import com.grillecube.common.world.TerrainLocation;
import com.grillecube.common.world.World;

public class WorldClient extends World
{
	/** every loaded terrain are in */
	private HashMap<TerrainLocation, TerrainClient>	_terrains;
	
	private ArrayList<EntityModeled>	_entities;
	
	public WorldClient()
	{
		super();
		this._terrains = new HashMap<TerrainLocation, TerrainClient>();
		this._entities = new ArrayList<EntityModeled>();
	}
	

	/** called on initiliaztion */
	public void start()
	{
		this.addTerrain(new TerrainClient(new TerrainLocation(0, 0, 0)));				
		
		this._entities.add(new EntityModeled(Models.getModel(Models.PIG))
		{

			@Override
			protected void updateEntity() {}
	
		});
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
}
