package com.grillecube.client.renderer;

import java.util.ArrayList;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.font.FontRenderer;
import com.grillecube.client.renderer.model.ModelRenderer;
import com.grillecube.client.renderer.sky.SkyRenderer;
import com.grillecube.client.renderer.terrain.TerrainRenderer;
import com.grillecube.client.window.GLWindow;

import fr.toss.lib.Logger;

public class MainRenderer
{
	/** camera */
	private Camera	_camera;
	
	/** renderers list, will be useful for modding*/
	private ArrayList<IRenderer>	_renderers;

	public MainRenderer(GLWindow window)
	{
		this._camera = new Camera(window);
		this._renderers = new ArrayList<IRenderer>();
	}
	
	public void	registerRenderer(IRenderer renderer)
	{
		Game.instance().getLogger().log(Logger.Level.FINE, "Adding renderer: " + renderer.getClass().getName());
		this._renderers.add(renderer);
	}
	
	/** call on initialization */
	public void	start()
	{
		//TODO : should default renderer be added like this?
		this.registerRenderer(new TerrainRenderer());
		this.registerRenderer(new ModelRenderer());
		this.registerRenderer(new SkyRenderer());
		this.registerRenderer(new FontRenderer());

		for (IRenderer renderer : this._renderers)
		{
			renderer.start();
		}
	}
	
	public void	stop()
	{
		for (IRenderer renderer : this._renderers)
		{
			renderer.stop();
		}
	}

	/** main rendering function (screen is already cleared, and frame buffer will be swapped after this render */
	public void render(Game game)
	{
		for (IRenderer renderer : this._renderers)
		{
			renderer.render(game.getWorld(), this._camera);
		}
		
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
	

}
