package com.grillecube.client.renderer;

import com.grillecube.client.Game;
import com.grillecube.client.window.GLWindow;

public class MainRenderer
{
	/** camera */
	private Camera	_camera;
	
	/** renderers */
	private WorldRenderer	_world_renderer;
	private QuadRenderer	_quad_renderer;

	public MainRenderer(GLWindow window)
	{
		this._world_renderer = new WorldRenderer();
		this._quad_renderer = new QuadRenderer();
		this._camera = new Camera(window);
	}
	
	/** call on initialization */
	public void	start()
	{
		this._world_renderer.start();
		this._quad_renderer.start();
	}
	
	public void	stop()
	{
		this._world_renderer.stop();
	}

	/** main rendering function (screen is already cleared, and frame buffer will be swapped after this render */
	public void render(Game game)
	{
		this._world_renderer.render(game.getWorld(), this._camera);
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
