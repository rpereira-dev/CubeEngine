package com.grillecube.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.grillecube.client.renderer.program.ProgramTerrain;
import com.grillecube.client.renderer.terrain.TerrainMesh;
import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.WorldClient;
import com.grillecube.client.world.blocks.BlockTextures;
import com.grillecube.common.world.Terrain;
import com.grillecube.server.Game;

import fr.toss.lib.Logger;

public class WorldRenderer
{
	private ProgramTerrain	_terrain_program;
	
	public WorldRenderer()
	{
		
	}
	
	/** render the given world */
	public void	render(WorldClient world, Camera camera)
	{
		this.renderTerrains(world, camera);
	}

	/** render world terrains */
	private void renderTerrains(WorldClient world, Camera camera)
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

//		GL11.glEnable(GL11.GL_CULL_FACE);
//		GL11.glCullFace(GL11.GL_BACK);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, BlockTextures.getGLTextureMap(BlockTextures.RESOLUTION_16));

		this._terrain_program.useStart();
		{
			this._terrain_program.loadUniforms(camera);
			for (TerrainClient terrain : world.getTerrains())
			{
				if (isTerrainVisible(terrain, camera))
				{
					renderTerrain(terrain);
				}
			}
		}
		this._terrain_program.useStop();
	}
	
	/** render a terrain */
	private void renderTerrain(TerrainClient terrain)
	{
		this._terrain_program.loadInstanceUniforms(terrain);
		for (TerrainMesh mesh : terrain.getMeshes())
		{
			mesh.render();
		}
	}

	/** return true if the terrain is visible */
	private boolean	isTerrainVisible(Terrain terrain, Camera camera)
	{
		//TODO
		return (true);
	}

	/** start the renderer */
	public void start()
	{
		this._terrain_program = new ProgramTerrain();
	}
	
	/** stop the renderer */
	public void stop()
	{
		this._terrain_program.stop();
		this._terrain_program = null;
	}
}
