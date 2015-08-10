package com.grillecube.client.renderer.world;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.world.sky.SkyRenderer;
import com.grillecube.client.renderer.world.terrain.TerrainRenderer;

public class WorldRenderer extends Renderer
{
	/** sky renderer */
	private SkyRenderer _sky_renderer;
	
	/** main terrani renderer */
	private TerrainRenderer	_terrain_renderer;
	
	public WorldRenderer(Game game)
	{
		super(game);
		this._terrain_renderer = new TerrainRenderer(game);
		this._sky_renderer = new SkyRenderer(game);
	}

	@Override
	public void start()
	{
		this._sky_renderer.start();
		this._terrain_renderer.start();
	}

	/** render the given world */
	@Override
	public void render()
	{
		this.getWorld().getWeather().update();
		this.prepareRendering();
		this._sky_renderer.render();
		this._terrain_renderer.render();
		this.endRendering();
	}
	
	private void endRendering()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void prepareRendering()
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

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
}
