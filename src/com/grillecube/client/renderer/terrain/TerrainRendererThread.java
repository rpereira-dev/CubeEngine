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
	private static final long SLEEPING_TIME = 1000 / 120;
	
	/** game instance */
	private Game _game;
	
	/** terrain to render list, the renderer get the next ready list before rendering */
	private ArrayList<Terrain> _terrain_to_render;

	/** mesher to update terrains mesh */
	private TerrainMesher _mesher;

	public TerrainRendererThread(Game game)
	{
		this._game = game;
		this._terrain_to_render	= new ArrayList<Terrain>();
	}

	public ArrayList<Terrain> getRendererList()
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
		this._mesher = new TerrainMesher(this._game.getResourceManager().getBlockManager().getBlockCount());
		while (this._game.isRunning())
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
		ArrayList<Terrain> terrains = new ArrayList<Terrain>(128);
		
		for (Terrain terrain : world.getTerrains())
		{
			if (!terrain.getMesh().hasState(TerrainMesh.STATE_VERTICES_UP_TO_DATE))
			{
				terrain.getMesh().updateVertices(this._mesher);
			}
						
			if (this.terrainIsVisible(camera, terrain))
			{
				terrains.add(terrain);
			}			
		}
		
		this._terrain_to_render = terrains;
//		Logger.get().log(Logger.Level.DEBUG, "Terrain rendered: " + terrains.size());
	}
	
	/** return true if the terrain should be rendered */
	private boolean terrainIsVisible(Camera camera, Terrain terrain)
	{
		if (terrain.getMesh().getVertexCount() == 0 && terrain.getMesh().hasState(TerrainMesh.STATE_VBO_UP_TO_DATE))
		{
			return (false);
		}
		
		float camera_dist = terrain.getCameraDistance(camera);
		if (camera_dist < Terrain.SIZE_DIAGONAL) //if terrain is nearby, always render it
		{
			return (true);
		}
		
		if (camera_dist > camera.getRenderDistance()) //if terrain is too far, dont render it
		{
			return (false);
		}
			
		//TODO : to fix invisible terrain when rotating fastly
		//		increase imprecision (this is the angle in degree to increase FOV at rendering time):)
		float imprecision = 10;
		return (terrain.isInFrustum(camera, imprecision));
	}
}
