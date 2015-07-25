package com.grillecube.client.world;

import com.grillecube.client.Game;
import com.grillecube.common.world.TerrainLocation;

public class WorldThread extends Thread
{
	private Game	_game;
	
	public WorldThread(Game game)
	{
		this._game = game;
	}
	
	@Override
	public void	run()
	{
		WorldClient	world;
		
		world = this._game.getWorld();

		while (this._game.hasState(Game.STATE_RUNNING))
		{
			world.update();
			try
			{
				Thread.sleep(1000 / 120);
			}
			catch (InterruptedException e)
			{
				break ;
			}
		}
	}

}
