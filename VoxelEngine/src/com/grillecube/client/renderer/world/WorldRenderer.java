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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLFrameBuffer;
import com.grillecube.client.opengl.object.GLProgramPostProcessing;
import com.grillecube.client.opengl.object.GLRenderBuffer;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.world.factories.LineRendererFactory;
import com.grillecube.client.renderer.world.factories.ModelRendererFactory;
import com.grillecube.client.renderer.world.factories.ParticleRendererFactory;
import com.grillecube.client.renderer.world.factories.SkyRendererFactory;
import com.grillecube.client.renderer.world.factories.TerrainRendererFactory;
import com.grillecube.client.renderer.world.factories.WorldRendererFactory;
import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.world.World;

public class WorldRenderer extends Renderer {

	/** world to render */
	private World world;

	/** camera used to render the world */
	private CameraProjective camera;

	/** post processing shaders programs */
	private GLProgramPostProcessing postProcessingProgram;

	/** fbo */
	private GLFrameBuffer fbo;
	private GLTexture fboTexture;
	private GLRenderBuffer fboDepthBuffer;

	/** factories array list */
	private ArrayList<WorldRendererFactory> factories;

	private int width;

	private int height;

	public WorldRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);
	}

	public WorldRenderer(MainRenderer mainRenderer, World world, CameraProjective camera) {
		this(mainRenderer);
		this.setWorld(world);
		this.setCamera(camera);
	}

	@Override
	public void initialize() {
		Logger.get().log(Logger.Level.DEBUG, "Initializing " + this.getClass().getSimpleName());

		GLH.glhCheckError("pre worldrenderer fbo creation");
		this.fbo = GLH.glhGenFBO();
		this.fbo.bind();
		this.fbo.createDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		this.fboTexture = GLH.glhGenTexture();
		this.fboTexture.bind(GL11.GL_TEXTURE_2D);
		this.fboTexture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		this.fboTexture.parameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		this.fbo.createTextureAttachment(this.fboTexture, GL30.GL_COLOR_ATTACHMENT0);

		this.fboDepthBuffer = GLH.glhGenRBO();
		this.fboDepthBuffer.bind();
		this.fboDepthBuffer.attachToFBO(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT);

		this.fbo.unbind();

		GLH.glhCheckError("post worldrenderer fbo creation");

		this.setPostProcessingProgram(null);
		this.factories = new ArrayList<WorldRendererFactory>();
		this.factories.add(new LineRendererFactory(this));
		this.factories.add(new SkyRendererFactory(this));
		this.factories.add(new TerrainRendererFactory(this));
		this.factories.add(new ModelRendererFactory(this));
		this.factories.add(new ParticleRendererFactory(this));

		for (WorldRendererFactory factory : this.factories) {
			factory.initialize();
		}

		// this.setPostProcessingProgram(new
		// GLProgramPostProcessing(R.getResPath("shaders/post_process/blurv.fs")));
		// this.setPostProcessingProgram(new
		// GLProgramPostProcessing(R.getResPath("shaders/post_process/blurh.fs")));
		// this.setPostProcessingProgram(new
		// GLProgramPostProcessing(R.getResPath("shaders/post_process/drunk.fs")));
	}

	@Override
	public void deinitialize() {

		Logger.get().log(Logger.Level.DEBUG, "Deinitializing " + this.getClass().getSimpleName());

		// remove fbo
		GLH.glhDeleteObject(this.fbo);
		GLH.glhDeleteObject(this.fboTexture);
		GLH.glhDeleteObject(this.fboDepthBuffer);
		this.fbo = null;
		this.fboTexture = null;
		this.fboDepthBuffer = null;

		for (WorldRendererFactory factory : this.factories) {
			factory.deinitialize();
		}
		this.factories = null;
	}

	public final void resizeFbo(int width, int height) {
		this.fboTexture.bind(GL11.GL_TEXTURE_2D);
		this.fboTexture.image2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

		this.fboDepthBuffer.bind();
		this.fboDepthBuffer.storage(GL11.GL_DEPTH_COMPONENT, width, height);

		this.width = width;
		this.height = height;
	}

	/** render the given world */
	@Override
	public void render() {

		// if there is a world
		if (this.getWorld() != null && this.getCamera() != null) {
			// refresh fbo
			this.getFBO().bind();
			this.getFBO().clear();

			// TODO change viewport here to have it modular
			this.getFBO().viewport(0, 0, this.width, this.height);

			// DO THE RENDER HERE
			for (WorldRendererFactory factory : this.factories) {
				factory.render();
			}

			// post processing effects
			this.renderPostProcessingEffects();

			// unbind the fbo
			this.getFBO().unbind();
		}
	}

	private void renderPostProcessingEffects() {

		if (this.postProcessingProgram != null) {
			// bind the fbo texture to texture attachment 0
			this.getFBOTexture().bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);

			this.postProcessingProgram.useStart();
			this.postProcessingProgram.loadUniforms(super.getTimer());
			this.getMainRenderer().getDefaultVAO().bind();
			GLH.glhDrawArrays(GL11.GL_POINTS, 0, 1);
			this.postProcessingProgram.useStop();
		}
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<VoxelEngine.Callable<Taskable>> tasks) {
		for (WorldRendererFactory factory : this.factories) {
			tasks.add(engine.new Callable<Taskable>() {
				@Override
				public Taskable call() throws Exception {
					factory.update();
					return (WorldRenderer.this);
				}

				@Override
				public String getName() {
					return (factory.getClass().getSimpleName() + " update");
				}
			});
		}
	}

	@Override
	public void onWindowResize(GLFWWindow window, int width, int height) {
		this.resizeFbo(width, height);
	}

	public final void setPostProcessingProgram(GLProgramPostProcessing program) {
		this.postProcessingProgram = program;
	}

	public final GLFrameBuffer getFBO() {
		return (this.fbo);
	}

	public final GLTexture getFBOTexture() {
		return (this.fboTexture);
	}

	public final void setWorld(World world) {
		this.world = world;
	}

	public final void setWorld(int worldID) {
		this.setWorld(this.getMainRenderer().getResourceManager().getWorldManager().getWorld(worldID));
	}

	public final void setCamera(CameraProjective camera) {
		this.camera = camera;
	}

	/** the world to be rendered */
	public final World getWorld() {
		return (this.world);
	}

	/** the camera use to render this world */
	public final CameraProjective getCamera() {
		return (this.camera);
	}

	// TODO : get factories
}
