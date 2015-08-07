package com.grillecube.client.renderer;

import java.util.Random;

import com.grillecube.client.Game;
import com.grillecube.client.ressources.ResourceManager;
import com.grillecube.client.window.GLWindow;
import com.grillecube.client.world.Weather;
import com.grillecube.client.world.World;

public abstract class ARenderer implements IRenderer
{
	private Game	_game;
	
	public ARenderer(Game game)
	{
		this._game = game;
	}
	
	protected Game getGame()
	{
		return (this._game);
	}
	
	protected World getWorld()
	{
		return (this._game.getWorld());
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
	
	protected GLWindow	getWindow()
	{
		return (this._game.getGLWindow());
	}
	
	protected ResourceManager getResourceManager()
	{
		return (this._game.getResourceManager());
	}
	
	protected Weather getWeather()
	{
		return (this._game.getWorld().getWeather());
	}
	
	@Override
	public abstract void start();

	@Override
	public abstract void stop();

	@Override
	public abstract void render();

}
