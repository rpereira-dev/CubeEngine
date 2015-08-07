package com.grillecube.client.renderer.terrain;

import java.util.ArrayList;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.Camera;
import com.grillecube.client.world.Terrain;
import com.grillecube.client.world.World;

/** this thread is dedicated to rendering calculation, such as:
 * 
 * - terrain's meshes vertices
 * - distances
 */
public class TerrainRendererThread extends Thread
{
	private static final long SLEEPING_TIME = 1000 / 10;
	
	/** game instance */
	private Game	_game;
	
	/** terrain to render list, the renderer get the next ready list before rendering */
	private ArrayList<Terrain> _terrain_to_render;

	public TerrainRendererThread(Game game)
	{
		this._game = game;
		this._terrain_to_render	= new ArrayList<Terrain>();
	}

	public ArrayList<Terrain>	getRendererList()
	{
		return (this._terrain_to_render);
	}
	
	@Override
	public void	run()
	{
		World	world;
		Camera		camera;
		
		world = this._game.getWorld();
		camera = this._game.getRenderer().getCamera();
		while (this._game.isRunning())
		{
			long t = System.currentTimeMillis();
			this.updateTerrains(world, camera);
			t = (System.currentTimeMillis() - t);
			if (t > 10)
				System.out.println("took: " + t);
			try
			{
				Thread.sleep(SLEEPING_TIME);
			}
			catch (InterruptedException e)
			{
				break ;
			}
		}
	}
	
	/** called in the RenderCalculatriceThread, (not in the rendering one) */
	public void updateTerrains(World world, Camera camera)
	{
		ArrayList<Terrain> terrains = new ArrayList<Terrain>(128);
		
		for (Terrain terrain : world.getTerrains())
		{
			terrain.update(camera);

			if (this.terrainIsVisible(camera, terrain))
			{
				terrains.add(terrain);
			}			
		}
		
		this._terrain_to_render = terrains;
	}
	
	/** return true if the terrain should be rendered */
	private boolean terrainIsVisible(Camera camera, Terrain terrain)
	{
		if (terrain.getCameraDistance() < Terrain.SIZE_DIAGONAL)
		{
			return (true);
		}
		
		if (terrain.getCameraDistance() > camera.getRenderDistance())
		{
			return (false);
		}
		
		//TODO : to fix invisible terrain when rotating fastly, increase imprecision :)
		float imprecision = -20;
		return (terrain.isInFrustum(camera, imprecision));
	}
}
