package com.grillecube.client.renderer;

import java.util.LinkedList;
import java.util.Stack;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.model.ModelRenderer;
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
	

	public MainRenderer(GLWindow window)
	{
		this._camera = new Camera(window);
		this._renderers = new LinkedList<IRenderer>();
	}
	
	public void	registerRenderer(IRenderer renderer)
	{
		Logger.get().log(Logger.Level.FINE, "Adding renderer: " + renderer.getClass().getName());
		this._renderers.addLast(renderer);
	}
	
	/** call on initialization */
	public void	start(Game game)
	{
		//TODO : should default renderer be added like this?
		this._renderers.addFirst(new TerrainRenderer(game));
		this._renderers.addFirst(new ModelRenderer(game));

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
	public void render()
	{
		for (IRenderer renderer : this._renderers)
		{
			renderer.render();
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
