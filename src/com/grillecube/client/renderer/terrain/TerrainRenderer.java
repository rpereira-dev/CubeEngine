package com.grillecube.client.renderer.terrain;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.ARenderer;
import com.grillecube.client.renderer.opengl.object.Texture;
import com.grillecube.client.world.Terrain;

public class TerrainRenderer extends ARenderer
{
	/** rendering program */
	private ProgramTerrain _terrain_program;
	
	/** calculation thread (meshing + visibility) */
	private TerrainRendererThread	_calculation_thread;
	
	public TerrainRenderer(Game game)
	{
		super(game);
	}
	
	/** render the given world */
	@Override
	public void	render()
	{
		this.prepareRendering();
		
		this._terrain_program.useStart();
		{
			this._terrain_program.loadUniforms(this.getWorld().getWeather(), this.getCamera());

			ArrayList<Terrain> to_render = this._calculation_thread.getRendererList();
			
			Texture atlas = this.getResourceManager().getBlockManager().getTextureAtlas();
			atlas.bind(GL11.GL_TEXTURE_2D);
			
			for (Terrain terrain : to_render)
			{
				this._terrain_program.loadInstanceUniforms(terrain);
				terrain.getMesh().render();
			}
			atlas.unbind(GL11.GL_TEXTURE_2D);
		}
		this._terrain_program.useStop();
		
		GL11.glDisable(GL11.GL_CULL_FACE);

	}

	private void prepareRendering()
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_FRONT);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);

		if (GLFW.glfwGetKey(this.getWindow().getPointer(), GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS)
		{
			GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
			GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_LINE);
		}
		else
		{
			GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
			GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_FILL);
		}		
	}

	/** start the renderer */
	@Override
	public void start()
	{
		this._terrain_program = new ProgramTerrain();
		this._calculation_thread = new TerrainRendererThread(this.getGame());
		this.getGame().registerThread(this._calculation_thread);
		
	}
}
