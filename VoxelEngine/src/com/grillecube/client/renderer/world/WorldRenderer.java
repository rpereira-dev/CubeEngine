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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.event.renderer.model.EventModelInstanceAdded;
import com.grillecube.client.event.renderer.model.EventModelInstanceRemoved;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLFrameBuffer;
import com.grillecube.client.opengl.object.GLProgramPostProcessing;
import com.grillecube.client.opengl.object.GLRenderBuffer;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.MainRenderer.GLTask;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.factories.LineRendererFactory;
import com.grillecube.client.renderer.factories.ModelRendererFactory;
import com.grillecube.client.renderer.factories.ParticleRendererFactory;
import com.grillecube.client.renderer.factories.SkyRendererFactory;
import com.grillecube.client.renderer.factories.TerrainRendererFactory;
import com.grillecube.client.renderer.gui.components.Gui;
import com.grillecube.client.renderer.gui.event.GuiEventAspectRatio;
import com.grillecube.client.renderer.gui.event.GuiListener;
import com.grillecube.common.Logger;
import com.grillecube.common.event.EventListener;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.world.World;

public class WorldRenderer extends RendererFactorized {

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

	/** the gui to match (so it optimizes the viewport) */
	private int width;
	private int height;

	private GuiListener<GuiEventAspectRatio<Gui>> aspectRatioListener;

	private LineRendererFactory lineFactory;
	private SkyRendererFactory skyFactory;
	private TerrainRendererFactory terrainFactory;
	private ModelRendererFactory modelFactory;
	private ParticleRendererFactory particleFactory;

	private EventListener<EventModelInstanceAdded> modelInstanceAddCallback;
	private EventListener<EventModelInstanceRemoved> modelInstanceRemoveCallback;

	public WorldRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);
		this.aspectRatioListener = new GuiListener<GuiEventAspectRatio<Gui>>() {
			@Override
			public void invoke(GuiEventAspectRatio<Gui> event) {
				VoxelEngineClient.instance().addGLTask(new GLTask() {
					@Override
					public void run() {
						resizeFbo();
					}
				});
			}
		};
	}

	@Override
	protected void onInitialized() {
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

		this.resizeFbo();

		GLH.glhCheckError("post worldrenderer fbo creation");

		this.setPostProcessingProgram(null);
		this.lineFactory = new LineRendererFactory(this.getMainRenderer());
		this.skyFactory = new SkyRendererFactory(this.getMainRenderer());
		this.terrainFactory = new TerrainRendererFactory(this.getMainRenderer());
		this.modelFactory = new ModelRendererFactory(this.getMainRenderer());
		this.particleFactory = new ParticleRendererFactory(this.getMainRenderer());

		super.addFactory(this.lineFactory);
		super.addFactory(this.skyFactory);
		super.addFactory(this.terrainFactory);
		super.addFactory(this.modelFactory);
		super.addFactory(this.particleFactory);

		// callback to add every model instances to the renderer, if they are in
		// the correct world
		EventManager eventManager = this.getMainRenderer().getResourceManager().getEventManager();
		this.modelInstanceAddCallback = new EventListener<EventModelInstanceAdded>() {
			@Override
			public void invoke(EventModelInstanceAdded event) {
				if (event.getModelInstance().getEntity().getWorld() == world) {
					modelFactory.addModelInstance(event.getModelInstance());
				}
			}
		};
		this.modelInstanceRemoveCallback = new EventListener<EventModelInstanceRemoved>() {
			@Override
			public void invoke(EventModelInstanceRemoved event) {
				if (event.getModelInstance().getEntity().getWorld() == world) {
					modelFactory.removeModelInstance(event.getModelInstance());
				}
			}
		};

		eventManager.addListener(this.modelInstanceAddCallback);
		eventManager.addListener(this.modelInstanceRemoveCallback);
	}

	@Override
	protected void onDeinitialized() {

		Logger.get().log(Logger.Level.DEBUG, "Deinitializing " + this.getClass().getSimpleName());

		// remove fbo
		GLH.glhDeleteObject(this.fbo);
		GLH.glhDeleteObject(this.fboTexture);
		GLH.glhDeleteObject(this.fboDepthBuffer);
		this.fbo = null;
		this.fboTexture = null;
		this.fboDepthBuffer = null;

		EventManager eventManager = this.getMainRenderer().getResourceManager().getEventManager();
		eventManager.removeListener(this.modelInstanceAddCallback);
		eventManager.removeListener(this.modelInstanceRemoveCallback);
	}

	public final void resizeFbo() {
		int W = this.getMainRenderer().getGLFWWindow().getWidth();
		int H = this.getMainRenderer().getGLFWWindow().getHeight();
		this.width = W;
		this.height = H;

		// TODO, negative values lol
		// if (this.guiToMatch != null) {
		// Vector4f w = new Vector4f(0, 1.0f, 0.0f, 1.0f);
		// Vector4f h = new Vector4f(0, 1.0f, 0.0f, 1.0f);
		// Matrix4f.transform(this.guiToMatch.getGuiToWindowChangeOfBasis(), w,
		// w);
		// Matrix4f.transform(this.guiToMatch.getGuiToWindowChangeOfBasis(), h,
		// h);
		// this.width = (int) ((w.y - w.x) * W);
		// this.height = (int) ((h.y - h.x) * H);
		// }

		this.fboTexture.bind(GL11.GL_TEXTURE_2D);
		this.fboTexture.image2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.width, this.height, 0, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		this.fboDepthBuffer.bind();
		this.fboDepthBuffer.storage(GL11.GL_DEPTH_COMPONENT, this.width, this.height);
	}

	/** render the given world */
	@Override
	public void render() {
		// System.out.println(this.world.getEntityStorage().getEntities().size());

		// if there is a world
		if (this.getWorld() != null && this.getCamera() != null) {
			// refresh fbo
			this.getFBO().bind();
			this.getFBO().clear();

			// TODO change viewport here to have it modular
			this.getFBO().viewport(0, 0, this.width, this.height);

			super.render();

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
	public void onWindowResize(GLFWWindow window) {
		this.resizeFbo();
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
		this.terrainFactory.setWorld(world);
		this.skyFactory.setSky(world.getSky());
		this.modelFactory.clear();
		this.modelFactory.loadWorldModelInstance(world);
	}

	public final void setWorld(int worldID) {
		this.setWorld(this.getMainRenderer().getResourceManager().getWorldManager().getWorld(worldID));
	}

	public final void setCamera(CameraProjective camera) {
		this.camera = camera;
		this.modelFactory.setCamera(camera);
		this.terrainFactory.setCamera(camera);
		this.skyFactory.setCamera(camera);
		this.lineFactory.setCamera(camera);
		this.particleFactory.setCamera(camera);
	}

	/** the world to be rendered */
	public final World getWorld() {
		return (this.world);
	}

	/** the camera use to render this world */
	public final CameraProjective getCamera() {
		return (this.camera);
	}

	public final LineRendererFactory getLineRendererFactory() {
		return (this.lineFactory);
	}

	public final SkyRendererFactory getSkyRendererFactory() {
		return (this.skyFactory);
	}

	public final TerrainRendererFactory getTerrainRendererFactory() {
		return (this.terrainFactory);
	}

	public final ModelRendererFactory getModelRendererFactory() {
		return (this.modelFactory);
	}

	public final ParticleRendererFactory getParticleRendererFactory() {
		return (this.particleFactory);
	}
}
