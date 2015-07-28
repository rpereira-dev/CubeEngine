package com.grillecube.client.renderer;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.WorldClient;
import com.grillecube.common.world.Terrain;

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
			world.getWeather().update();
			
			try
			{
				Thread.sleep(1000 / 60);
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
			
			mesh.updateCameraDistance(camera);

			if (!mesh.hasState(TerrainMesh.STATE_VERTICES_UP_TO_DATE))
			{
				mesh.generateVertices();
			}
			
			if (camera.isInFrustum(mesh.getCenter(), Terrain.SIZE_X / 2, Terrain.SIZE_Y / 2, Terrain.SIZE_Z / 2))
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
