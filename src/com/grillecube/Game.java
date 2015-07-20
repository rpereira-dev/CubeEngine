package com.grillecube;

import com.grillecube.logger.Logger;
import com.grillecube.logger.Logger.LoggerLevel;
import com.grillecube.window.GLWindow;

public class Game
{
	/** game instance */
	private static Game	_instance;
	
	/** Logger */
	private Logger	_logger;
	
	/** Game window */
	private GLWindow	_window;
	
	/** Game state */
	public static final long STATE_RUNNING = 1;
	private long _state;
	
	public Game()
	{
		_instance = this;
		this._logger = new Logger();
		this._state = 0;
		this._window = new GLWindow();
	}
	
	public void	start()
	{
		this._logger.log(LoggerLevel.FINE, "Starting game...");
		this._window.start();
		this._state = STATE_RUNNING;
		this._logger.log(LoggerLevel.FINE, "Game started!");
	}
	
	public void	loop()
	{
		
	}
	
	public void	stop()
	{
		this._window.stop();
	}
	
	public boolean	gameHasState(long state)
	{
		return ((this._state & state) == state);
	}
	
	public void		gameSetState(int state)
	{
		this._state = this._state | state;
	}
	
	public void		gameUnsetState(long state)
	{
		this._state = this._state & ~(state);
	}
	
	public void		gameSwitchState(long state)
	{
		this._state = this._state ^ state;
	}

	public static Game	instance()
	{
		return (_instance);
	}
	
	public static void	log(LoggerLevel level, String message)
	{
		_instance.getLogger().log(level, message);
	}

	public Logger	getLogger()
	{
		return (this._logger);
	}
}
