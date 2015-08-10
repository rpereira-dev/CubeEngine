package com.grillecube.client.renderer.world.terrain;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.Game;
import com.grillecube.client.opengl.object.Texture;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.world.Terrain;
import com.grillecube.client.world.World;

public class TerrainRenderer extends Renderer
{
	/** rendering program */
	private ProgramTerrain _terrain_program;
	
	/** calculation thread (meshing + visibility) */
	private TerrainRendererThread _calculation_thread;
	
	public TerrainRenderer(Game game)
	{
		super(game);
	}
	
	/** render terrains in the given world */
	@Override
	public void	render()
	{
		World world = this.getWorld();
		
		this._terrain_program.useStart();
		{
			this._terrain_program.loadUniforms(world.getWeather(), this.getCamera());

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
