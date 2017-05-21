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

package com.grillecube.client.renderer.world;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLFrameBuffer;
import com.grillecube.client.opengl.object.GLRenderBuffer;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.model.ModelRenderer;
import com.grillecube.client.renderer.world.lines.LineRenderer;
import com.grillecube.client.renderer.world.particles.ParticleRenderer;
import com.grillecube.client.renderer.world.sky.SkyRenderer;
import com.grillecube.client.renderer.world.terrain.TerrainRenderer;
import com.grillecube.client.sound.ALH;
import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.world.World;

public class WorldRenderer extends Renderer {

	public static final int REFLECTION_FBO_WIDTH = 640;
	public static final int REFLECTION_FBO_HEIGHT = (int) (REFLECTION_FBO_WIDTH / 1.6f);

	public static final int REFRACTION_FBO_WIDTH = 500;
	public static final int REFRACTION_FBO_HEIGHT = (int) (REFRACTION_FBO_WIDTH / 1.6f);

	public static final int SHADOW_FBO_WIDTH = 2048; // the bigger the better
	public static final int SHADOW_FBO_HEIGHT = SHADOW_FBO_WIDTH;

	// reflection fbo
	private GLFrameBuffer _reflection_fbo;
	private GLTexture _reflection_texture;
	private GLRenderBuffer _reflection_depth_buffer;

	// refraction fbo
	private GLFrameBuffer _refraction_fbo;
	private GLTexture _refraction_texture;
	private GLRenderBuffer _refraction_depth_buffer;

	/** a clipping plane that does not clip anything */
	public static final Vector4f NO_CLIPPING = new Vector4f(0, 0, 0, 0);

	/** sky renderer */
	private SkyRenderer skyRenderer;

	/** line renderer */
	private LineRenderer lineRenderer;

	/** main terrain renderer */
	private TerrainRenderer terrainRenderer;

	/** model view projection renderer */
	private MVPRenderer mvpRenderer;

	/** model renderer */
	private ModelRenderer modelRenderer;

	/** particles renderer */
	private ParticleRenderer particleRenderer;

	/** world to render */
	private World _world;

	/** renderers */
	private ArrayList<RendererWorld> renderers;

	public WorldRenderer(MainRenderer main_renderer) {
		super(main_renderer);
	}

	@Override
	public void initialize() {

		Logger.get().log(Logger.Level.DEBUG, "Initializing " + this.getClass().getSimpleName());

		this.renderers = new ArrayList<RendererWorld>();

		this.skyRenderer = new SkyRenderer(this.getParent());
		this.lineRenderer = new LineRenderer(this.getParent());
		this.terrainRenderer = new TerrainRenderer(this.getParent());
		this.modelRenderer = new ModelRenderer(this.getParent());
		this.mvpRenderer = new MVPRenderer(this.getParent());
		this.particleRenderer = new ParticleRenderer(this.getParent());

		this.renderers.add(this.skyRenderer);
		this.renderers.add(this.lineRenderer);
		this.renderers.add(this.terrainRenderer); // TODO : fix artifacts
		this.renderers.add(this.modelRenderer);
		this.renderers.add(this.mvpRenderer);
		this.renderers.add(this.particleRenderer);

		this.createReflectionFBO();
		this.createRefractionFBO();

		for (RendererWorld renderer : this.renderers) {
			Logger.get().log(Logger.Level.DEBUG, "Initializing " + renderer.getClass().getSimpleName());
			renderer.initialize();
		}
		// this.renderers.clear();
		// this.renderers.add(this.terrainRenderer);
		// this.renderers.add(this.particleRenderer);
	}

	@Override
	public void deinitialize() {

		Logger.get().log(Logger.Level.DEBUG, "Deinitializing " + this.getClass().getSimpleName());

		for (RendererWorld renderer : this.renderers) {
			Logger.get().log(Logger.Level.DEBUG, "Deinitializing " + renderer.getClass().getSimpleName());
			renderer.deinitialize();
		}
	}

	private void onWorldSet(World world) {
		for (RendererWorld renderer : this.renderers) {
			renderer.onWorldSet(world);
		}

	}

	private void onWorldUnset(World world) {
		for (RendererWorld renderer : this.renderers) {
			renderer.onWorldUnset(world);
		}
	}

	@Override
	public void preRender() {

		// pre render every renderer
		for (RendererWorld renderer : this.renderers) {
			renderer.preRender();
			GLH.glhCheckError("post " + renderer.getClass().getSimpleName() + ".preRender()");
		}

		if (GLH.glhGetContext().getWindow().isKeyPressed(GLFW.GLFW_KEY_C)) {
			this.getParent().getResourceManager().getSoundManager().playSoundAt(
					ALH.alhLoadSound("C:/Users/Romain/AppData/Roaming/VoxelEngine/assets/POT/sounds/acoustic1.wav"),
					this.getCamera().getPosition(), new Vector3f(0, 0, 0));
		}
	}

