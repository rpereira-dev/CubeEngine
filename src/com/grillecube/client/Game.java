package com.grillecube.client;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.RenderCalculationThread;
import com.grillecube.client.window.GLWindow;
import com.grillecube.client.world.WorldClient;
import com.grillecube.client.world.blocks.Blocks;

import fr.toss.lib.Logger;
import fr.toss.lib.Logger.Level;

public class Game
{
	/** game instance */
	private static Game	_instance;
	
	/** Thread pool */
	private static final int THRD_CALCUL	= 0;
	private static final int THRD_MAX		= 1;
	private Thread	_threads[];
	
	/** Logger */
	private Logger	_logger;
	
	/** Game window */
	private GLWindow	_window;
	
	/** World */
	private WorldClient	_world;
	
	/** Renderer */
	private MainRenderer	_renderer;
	
	/** Game state */
	public static final long STATE_RUNNING = 1;
	private long _state;
	
	public Game()
	{
		_instance = this;
		this._threads = new Thread[THRD_MAX];
		this._logger = new Logger(System.out);
		this._state = 0;
		this._window = new GLWindow();
		this._renderer = new MainRenderer(this._window);
		this._world = new WorldClient();
	}
	
	public void	start()
	{
		this._logger.log(Level.FINE, "Starting game...");
		this._state = 0;
		this._window.start();
		this._renderer.start();
		this._world.start();
		Blocks.initBlocks();
		this._threads[THRD_CALCUL] = new RenderCalculationThread(this);
		this._logger.log(Level.FINE, "Game started!");
	}

	/** main game loop (dedicated to rendering) */
	public void loop()
	{
		this.setState(STATE_RUNNING);
		
		for (Thread thrd : this._threads)
		{
			thrd.start();
		}
				
		long	prev = System.currentTimeMillis();
		int		frames = 0;

	//	this._window.useVSync(0);
		while (this._window.shouldClose() == false)
		{
			if (System.currentTimeMillis() - prev >= 1000)
			{
				log(Logger.Level.DEBUG, "fps: " + frames);
				frames = 0;
				prev = System.currentTimeMillis();
			}
			
			this._window.prepareScreen();
			this._renderer.update();
			this._renderer.render(this);
			this._window.flushScreen();
			
			frames++;
		}
		
		this.unsetState(STATE_RUNNING);
	}

	public void	stop()
	{
		this._logger.log(Level.FINE, "Stopping game...");

		for (Thread thrd : this._threads)
		{
			try
			{
				thrd.join();
			}
			catch (InterruptedException e)
			{
				log(Logger.Level.ERROR, "interupted");
				thrd.interrupt();
			}
		}
		this._window.stop();
		this._logger.log(Level.FINE, "Stopped");
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
	
	public static void	log(Level level, String message)
	{
		_instance.getLogger().log(level, message);
	}

	public Logger	getLogger()
	{
		return (this._logger);
	}

	public WorldClient getWorld()
	{
		return (this._world);
	}

	public MainRenderer	getRenderer()
	{
		return (this._renderer);
	}
}
