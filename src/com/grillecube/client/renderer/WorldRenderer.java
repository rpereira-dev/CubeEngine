package com.grillecube.client.renderer;

import com.grillecube.client.renderer.program.ProgramTerrain;
import com.grillecube.client.world.WorldClient;

public class WorldRenderer
{
	private ProgramTerrain	_terrain_program;
	
	public WorldRenderer()
	{
		
	}
	
	/** render the given world */
	public void	render(WorldClient world, Camera camera)
	{
		
	}

	/** start the renderer */
	public void start()
	{
		this._terrain_program = new ProgramTerrain();
	}
	
	/** stop the renderer */
	public void stop()
	{
		this._terrain_program.delete();
		this._terrain_program = null;
	}
}