	@Override
	public void postRender() {
		for (RendererWorld renderer : this.renderers) {
			renderer.postRender();
			GLH.glhCheckError("post " + renderer.getClass().getSimpleName() + ".postRender()");
		}
	}

	/** render the given world */
	@Override
	public void render() {

		// README : commented stuff are used to debug renderer. To see different
		// performances

		GLH.glhCheckError("pre world renderer");

		// long total = 0;
		// long times[] = new long[this.renderers.size()];
		//
		// final world renderer
		for (int i = 0; i < this.renderers.size(); i++) {

			RendererWorld renderer = this.renderers.get(i);
			//
			// long time = System.nanoTime();
			renderer.render();
			// long difft = System.nanoTime() - time;
			// times[i] = difft;
			// total += difft;
			//
			GLH.glhCheckError("post " + renderer.getClass().getSimpleName() + ".render()");
		}

		// float ftotal = 0;
		// for (int i = 0 ; i < this.renderers.size() ; i++) {
		//
		// RendererWorld renderer = this.renderers.get(i);
		// float ftime = times[i] / (float)total;
		// ftotal += ftime;
		// Logger.get().log(Logger.Level.DEBUG,
		// renderer.getClass().getSimpleName(), ftime);
		// }

		// Logger.get().log(Logger.Level.DEBUG, "/" + ftotal);
		// Logger.get().log(Logger.Level.DEBUG, " ");

	}

	public void setWorld(World world) {

		World prevworld = this._world;
		this._world = world;

		// if we are setting a world and none was set before
		if (prevworld == null && world != null) {
			// initialize renderers
			this.initialize();
			this.onWorldSet(world);
			// else if we are changing the world
		} else if (prevworld != null && world != null) {

			this.onWorldUnset(prevworld);
			this.onWorldSet(world);

			// else if we are setting a null world but one was set before
		} else if (prevworld != null && world == null) {
			this.onWorldUnset(prevworld);
			this.deinitialize();
		}
	}

	public World getWorld() {
		return (this._world);
	}

	/** return the default particle renderer */
	public ParticleRenderer getParticleRenderer() {
		return (this.particleRenderer);
	}

	public MVPRenderer getMVPRenderer() {
		return (this.mvpRenderer);
	}

	public CameraProjectiveWorld getCamera() {
		return (this.getParent().getCamera());
	}

	public TerrainRenderer getTerrainRenderer() {
		return (this.terrainRenderer);
	}

	public LineRenderer getLineRenderer() {
		return (this.lineRenderer);
	}

	private void createReflectionFBO() {

		this._reflection_fbo = GLH.glhGenFBO();
		this._reflection_fbo.bind();
		this._reflection_fbo.createDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		this._reflection_texture = GLH.glhGenTexture();
		this._reflection_texture.bind(GL11.GL_TEXTURE_2D);
		this._reflection_texture.bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		this._reflection_texture.image2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, REFLECTION_FBO_WIDTH,
				REFLECTION_FBO_HEIGHT, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		this._reflection_texture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		this._reflection_texture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		this._reflection_fbo.createTextureAttachment(this._reflection_texture, GL30.GL_COLOR_ATTACHMENT0);

		this._reflection_depth_buffer = this._reflection_fbo.createRenderBuffer(REFLECTION_FBO_WIDTH,
				REFLECTION_FBO_HEIGHT, GL30.GL_DEPTH_ATTACHMENT);

		this._reflection_fbo.unbind();
	}

	private void deleteReflectionFBO() {
		GLH.glhDeleteObject(this._reflection_fbo);
		GLH.glhDeleteObject(this._reflection_texture);
		GLH.glhDeleteObject(this._reflection_depth_buffer);
	}

	private void createRefractionFBO() {

		this._refraction_fbo = GLH.glhGenFBO();
		this._refraction_fbo.bind();
		this._refraction_fbo.createDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		this._refraction_texture = GLH.glhGenTexture();
		this._refraction_texture.bind(GL11.GL_TEXTURE_2D);
		this._refraction_texture.bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
		this._refraction_texture.image2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, REFRACTION_FBO_WIDTH,
				REFRACTION_FBO_HEIGHT, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		this._refraction_texture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		this._refraction_texture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		this._refraction_fbo.createTextureAttachment(this._refraction_texture, GL30.GL_COLOR_ATTACHMENT0);

		this._refraction_depth_buffer = this._reflection_fbo.createRenderBuffer(REFRACTION_FBO_WIDTH,
				REFRACTION_FBO_HEIGHT, GL30.GL_DEPTH_ATTACHMENT);

		this._refraction_fbo.unbind();
	}

	public Collection<RendererWorld> getRenderers() {
		return (this.renderers);
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {

		World world = this.getWorld();
		CameraProjectiveWorld camera = this.getCamera();

		if (world == null || camera == null) {
			return;
		}

		for (RendererWorld renderer : this.getRenderers()) {
			renderer.getTasks(engine, tasks, world, camera);
		}
	}

	/** request the world renderer to update reflection/refraciton textures */
	public void requestReflectionRefractionUpdate() {
		// TODO
	}
}
