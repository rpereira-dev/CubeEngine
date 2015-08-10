package com.grillecube.client.world;

import java.util.ArrayList;

import com.grillecube.common.logger.Logger;

public class WorldManager
{
	/** default world ids goes here */
	public static int WORLD_DEFAULT;
	
	/** world array list!*/
	private ArrayList<World> _worlds;
	
	public WorldManager()
	{
		this._worlds = new ArrayList<World>(16);
		WorldManager.WORLD_DEFAULT = this.registerWorld(new WorldDefault());
	}
	
	/** add a new world to the game and return it ID */
	public int registerWorld(World world)
	{
		int id = this._worlds.size();
		this._worlds.add(world);
		return (id);
	}
	
	/** return the world for the given ID */
	public World getWorld(int id)
	{
		World world = null;
		
		try {
			world = this._worlds.get(id);
		} catch (Exception e) {
			Logger.get().log(Logger.Level.ERROR, "Tried to get a NULL world");
		}
		return (world);
	}

	/** start every registered worlds */
	public void start()
	{
		for (World world : this._worlds)
		{
			world.start();
		}
	}
}
