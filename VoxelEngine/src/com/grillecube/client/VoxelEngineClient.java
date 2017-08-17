package com.grillecube.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opencl.CLH;
import com.grillecube.client.opengl.GLFWContext;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.client.sound.ALH;
import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.World;

public class VoxelEngineClient extends VoxelEngine {

	/** Game window */
	private GLFWContext glContext;

	/** Renderer */
	private MainRenderer renderer;

	/** the world currently set */
	private World world;

	public VoxelEngineClient() {
		super(Side.CLIENT);
		this.initializeLWJGL();
	}

	/** initialize libraries + window */
	private void initializeLWJGL() {

		// init opengl
		GLH.glhInit();
		this.glContext = GLH.glhCreateContext(GLH.glhCreateWindow());
		GLH.glhSetContext(this.glContext);

		Logger.get().log(Logger.Level.FINE, "OpenGL initiated.");
		Logger.get().log(Logger.Level.FINE, "GLFW version: " + GLFW.glfwGetVersionString());

		// init openal
		ALH.alhInit();

		// init opencl
		CLH.clhInit();
	}

	@Override
	protected void preLoaded() {
		this.renderer = new MainRenderer(this);
	}

	@Override
	protected void postLoaded() {
		this.renderer.initialize();
		File[] files = new File(R.getResPath("textures/blocks/")).listFiles();
		this.getGLFWWindow().setIcon(files[new Random().nextInt(files.length)]);
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

		// TODO : clean this
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return (this.renderer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResourceManagerClient getResourceManager() {
		return ((ResourceManagerClient) super.resources);
	}

	/** get the window */
	public final GLFWWindow getGLFWWindow() {
		return (this.getGLContext().getWindow());
	}

	public final GLFWContext getGLContext() {
		return (this.glContext);
	}

	/** set current world for the client */
	public final void setWorld(int worldID) {
		this.setWorld(this.getResourceManager().getWorldManager().getWorld(worldID));
	}

	/** set the current world (can be null) */
	private final void setWorld(World world) {
		if (this.world != null) {
			this.world.unset();
		}
		this.world = world;
		this.renderer.setWorld(this.world);
		if (this.world != null) {
			this.world.set();
		}
	}

	/** get the current world of the client (can be null) */
	public World getWorld() {
		return (this.world);
	}

	public static VoxelEngineClient instance() {
		return ((VoxelEngineClient) VoxelEngine.instance());
	}

	@Override
	protected void getTasks(ArrayList<Callable<Taskable>> tasks) {
		// get all renderer tasks
		this.renderer.getTasks(this, tasks);

		// get all world tasks
		if (this.world != null) {
			this.world.getTasks(this, tasks);
		}
	}
}
