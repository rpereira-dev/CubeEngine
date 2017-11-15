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
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.grillecube.client.event.renderer.EventPostWorldRender;
import com.grillecube.client.event.renderer.EventPreWorldRender;
import com.grillecube.client.event.renderer.model.EventModelInstanceAdded;
import com.grillecube.client.event.renderer.model.EventModelInstanceRemoved;
import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLFrameBuffer;
import com.grillecube.client.opengl.object.GLProgramPostProcessing;
import com.grillecube.client.opengl.object.GLRenderBuffer;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.lines.LineRendererFactory;
import com.grillecube.client.renderer.model.ModelRendererFactory;
import com.grillecube.client.renderer.particles.ParticleRendererFactory;
import com.grillecube.common.Logger;
import com.grillecube.common.event.EventListener;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;

public abstract class WorldRenderer<T extends World> extends RendererFactorized {

	/** world to render */
	private T world;

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

	private final LineRendererFactory lineFactory;
	private final ModelRendererFactory modelFactory;
	private final ParticleRendererFactory particleFactory;

	private EventListener<EventModelInstanceAdded> modelInstanceAddCallback;
	private EventListener<EventModelInstanceRemoved> modelInstanceRemoveCallback;

	private final EventPreWorldRender preRenderEvent;

	private final EventPostWorldRender postRenderEvent;

	public WorldRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);

		this.lineFactory = new LineRendererFactory(mainRenderer);
		this.modelFactory = new ModelRendererFactory(mainRenderer);
		this.particleFactory = new ParticleRendererFactory(mainRenderer);

		super.addFactory(this.lineFactory);
		super.addFactory(this.modelFactory);
		super.addFactory(this.particleFactory);

		this.preRenderEvent = new EventPreWorldRender(this);
		this.postRenderEvent = new EventPostWorldRender(this);
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

	// TODO : take size as parameter
	public final void resizeFbo() {
		int W = this.getMainRenderer().getGLFWWindow().getWidth();
		int H = this.getMainRenderer().getGLFWWindow().getHeight();
		this.width = W;
		this.height = H;

		this.fboTexture.bind(GL11.GL_TEXTURE_2D);
		this.fboTexture.image2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.width, this.height, 0, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		this.fboDepthBuffer.bind();
		this.fboDepthBuffer.storage(GL11.GL_DEPTH_COMPONENT, this.width, this.height);
	}

	HashMap<Entity, Integer> BB = new HashMap<Entity, Integer>();

	// TODO : remove this
	private final void renderEntitiesAABB() {
		for (Entity e : this.world.getEntityStorage().getEntities()) {
			if (!BB.containsKey(e)) {
				BB.put(e, this.lineFactory.addBox(e, e));
			} else {
				this.lineFactory.setBox(e, e, BB.get(e));
			}
		}
	}

	/** render the given world */
	@Override
	public void render() {

		this.getMainRenderer().getResourceManager().getEventManager().invokeEvent(this.preRenderEvent);

		this.renderEntitiesAABB();

		// if there is a world
		if (this.getWorld() != null && this.getCamera() != null) {
			// refresh fbo
			this.getFBO().bind();
			this.getFBO().clear();

			// change viewport here to have it modular
			this.getFBO().viewport(0, 0, this.width, this.height);

			super.render();

			// post processing effects
			this.renderPostProcessingEffects();

			// unbind the fbo
			this.getFBO().unbind();
		}

		this.getMainRenderer().getResourceManager().getEventManager().invokeEvent(this.postRenderEvent);

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

	public final void setWorld(T world) {
		this.world = world;
		this.modelFactory.clear();
		this.modelFactory.loadWorldModelInstance(world);
		this.onWorldSet();
	}

	protected void onWorldSet() {

	}

	@SuppressWarnings("unchecked")
	public final void setWorld(int worldID) {
		this.setWorld((T) this.getMainRenderer().getResourceManager().getWorldManager().getWorld(worldID));
	}

	public final void setCamera(CameraProjective camera) {
		this.camera = camera;
		this.modelFactory.setCamera(camera);
		this.lineFactory.setCamera(camera);
		this.particleFactory.setCamera(camera);
		this.onCameraSet();
	}

	protected void onCameraSet() {
	}

	/** the world to be rendered */
	public final T getWorld() {
		return (this.world);
	}

	/** the camera use to render this world */
	public final CameraProjective getCamera() {
		return (this.camera);
	}

	public final LineRendererFactory getLineRendererFactory() {
		return (this.lineFactory);
	}

	public final ModelRendererFactory getModelRendererFactory() {
		return (this.modelFactory);
	}

	public final ParticleRendererFactory getParticleRendererFactory() {
		return (this.particleFactory);
	}
}
