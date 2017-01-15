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

package com.grillecube.engine.renderer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.grillecube.engine.Logger;
import com.grillecube.engine.Logger.Level;
import com.grillecube.engine.Taskable;
import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.VoxelEngineClient;
import com.grillecube.engine.event.renderer.EventPostRender;
import com.grillecube.engine.event.renderer.EventPreRender;
import com.grillecube.engine.opengl.GLFWListenerResize;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLFrameBuffer;
import com.grillecube.engine.opengl.object.GLProgramPostProcessing;
import com.grillecube.engine.opengl.object.GLRenderBuffer;
import com.grillecube.engine.opengl.object.GLTexture;
import com.grillecube.engine.opengl.object.GLVertexArray;
import com.grillecube.engine.opengl.object.GLVertexBuffer;
import com.grillecube.engine.renderer.camera.CameraPerspectiveWorld;
import com.grillecube.engine.renderer.camera.CameraProjectiveWorld;
import com.grillecube.engine.renderer.gui.GuiRenderer;
import com.grillecube.engine.renderer.world.WorldRenderer;
import com.grillecube.engine.resources.R;
import com.grillecube.engine.resources.ResourceManagerClient;
import com.grillecube.engine.world.World;

public class MainRenderer implements Taskable, GLFWListenerResize {

	/** resource manager */
	private VoxelEngineClient _engine;

	/** post processing shaders programs */
	private GLProgramPostProcessing _final_process_program;
	private GLProgramPostProcessing _post_processing_program;

	/** camera */
	private CameraProjectiveWorld _camera;

	/** default renderers */
	private WorldRenderer _world_renderer;
	private GuiRenderer _gui_renderer;

	private ArrayList<GLTask> _gl_tasks;

	/** random number generator */
	private Random _rng;

	/** event instances (so we do not realloc them every frames */
	private EventPreRender _pre_render_event;
	private EventPostRender _post_render_event;

	/** main fbo */
	private GLFrameBuffer _fbo;
	private GLTexture _fbo_texture;
	private GLRenderBuffer _fbo_depth_buffer;

	/** values */
	private int _vertices_drawn;
	private int _draw_calls;

	/** default and simple vao (to use geometry shaders) */
	private GLVertexArray _default_vao;
	private GLVertexBuffer _default_vbo;

	private boolean _toggled = true;

	public MainRenderer(VoxelEngineClient engine) {
		this._engine = engine;
	}

	/** called after resources where loaded */
	public void initialize() {

		GLH.glhCheckError("Pre mainrenderer initialization");
		Logger.get().log(Level.FINE, "Initializing " + this.getClass().getSimpleName());

		this._gl_tasks = new ArrayList<GLTask>();
		this._rng = new Random();
		this._pre_render_event = new EventPreRender(this);
		this._post_render_event = new EventPostRender(this);

		this.initialiseDefaultVAO();
		this.initializeMainFBO(this.getGLFWWindow().getWidth(), this.getGLFWWindow().getHeight());

		this._world_renderer = new WorldRenderer(this);
		this._gui_renderer = new GuiRenderer(this);

		this.getGLFWWindow().addResizeListener(this);

		Logger.get().log(Level.FINE, "Initializing " + this.getClass().getSimpleName());

		Logger.get().log(Level.FINE, "Initializing " + this._gui_renderer.getClass().getSimpleName());
		GLH.glhCheckError("pre guirenderer starts");
		this._gui_renderer.initialize();
		// this._gui_renderer.addView(new GuiViewDefault());
		GLH.glhCheckError("post guirenderer starts");

		this._final_process_program = new GLProgramPostProcessing(
				R.getResPath("shaders/post_process/post_processing.fs"));
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

		this._fbo = GLH.glhGenFBO();
		this._fbo.bind();
		this._fbo.createDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		this._fbo_texture = GLH.glhGenTexture();
		this._fbo_texture.bind(GL11.GL_TEXTURE_2D);
		this._fbo_texture.image2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		this._fbo_texture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		this._fbo_texture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		this._fbo.createTextureAttachment(this._fbo_texture, GL30.GL_COLOR_ATTACHMENT0);

		this._fbo_depth_buffer = this._fbo.createRenderBuffer(width, height, GL30.GL_DEPTH_ATTACHMENT);

		this._fbo.unbind();
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

		this._gui_renderer.deinitialize();
		this._world_renderer.deinitialize();

		GLH.glhDeleteObject(this._default_vao);
		this._default_vao = null;

		GLH.glhDeleteObject(this._default_vbo);
		this._default_vbo = null;
	}

	private void deleteMainVBO() {
		GLH.glhDeleteObject(this._fbo);
		GLH.glhDeleteObject(this._fbo_texture);
		GLH.glhDeleteObject(this._fbo_depth_buffer);
	}

