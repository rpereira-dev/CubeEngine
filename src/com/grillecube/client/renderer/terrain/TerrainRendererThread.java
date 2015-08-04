package com.grillecube.client.renderer.terrain;

import java.util.ArrayList;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.Camera;
import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.WorldClient;

import fr.toss.lib.Logger;

/** this thread is dedicated to rendering calculation, such as:
 * 
 * - terrain's meshes vertices
 * - distances
 */
public class TerrainRendererThread extends Thread
{
	private static final long SLEEPING_TIME = 1000 / 20;
	
	/** game instance */
	private Game	_game;
	
	/** terrain to render list, the renderer get the next ready list before rendering */
	private ArrayList<TerrainClient>	_terrain_to_render;
	
	public TerrainRendererThread(Game game)
	{
		this._game = game;
		this._terrain_to_render	= new ArrayList<TerrainClient>();
	}

	public ArrayList<TerrainClient>	getRendererList()
	{
		return (this._terrain_to_render);
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
	public void updateTerrains(WorldClient world, Camera camera)
	{
		ArrayList<TerrainClient> buffer = new ArrayList<TerrainClient>(128);
		
		for (TerrainClient terrain : world.getTerrains())
		{
			TerrainMesh	mesh = terrain.getMesh();
			
			if (!mesh.hasState(TerrainMesh.STATE_VERTICES_UP_TO_DATE))
			{
				mesh.generateVertices();
				mesh.updateFacesVisiblity();
			}
		}
		
		this.fillRenderingBuffer(world, camera, buffer);
		
		this._terrain_to_render = buffer;
		
//		this._game.getLogger().log(Logger.Level.DEBUG, "Terrain rendered: " + buffer.size());
	}

	private void fillRenderingBuffer(WorldClient world, Camera camera, ArrayList<TerrainClient> buffer)
	{
		TerrainClient terrain = world.getTerrainAt(camera.getPosition());
		
		for (TerrainClient t : world.getTerrains())
		{
			if (t.getMesh().isInFrustum(camera))
			{
				buffer.add(t);
			}
		}
		
	}

}
