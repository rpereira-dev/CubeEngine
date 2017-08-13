/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.opengl.GLFWListenerResize;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLFrameBuffer;
import com.grillecube.client.opengl.object.GLProgramPostProcessing;
import com.grillecube.client.opengl.object.GLRenderBuffer;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.opengl.object.GLVertexArray;
import com.grillecube.client.opengl.object.GLVertexBuffer;
import com.grillecube.client.renderer.camera.CameraPerspectiveWorld;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.event.EventPostRender;
import com.grillecube.client.renderer.event.EventPreRender;
import com.grillecube.client.renderer.gui.GuiRenderer;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.World;

public class MainRenderer implements Taskable, GLFWListenerResize {

	/**
	 * screen referentials (bottom left to top right) varies from
	 * 
	 * (0, 0) to (1,1) from window referential
	 * 
	 * -1, -1) to (1, 1) from openg referential
	 */
	public static final Matrix4f WINDOW_TO_GL_BASIS = new Matrix4f();

	static {
		WINDOW_TO_GL_BASIS.translate(-1.0f, -1.0f, 0.0f);
		WINDOW_TO_GL_BASIS.scale(2.0f, 2.0f, 1.0f);
	}

	public static final Matrix4f GL_TO_WINDOW_BASIS = new Matrix4f();

	static {
		Matrix4f.invert(WINDOW_TO_GL_BASIS, GL_TO_WINDOW_BASIS);
	}

	/** resource manager */
	private VoxelEngineClient engine;

	/** post processing shaders programs */
	private GLProgramPostProcessing finalProcessProgram;
	private GLProgramPostProcessing postProcessingProgram;

	/** camera */
	private CameraProjectiveWorld camera;

	/** default renderers */
	private WorldRenderer worldRenderer;
	private GuiRenderer guiRenderer;

	private ArrayList<GLTask> glTasks;

	/** random number generator */
	private Random rng;

	/** event instances (so we do not realloc them every frames */
	private EventPreRender preRenderEvent;
	private EventPostRender postRenderEvent;

	/** main fbo */
	private GLFrameBuffer fbo;
	private GLTexture fboTexture;
	private GLRenderBuffer fboDepthBuffer;

	/** values */
	private int verticesDrawn;
	private int drawCalls;

	/** default and simple vao (to use geometry shaders) */
	private GLVertexArray _default_vao;
	private GLVertexBuffer _default_vbo;

	private boolean toggle = true;

	public MainRenderer(VoxelEngineClient engine) {
		this.engine = engine;
	}

	/** called after resources where loaded */
	public void initialize() {

		GLH.glhCheckError("Pre mainrenderer initialization");
		Logger.get().log(Level.FINE, "Initializing " + this.getClass().getSimpleName());

		this.glTasks = new ArrayList<GLTask>();
		this.rng = new Random();
		this.preRenderEvent = new EventPreRender(this);
		this.postRenderEvent = new EventPostRender(this);

		this.initialiseDefaultVAO();
		this.initializeMainFBO(this.getGLFWWindow().getWidth(), this.getGLFWWindow().getHeight());

		this.worldRenderer = new WorldRenderer(this);
		this.guiRenderer = new GuiRenderer(this);

		this.getGLFWWindow().addResizeListener(this);

		Logger.get().log(Level.FINE, "Initializing " + this.getClass().getSimpleName());

		Logger.get().log(Level.FINE, "Initializing " + this.guiRenderer.getClass().getSimpleName());
		GLH.glhCheckError("pre guirenderer starts");
		this.guiRenderer.initialize();
		// this.guiRenderer.addView(new GuiViewDefault());
		GLH.glhCheckError("post guirenderer starts");

		this.finalProcessProgram = new GLProgramPostProcessing(R.getResPath("shaders/post_process/post_processing.fs"));
		this.setPostProcessingProgram(null);
		// this.setPostProcessingProgram(new
		// GLProgramPostProcessing(R.getResPath("shaders/post_process/blurv.fs")));
		// this.setPostProcessingProgram(new
		// GLProgramPostProcessing(R.getResPath("shaders/post_process/blurh.fs")));
		// this.setPostProcessingProgram(new
		// GLProgramPostProcessing(R.getResPath("shaders/post_process/drunk.fs")));
		// this.setPostProcessingProgram(new
		// GLProgramPostProcessing(R.getResPath("shaders/post_process/fisheye.fs")));

		Logger.get().log(Level.FINE, "Done");
		GLH.glhCheckError("post mainrenderer started");
	}

	/** the main fbo */
	private void initializeMainFBO(int width, int height) {

		GLH.glhCheckError("pre mainrenderer fbo creation");

		this.fbo = GLH.glhGenFBO();
		this.fbo.bind();
		this.fbo.createDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		this.fboTexture = GLH.glhGenTexture();
		this.fboTexture.bind(GL11.GL_TEXTURE_2D);
		this.fboTexture.image2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		this.fboTexture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		this.fboTexture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		this.fbo.createTextureAttachment(this.fboTexture, GL30.GL_COLOR_ATTACHMENT0);

		this.fboDepthBuffer = this.fbo.createRenderBuffer(width, height, GL30.GL_DEPTH_ATTACHMENT);

		this.fbo.unbind();
		GLH.glhCheckError("post mainrenderer fbo creation");
	}