	/**
	 * main rendering function (screen is already cleared, and frame buffer will
	 * be swapped after this render
	 */
	public void render() {

		// run tasks
		while (!this._gl_tasks.isEmpty()) {
			GLTask task = this._gl_tasks.remove(0);
			task.run();
		}

		// if renderer is not enabled, return
		if (!this._toggled) {
			return;
		}

		// TODO move this somewhere else, if openal is thread safe
		if (this.getCamera() != null) {
			this.getCamera().update();
			this._engine.getResourceManager().getSoundManager().update(this.getCamera());
		}

		this.getResourceManager().getEventManager().invokeEvent(this._pre_render_event);

		// reset these values before next rendering
		this._draw_calls = GLH.glhGetContext().resetDrawCalls();
		this._vertices_drawn = GLH.glhGetContext().resetDrawVertices();

		// if there is a world
		if (this.getWorldRenderer().getWorld() != null && this.getCamera() != null) {
			// render it
			this._world_renderer.preRender();

			// refresh fbo
			this.getFBO().bind();
			this.getFBO().clear();
			this.getFBO().viewport(0, 0, this.getGLFWWindow().getWidth(), this.getGLFWWindow().getHeight());
			this._world_renderer.render();
			this.renderPostProcessingEffects(); // post processing effects
			this.getFBO().unbind(); // render the world to the main fbo

			this._world_renderer.postRender();

			this.renderFinalImage(); // final image to default fbo
		}

		// render guis to default fbo (post processing effects doesnt affect
		// guis)
		this._gui_renderer.preRender();
		this._gui_renderer.render();
		this._gui_renderer.postRender();

		this.getResourceManager().getEventManager().invokeEvent(this._post_render_event);
	}

	private void renderFinalImage() {

		// bind the fbo texture to texture attachment 0
		this.getFBOTexture().bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);

		this._final_process_program.useStart();
		this._final_process_program.loadUniforms(this);
		this._default_vao.bind();
		GLH.glhDrawArrays(GL11.GL_POINTS, 0, 1);
		this._final_process_program.useStop();
	}

	private void renderPostProcessingEffects() {

		if (this._post_processing_program != null) {

			// bind the fbo texture to texture attachment 0
			this.getFBOTexture().bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);

			this._post_processing_program.useStart();
			this._post_processing_program.loadUniforms(this);
			this._default_vao.bind();
			GLH.glhDrawArrays(GL11.GL_POINTS, 0, 1);
			this._post_processing_program.useStop();
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
		return (this._draw_calls);
	}

	/** return the vertices drawn for the last frame */
	public int getVerticesDrawn() {
		return (this._vertices_drawn);
	}

	/** get the resource manager */
	public ResourceManagerClient getResourceManager() {
		return (this._engine.getResourceManager());
	}

	/** get the camera */
	public CameraProjectiveWorld getCamera() {
		return (this._camera);
	}

	public void setCamera(CameraProjectiveWorld camera) {
		Logger.get().log(Level.FINE, "Setting MainRenderer camera: " + camera);
		this._camera = camera;
	}

	/** font renderer */
	public GuiRenderer getGuiRenderer() {
		return (this._gui_renderer);
	}

	/** get the world renderer */
	public WorldRenderer getWorldRenderer() {
		return (this._world_renderer);
	}

	/** OpenGL tasks to be realized in main thread */
	public interface GLTask {
		public void run();
	}

	/** get current window on which the main renderer is rendering */
	public GLFWWindow getGLFWWindow() {
		return (this._engine.getGLFWWindow());
	}

	public VoxelEngineClient getEngine() {
		return (this._engine);
	}

	public void setWorld(World world) {
		this._world_renderer.setWorld(world);
		if (this._camera != null && this._camera instanceof CameraPerspectiveWorld) {
			((CameraPerspectiveWorld) this._camera).setWorld(world);
		}
	}

	public void setPostProcessingProgram(GLProgramPostProcessing program) {
		this._post_processing_program = program;
	}

	public GLFrameBuffer getFBO() {
		return (this._fbo);
	}

	public GLTexture getFBOTexture() {
		return (this._fbo_texture);
	}

	public Random getRNG() {
		return (this._rng);
	}

	public void addGLTask(GLTask task) {
		this._gl_tasks.add(task);
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {

		this._world_renderer.getTasks(engine, tasks);
		this._gui_renderer.getTasks(engine, tasks);
		// TODO : fix me
		// final Camera camera = this.getCamera();
		//
		// if (camera != null) {
		//
		// Callable<Taskable> call = new Callable<Taskable>() {
		// @Override
		// public Taskable call() throws Exception {
		// camera.update();
		// return (MainRenderer.this);
		// }
		// };
		// tasks.add(call);
		//
		// }
	}

	/** set to true of false weather you want to render or not */
	public void toggle(boolean b) {
		this._toggled = b;
	}

}
