package com.grillecube.client.renderer.terrain;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.IRenderer;
import com.grillecube.client.world.TerrainClient;
import com.grillecube.client.world.WorldClient;
import com.grillecube.client.world.blocks.BlockTextures;

public class TerrainRenderer implements IRenderer
{
	private ProgramTerrain	_terrain_program;
	
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

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, BlockTextures.getGLTextureAtlas(BlockTextures.RESOLUTION_16));

//		GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
//		GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_LINE);
		
		this._terrain_program.useStart();
		{
			this._terrain_program.loadUniforms(world, camera);
			for (TerrainClient terrain : world.getTerrains())
			{
				this.renderTerrain(terrain, camera);
			}
		}
		this._terrain_program.useStop();
		
		GL11.glDisable(GL11.GL_CULL_FACE);

	}
	
	/** render a terrain */
	private void renderTerrain(TerrainClient terrain, Camera camera)
	{
		if (terrain.getMesh().hasState(TerrainMesh.STATE_VISIBLE))
		{
			this._terrain_program.loadInstanceUniforms(terrain);
			terrain.getMesh().render();				
		}
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
