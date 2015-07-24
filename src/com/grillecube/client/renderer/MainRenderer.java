package com.grillecube.client.renderer;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.Game;
import com.grillecube.client.window.GLWindow;

public class MainRenderer
{
	/**window */
	private GLWindow	_window;
	
	/** camera */
	private Camera	_camera;
	
	/** renderers */
	private WorldRenderer	_world_renderer;

	public MainRenderer(GLWindow window)
	{
		this._window = window;
		this._world_renderer = new WorldRenderer();
		this._camera = new Camera(window);
	}
	
	/** call on initialization */
	public void	start()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		this._world_renderer.start();
	}
	
	public void	stop()
	{
		this._world_renderer.stop();
	}

	/** main rendering function (screen is already cleared, and frame buffer will be swapped after this render */
	public void render(Game game)
	{
		this._world_renderer.render(game.getWorld(), this._camera);
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
