package com.grillecube.client.renderer;

import java.util.Random;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.client.ressources.ResourceManager;
import com.grillecube.client.window.GLWindow;
import com.grillecube.client.world.World;

public abstract class Renderer implements IRenderer
{
	private Game _game;
	
	public Renderer(Game game)
	{
		this._game = game;
	}
	
	/** return game instance */
	protected Game getGame()
	{
		return (this._game);
	}
	
	protected Random getRNG()
	{
		return (this._game.getRenderer().getRNG());
	}
	
	protected MainRenderer	getRenderer()
	{
		return (this._game.getRenderer());
	}
	
	protected Camera	getCamera()
	{
		return (this._game.getRenderer().getCamera());
	}
	
	protected GLWindow getWindow()
	{
		return (this._game.getGLWindow());
	}
	
	protected ResourceManager getResourceManager()
	{
		return (this._game.getResourceManager());
	}
	
	/** return current world */
	protected World getWorld()
	{
		return (this._game.getCurrentWorld());
	}
	
	@Override
	public abstract void start();

	@Override
	public abstract void render();

}
