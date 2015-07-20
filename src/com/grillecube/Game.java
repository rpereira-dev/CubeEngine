package com.grillecube;

import com.grillecube.logger.Logger;
import com.grillecube.logger.Logger.LoggerLevel;
import com.grillecube.renderer.MainRenderer;
import com.grillecube.window.GLWindow;

public class Game
{
	/** game instance */
	private static Game	_instance;
	
	/** Thread pool */
	private Thread	_threads[];
	
	/** Logger */
	private Logger	_logger;
	
	/** Game window */
	private GLWindow	_window;
	
	/** Renderer */
	private MainRenderer	_renderer;
	
	/** Game state */
	public static final long STATE_RUNNING = 1;
	private long _state;
	
	public Game()
	{
		_instance = this;
		this._threads = new Thread[0];
		this._logger = new Logger();
		this._state = 0;
		this._window = new GLWindow();
		this._renderer = new MainRenderer();
	}
	
	public void	start()
	{
		this._logger.log(LoggerLevel.FINE, "Starting game...");
		this._window.start();
		this._renderer.start();
		this._state = STATE_RUNNING;
		this._logger.log(LoggerLevel.FINE, "Game started!");
	}
	

	public void loop()
	{
		this.setState(STATE_RUNNING);
		
		while (this._window.shouldClose() == false)
		{
			this._window.clear();
			this._renderer.render();
			this._window.update();
			
			try
			{
				Thread.sleep(1000 / 60);
			}
			catch (InterruptedException e)
			{
				break ;
			}
		}
		
		this.unsetState(STATE_RUNNING);
	}

	public void	stop()
	{
		for (Thread thrd : this._threads)
		{
			try
			{
				thrd.join();
			}
			catch (InterruptedException e)
			{
				log(Logger.LoggerLevel.ERROR, e.getLocalizedMessage());
			}
		}
		this._window.stop();
	}
	
	public boolean	hasState(long state)
	{
		return ((this._state & state) == state);
	}
	
	public void		setState(long state)
	{
		this._state = this._state | state;
	}
	
	public void		unsetState(long state)
	{
		this._state = this._state & ~(state);
	}
	
	public void		switchState(long state)
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
