package com.grillecube.client.renderer;

import java.util.LinkedList;
import java.util.Random;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.font.FontRenderer;
import com.grillecube.client.renderer.model.ModelRenderer;
import com.grillecube.client.renderer.opengl.GLH;
import com.grillecube.client.renderer.particles.ParticleRenderer;
import com.grillecube.client.renderer.terrain.TerrainRenderer;
import com.grillecube.client.window.GLWindow;
import com.grillecube.common.logger.Logger;

public class MainRenderer
{
	/** camera */
	private Camera	_camera;
	
	/** renderers list, will be useful for modding*/
	private LinkedList<IRenderer> _renderers;
	
	/** default renderers */
	private TerrainRenderer	_terrain_renderer;
	private ModelRenderer _model_renderer;
	private FontRenderer _font_renderer;
	private ParticleRenderer _particle_renderer;
	
	/** random number generator */
	private Random _rng;
	
	public MainRenderer(Game game)
	{
		this._camera = new Camera(game.getGLWindow());
		this._renderers = new LinkedList<IRenderer>();
		this._rng = new Random();
		
		this._terrain_renderer = new TerrainRenderer(game);
		this._model_renderer = new ModelRenderer(game);
		this._font_renderer = new FontRenderer(game);
		this._particle_renderer = new ParticleRenderer(game);
	}
	
	public Random getRNG()
	{
		return (this._rng);
	}
	
	/** add a renderer to the rendering list */
	public void	registerRenderer(IRenderer renderer)
	{
		Logger.get().log(Logger.Level.FINE, "Adding renderer: " + renderer.getClass().getName());
		this._renderers.addLast(renderer);
	}
	
	/** call on initialization */
	public void	start(Game game)
	{
		GLH.start();

		this._terrain_renderer.start();
		this._model_renderer.start();
		this._font_renderer.start();
		this._particle_renderer.start();
		
		for (IRenderer renderer : this._renderers)
		{
			renderer.start();
		}
	}

	/** stop the renderer */
	public void	stop()
	{
		Logger.get().log(Logger.Level.FINE, "Stopping Main renderer...");
		GLH.clean(); //clean every GLObject created
	}

	/** main rendering function (screen is already cleared, and frame buffer will be swapped after this render */
	public void render()
	{
		this._terrain_renderer.render();
		this._model_renderer.render();
		
		for (IRenderer renderer : this._renderers)
		{
			renderer.render();
		}
		
		this._particle_renderer.render();
		this._font_renderer.render();

		GLWindow.glCheckError("MainRenderer.render()");
	}

	/** update the renderer */
	public void update()
	{
		this._camera.update();
	}
	
	/** get the camera */
	public Camera	getCamera()
	{
		return (this._camera);
	}
	
	/** font renderer */
	public FontRenderer getFontRenderer()
	{
		return (this._font_renderer);
	}
	
	/** return the default particle renderer */
	public ParticleRenderer getParticleRenderer()
	{
		return (this._particle_renderer);
	}
}
