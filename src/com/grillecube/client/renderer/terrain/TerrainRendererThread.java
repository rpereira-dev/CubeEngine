package com.grillecube.client.renderer.terrain;

import java.util.ArrayList;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.Camera;
import com.grillecube.client.world.Terrain;
import com.grillecube.client.world.TerrainLocation;
import com.grillecube.client.world.World;

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
	public void updateTerrains(World world, Camera camera)
	{
		for (Terrain terrain : world.getTerrains())
		{
			TerrainMesh	mesh = terrain.getMesh();
			
			if (!mesh.hasState(TerrainMesh.STATE_VERTICES_UP_TO_DATE))
			{
				mesh.generateVertices();
				mesh.updateFacesVisiblity();
			}
		}
		
		this._terrain_to_render = this.getNewFrustumCullingRendererList(world, camera);
		
//		this._game.getLogger().log(Logger.Level.DEBUG, "Terrain rendered: " + buffer.size());
	}


	/** create an arraylist which contains every terrains that are in view frustum */
	private ArrayList<Terrain> getNewFrustumCullingRendererList(World world, Camera camera)
	{
		ArrayList<Terrain> terrains = new ArrayList<Terrain>(128);
		
		for (Terrain terrain : world.getTerrains())
		{
			if (terrain.isInFrustum(camera))
			{
				terrains.add(terrain);
			}
		}
		return (terrains);
	}
}

class TerrainSearchData
{
	public int from_face; //face id where we come from
	public int to_face; //face id where we come from
	public TerrainLocation to_visit; //the terrain we are visiting
	
	public TerrainSearchData(int from, int to, TerrainLocation to_visit)
	{
		this.from_face = from;
		this.to_face = to;
		this.to_visit = to_visit;
	}
}
