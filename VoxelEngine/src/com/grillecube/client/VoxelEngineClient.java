package com.grillecube.client;

import java.io.File;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.grillecube.client.opencl.CLH;
import com.grillecube.client.opengl.GLFWContext;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.Logger;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.event.EventCallback;
import com.grillecube.common.event.EventGetTasks;
import com.grillecube.common.event.EventOnLoop;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;

public class VoxelEngineClient extends VoxelEngine {

	/** Game window */
	private GLFWContext glContext;

	/** Renderer */
	private MainRenderer renderer;

	public VoxelEngineClient() {
		super(Side.CLIENT);
	}

	/** initialize libraries + window */
	@Override
	protected final void onInitialized() {

		// init opengl
		GLH.glhInit();
		this.glContext = GLH.glhCreateContext(GLH.glhCreateWindow());
		GLH.glhSetContext(this.glContext);

		Logger.get().log(Logger.Level.FINE, "OpenGL initiated.");
		Logger.get().log(Logger.Level.FINE, "GLFW version: " + GLFW.glfwGetVersionString());

		// init opencl
		CLH.clhInit();

		// main renderer
		this.renderer = new MainRenderer(this);
		this.renderer.initialize();
	}

	@Override
	public void load() {
		super.load();

		// set icon
		File[] files = new File(R.getResPath("textures/blocks/")).listFiles();
		this.getGLFWWindow().setIcon(files[new Random().nextInt(files.length)]);

		// event callback
		this.registerEventCallback(new EventCallback<EventOnLoop>() {
			@Override
			public void invoke(EventOnLoop event) {

				// window update has to be done in the main thread
				// BEGIN FRAME
				getGLFWWindow().clearScreen();

				// render has to be done in the main thread
				// RENDER THE FRAME
				getRenderer().render();

				// FLUSH THE FRAME
				getGLFWWindow().flushScreen();
				getGLFWWindow().pollEvents();

				if (getGLFWWindow().shouldClose()) {
					stopRunning();
				}

				// TODO hard sleep here
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		// get tasks
		this.registerEventCallback(new EventCallback<EventGetTasks>() {
			@Override
			public void invoke(EventGetTasks event) {
				// get all renderer tasks
				renderer.getTasks(event.getEngine(), event.getTasksList());
			}
		});
	}

	@Override
	protected void onDeinitialized() {
		// clean every GLObject create
		GLH.glhStop();
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

	public static VoxelEngineClient instance() {
		return ((VoxelEngineClient) VoxelEngine.instance());
	}
}
