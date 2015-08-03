package com.grillecube.client.renderer;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.WorldClient;

/** this thread is dedicated to rendering calculation, such as:
 * 
 * - terrain's meshes vertices
 * - distances
 */
public class RenderCalculationThread extends Thread
{
	private Game	_game;
	
	public RenderCalculationThread(Game game)
	{
		this._game = game;
	}
	
	@Override
	public void	run()
	{
		WorldClient	world;
		Camera		camera;
		
		world = this._game.getWorld();
		camera = this._game.getRenderer().getCamera();
		while (this._game.hasState(Game.STATE_RUNNING))
		{
			this.updateTerrains(world, camera);
			
			
			//weather will be update somewhere else later so this thread iwll be dedicated to terrain updates
			world.getWeather().update();
			
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
				break ;
			}
		}
	}
	
	
	/** called in the RenderCalculatriceThread, (not in the rendering one) */
	public void updateTerrains(WorldClient world, Camera camera)
	{
		for (TerrainClient terrain : world.getTerrains())
		{
			TerrainMesh	mesh = terrain.getMesh();
			
			if (!mesh.hasState(TerrainMesh.STATE_VERTICES_UP_TO_DATE))
			{
				mesh.generateVertices();
				mesh.updateFacesVisiblity();
			}

			//TODO : change visibility test by an occlusion test
			if (mesh.isInFrustum(camera))
			{
				mesh.setState(TerrainMesh.STATE_VISIBLE);
			}
			else
			{
				mesh.unsetState(TerrainMesh.STATE_VISIBLE);
			}
		}
	}

}