	private void initialiseDefaultVAO() {
		GLH.glhCheckError("pre default vao");

		this._default_vao = GLH.glhGenVAO();
		this._default_vbo = GLH.glhGenVBO();

		this._default_vao.bind();
		this._default_vbo.bind(GL15.GL_ARRAY_BUFFER);
		this._default_vao.enableAttribute(0);
		this._default_vao.setAttribute(0, 1, GL11.GL_FLOAT, false, 0, 0);

		float[] points = { 0 };
		this._default_vbo.bufferData(GL15.GL_ARRAY_BUFFER, points, GL15.GL_STATIC_DRAW);

		GLH.glhCheckError("post default vao");
	}

	/** called whenever the window is resized */
	@Override
	public void invokeResize(GLFWWindow window, int width, int height) {
		this.deleteMainVBO();
		this.initializeMainFBO(width, height); // maybe dont do it
	}

	public void deinitialize() {
		this.getGLFWWindow().removeResizeListener(this);

		this.guiRenderer.deinitialize();
		this.worldRenderer.deinitialize();

		GLH.glhDeleteObject(this._default_vao);
		this._default_vao = null;

		GLH.glhDeleteObject(this._default_vbo);
		this._default_vbo = null;
	}

	private void deleteMainVBO() {
		GLH.glhDeleteObject(this.fbo);
		GLH.glhDeleteObject(this.fboTexture);
		GLH.glhDeleteObject(this.fboDepthBuffer);
	}

	/**
	 * main rendering function (screen is already cleared, and frame buffer will
	 * be swapped after this render
	 */
	public void render() {

		// run tasks
		while (!this.glTasks.isEmpty()) {
			GLTask task = this.glTasks.remove(0);
			task.run();
		}

		// if renderer is not enabled, return
		if (!this.toggle) {
			return;
		}

		// TODO move this somewhere else, if openal is thread safe
		if (this.getCamera() != null) {
			this.getCamera().update();
			this.engine.getResourceManager().getSoundManager().update(this.getCamera());
		}

		this.getResourceManager().getEventManager().invokeEvent(this.preRenderEvent);

		// reset these values before next rendering
		this.drawCalls = GLH.glhGetContext().resetDrawCalls();
		this.verticesDrawn = GLH.glhGetContext().resetDrawVertices();

		// if there is a world
		if (this.getWorldRenderer().getWorld() != null && this.getCamera() != null) {
			// render it
			this.worldRenderer.preRender();

			// refresh fbo
			this.getFBO().bind();
			this.getFBO().clear();
			this.getFBO().viewport(0, 0, this.getGLFWWindow().getWidth(), this.getGLFWWindow().getHeight());
			this.worldRenderer.render();
			this.renderPostProcessingEffects(); // post processing effects
			this.getFBO().unbind(); // render the world to the main fbo

			this.worldRenderer.postRender();
		}

		// render guis to default fbo
		this.guiRenderer.preRender();
		this.guiRenderer.render();
		this.guiRenderer.postRender();

		this.getResourceManager().getEventManager().invokeEvent(this.postRenderEvent);
	}

	private void renderPostProcessingEffects() {

		if (this.postProcessingProgram != null) {

			// bind the fbo texture to texture attachment 0
			this.getFBOTexture().bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);

			this.postProcessingProgram.useStart();
			this.postProcessingProgram.loadUniforms(this);
			this._default_vao.bind();
			GLH.glhDrawArrays(GL11.GL_POINTS, 0, 1);
			this.postProcessingProgram.useStop();
		}
	}

	/**
	 * return the default vertex array. It contains a single attribute (bound on
	 * 0) with a VBO which contains a single float (this float is 0.0f) you
	 * should use it as a default VAO for geometry
	 */
	public GLVertexArray getDefaultVAO() {
		return (this._default_vao);
	}

	/** return the draw calls for the last frame */
	public int getDrawCalls() {
		return (this.drawCalls);
	}

	/** return the vertices drawn for the last frame */
	public int getVerticesDrawn() {
		return (this.verticesDrawn);
	}

	/** get the resource manager */
	public ResourceManagerClient getResourceManager() {
		return (this.engine.getResourceManager());
	}

	/** get the camera */
	public CameraProjectiveWorld getCamera() {
		return (this.camera);
	}

	public void setCamera(CameraProjectiveWorld camera) {
		Logger.get().log(Level.FINE, "Setting MainRenderer camera: " + camera);
		this.camera = camera;
	}

	/** font renderer */
	public GuiRenderer getGuiRenderer() {
		return (this.guiRenderer);
	}

	/** get the world renderer */
	public WorldRenderer getWorldRenderer() {
		return (this.worldRenderer);
	}

	/** OpenGL tasks to be realized in main thread */
	public interface GLTask {
		public void run();
	}

	/** get current window on which the main renderer is rendering */
	public GLFWWindow getGLFWWindow() {
		return (this.engine.getGLFWWindow());
	}

	public VoxelEngineClient getEngine() {
		return (this.engine);
	}

	public void setWorld(World world) {
		this.worldRenderer.setWorld(world);
		if (this.camera != null && this.camera instanceof CameraPerspectiveWorld) {
			((CameraPerspectiveWorld) this.camera).setWorld(world);
		}
	}

	public void setPostProcessingProgram(GLProgramPostProcessing program) {
		this.postProcessingProgram = program;
	}

	public GLFrameBuffer getFBO() {
		return (this.fbo);
	}

	public GLTexture getFBOTexture() {
		return (this.fboTexture);
	}

	public Random getRNG() {
		return (this.rng);
	}

	public void addGLTask(GLTask task) {
		this.glTasks.add(task);
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {

		this.worldRenderer.getTasks(engine, tasks);
		this.guiRenderer.getTasks(engine, tasks);
	}

	/** set to true of false weather you want to render or not */
	public void toggle(boolean b) {
		this.toggle = b;
	}

}
