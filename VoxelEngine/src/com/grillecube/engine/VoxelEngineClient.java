package com.grillecube.engine;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.grillecube.engine.opencl.CLH;
import com.grillecube.engine.opengl.GLFWContext;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.renderer.MainRenderer;
import com.grillecube.engine.resources.ResourceManager;
import com.grillecube.engine.resources.ResourceManagerClient;
import com.grillecube.engine.sound.ALH;
import com.grillecube.engine.world.World;

public class VoxelEngineClient extends VoxelEngine {

	/** Game window */
	private GLFWContext _gl_context;

	/** Renderer */
	private MainRenderer _renderer;

	/** the world currently set */
	private World _world;

	public VoxelEngineClient() {
		super(Side.CLIENT);
		this.initializeLWJGL();
	}

	/** initialize libraries + window */
	private void initializeLWJGL() {

		// init opengl
		GLH.glhInit();
		this._gl_context = GLH.glhCreateContext(GLH.glhCreateWindow());
		GLH.glhSetContext(this._gl_context);

		Logger.get().log(Logger.Level.FINE, "OpenGL initiated.");
		Logger.get().log(Logger.Level.FINE, "GLFW version: " + GLFW.glfwGetVersionString());

		// init openal
		ALH.alhInit();

		// init opencl
		CLH.clhInit();
	}

	@Override
	protected void preLoaded() {
		this._renderer = new MainRenderer(this);
	}

	@Override
	protected void postLoaded() {
		this._renderer.initialize();
	}

	@Override
	public void onLoopStart() {
		// this.getResourceManager().getWorldManager().restartWorldUpdates();
	}

	@Override
	protected void onLoopEnd() {
	}

	@Override
	public void onLoop() {

		// window update has to be done in the main thread
		// BEGIN FRAME
		this.getGLFWWindow().clearScreen();
		this.getGLFWWindow().update();

		// render has to be done in the main thread
		// RENDER THE FRAME
		this.getRenderer().render();

		// FLUSH THE FRAME
		this.getGLFWWindow().flushScreen();
		if (this.getGLFWWindow().shouldClose()) {
			super.stop();
		}
	}

	@Override
	protected void onStopped() {
		this.setWorld(null);

		// clean every GLObject create
		GLH.glhStop();

		// clean openal
		ALH.alhStop();
	}

	@Override
	protected ResourceManager instanciateResourceManager() {
		return (new ResourceManagerClient(this));
	}

	// public void startNetwork(String host, int port)
	// {
	// _network = new ClientNetwork();
	// try
	// {
	// _network.start(host, port);
	// }
	// catch (Exception exception)
	// {
	// Logger.get().log(Level.ERROR, "Couldnt start networking.");
	// exception.printStackTrace(Logger.get().getPrintStream());
	// }
	// }

	/** return the main renderer */
	public MainRenderer getRenderer() {
		return (this._renderer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResourceManagerClient getResourceManager() {
		return ((ResourceManagerClient) (super._resources));
	}

	/** get the window */
	public GLFWWindow getGLFWWindow() {
		return (this.getGLContext().getWindow());
	}

	public GLFWContext getGLContext() {
		return (this._gl_context);
	}

	/** set current world for the client */
	public void setWorld(int worldID) {
		this.setWorld(this.getResourceManager().getWorldManager().getWorld(worldID));
	}

	/** set the current world (can be null) */
	public void setWorld(World world) {
		if (this._world != null) {
			this._world.unset();
		}
		this._world = world;
		this._renderer.setWorld(this._world);
		if (this._world != null) {
			this._world.set();
		}
	}

	/** get the current world of the client (can be null) */
	public World getWorld() {
		return (this._world);
	}

	public static VoxelEngineClient instance() {
		return ((VoxelEngineClient) VoxelEngine.instance());
	}

	@Override
	protected void getTasks(ArrayList<Callable<Taskable>> tasks) {
		// get all renderer tasks
		this._renderer.getTasks(this, tasks);

		// get all world tasks
		if (this._world != null) {
			this._world.getTasks(this, tasks);
		}
	}
}
