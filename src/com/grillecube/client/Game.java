
package com.grillecube.client;

import java.util.ArrayList;

import com.grillecube.client.mod.blocks.ModBlocks;
import com.grillecube.client.mod.renderer.font.ModFontRenderer;
import com.grillecube.client.mod.renderer.particles.ModParticles;
import com.grillecube.client.mod.renderer.sky.ModSkyRenderer;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.ressources.ResourceManager;
import com.grillecube.client.window.GLWindow;
import com.grillecube.client.world.World;
import com.grillecube.common.mod.ModLoader;

import fr.toss.lib.Logger;
import fr.toss.lib.Logger.Level;

public class Game
{
	/** game instance */
	private static Game	_instance;
	
	/** Mod loaded */
	private ModLoader	_mod_loader;
	
	/** Logger */
	private Logger	_logger;
	
	/** Game window */
	private GLWindow	_window;
	
	/** World */
	private World	_world;
	
	/** Renderer */
	private MainRenderer	_renderer;
	
	/** Resource manager */
	private ResourceManager	_resources;
	
	/** Game state */
	private GameStateFactory _state_factory;
	private GameState _state;

	/** game threads */
	private ArrayList<Thread> _threads;
	
	/** game events */
	//TODO : events
	private ArrayList[] _events;

	public Game()
	{
		_instance = this;
		this._logger = new Logger(System.out);
		this._threads = new ArrayList<Thread>();
		this._events = new ArrayList[GameEvent.MAX_ID];
		this._state_factory = new GameStateFactory();
		this._state = this._state_factory.registerNewState();
		this._window = new GLWindow();
		this._renderer = new MainRenderer(this._window);
		this._world = new World();
		this._resources = new ResourceManager();
		this._mod_loader = new ModLoader();
	}
	
	public void	start()
	{		
		this.injectMods();
		
		this._logger.log(Level.FINE, "Starting game...");
		this._window.start();
		
		this._mod_loader.initializeAll(this);
		
		this._renderer.start(this);
		this._resources.start();
		this._world.start();
		this._logger.log(Level.FINE, "Game started!");
	}

	/** load every mods */
	private void injectMods()
	{
		this._mod_loader.loadMods("./mods");
		
		//TODO : default mods are injected here
		this._mod_loader.injectMod(new ModBlocks());
		this._mod_loader.injectMod(new ModParticles());
		this._mod_loader.injectMod(new ModSkyRenderer());
		this._mod_loader.injectMod(new ModFontRenderer());
	}

	/** main game loop (dedicated to rendering) */
	public void loop()
	{
		this._state.set(GameState.RUNNING);
		
		for (Thread thrd : this._threads)
		{
			thrd.start();
		}

		while (this.isRunning())
		{
			this._window.prepareScreen();
			this._renderer.update();
			this._renderer.render();
			this._window.flushScreen();
			
			if (this._window.shouldClose())
			{
				this.stop();
			}
		}
		
		this.stopAll();
	}

	/** request the game to stop */
	public void stop()
	{
		this._state.unset(GameState.RUNNING);
	}
	
	/** stop the game properly */
	private void stopAll()
	{
		this._logger.log(Level.FINE, "Stopping game...");

		this._state.unset(GameState.RUNNING);

		for (Thread thrd : this._threads)
		{
			try
			{
				thrd.join();
			}
			catch (InterruptedException e)
			{
				Logger.get().log(Logger.Level.ERROR, "interupted");
				thrd.interrupt();
			}
		}
		this._mod_loader.deinitializeAll(this);
		this._window.stop();
		this._renderer.stop();
		this._logger.log(Level.FINE, "Stopped");
	}
	
	/** register a thread which will be launch when the game will be launched, and stopped when the game ends*/
	public void	registerThread(Thread thrd)
	{
		this._threads.add(thrd);
	}

	/** register a new game state */
	public GameState registerNewState()
	{
		return (this._state_factory.registerNewState());
	}
	
	/** return current game state */
	public GameState getState()
	{
		return (this._state);
	}

	/** return game instance */
	public static Game instance()
	{
		return (_instance);
	}

	/** return the world */
	public World getWorld()
	{
		return (this._world);
	}

	/** return the main renderer */
	public MainRenderer	getRenderer()
	{
		return (this._renderer);
	}
	
	/** return the main resource manager */
	public ResourceManager getResourceManager()
	{
		return (this._resources);
	}

	/** return the window in use */
	public GLWindow getGLWindow()
	{
		return (this._window);
	}

	/** return true if the game is running */
	public boolean isRunning()
	{
		return (this.getState().has(GameState.RUNNING));
	}
}
