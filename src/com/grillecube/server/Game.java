package com.grillecube.server;

import java.net.SocketException;

import com.grillecube.server.network.Server;

import fr.toss.lib.Logger;

public class Game
{
	private static final Logger	_logger = new Logger(System.out);

	/** Server network */
	private Server	_server;
	
	/** Game states */
	private int	_state;

	public enum GameState
	{
		RUNNING(0);
		
		private int	_id;
		
		GameState(int id)
		{
			this._id = (int) Math.pow(2, id);
		}
		
		int	getID()
		{
			return (this._id);
		}
	}
	
	public Game()
	{
		this._server = new Server(this, 4242);
		this._state = 0;
	}

	/** start the server */
	public void start()
	{
		getLogger().log(Logger.LoggerLevel.FINE, "Starting game...");

		try
		{
			this._server.start();
			getLogger().log(Logger.LoggerLevel.FINE, "Server started!");
		}
		catch (SocketException e)
		{
			getLogger().log(Logger.LoggerLevel.ERROR, "SocketException: " + e);
		}
		
		getLogger().log(Logger.LoggerLevel.FINE, "Started.");
	}
	
	/** main server loop */
	public void	loop()
	{
		this.setState(GameState.RUNNING);
		while (this.hasState(GameState.RUNNING))
		{
			try
			{
				Thread.sleep(1000 / 60);
			}
			catch (InterruptedException e)
			{
				this.unsetState(Game.GameState.RUNNING);
			}
		}
	}
	
	/** stop the server */
	public void stop()
	{
		getLogger().log(Logger.LoggerLevel.FINE, "Stopping game...");
		getLogger().log(Logger.LoggerLevel.FINE, "Stopped");
	}
	
	public void	setState(Game.GameState state)
	{
		this._state = this._state | state.getID();
	}
	
	public void	unsetState(Game.GameState state)
	{
		this._state = this._state & ~(state.getID());
	}
	
	public boolean	hasState(Game.GameState state)
	{
		return ((this._state & state.getID()) == state.getID());
	}

	public void switchState(Game.GameState state)
	{
		if (this.hasState(state))
		{
			this.unsetState(state);
		}
		else
		{
			this.setState(state);
		}
	}
	
	public static Logger	getLogger()
	{
		return (_logger);
	}

	public Server	getServer()
	{
		return (this._server);
	}
	

}
