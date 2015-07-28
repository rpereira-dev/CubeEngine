package com.grillecube.client.renderer;

import java.util.ArrayList;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.gui.QuadRenderer;
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
	
	public void	addRenderer(IRenderer renderer)
	{
		Game.instance().getLogger().log(Logger.Level.FINE, "Adding renderer: " + renderer.getClass().getName());
		this._renderers.add(renderer);
	}
	
	/** call on initialization */
	public void	start()
	{
		this.addRenderer(new TerrainRenderer());
		this.addRenderer(new ModelRenderer());
		this.addRenderer(new QuadRenderer());
		this.addRenderer(new SkyRenderer());
		
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
