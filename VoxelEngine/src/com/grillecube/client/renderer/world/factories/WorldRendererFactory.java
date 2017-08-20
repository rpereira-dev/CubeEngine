package com.grillecube.client.renderer.world.factories;

import java.util.Random;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.world.World;

public abstract class WorldRendererFactory {

	/** the world renderer bound to this factory */
	public final WorldRenderer worldRenderer;

	public WorldRendererFactory(WorldRenderer worldRenderer) {
		this.worldRenderer = worldRenderer;
	}

	/** update the factory to generate rendering lists */
	public abstract void update();

	/** render */
	public abstract void render();

	public final World getWorld() {
		return (this.worldRenderer.getWorld());
	}

	public final CameraProjective getCamera() {
		return (this.worldRenderer.getCamera());
	}

	public ResourceManagerClient getResourceManager() {
		return (this.getMainRenderer().getResourceManager());
	}

	public final MainRenderer getMainRenderer() {
		return (this.worldRenderer.getMainRenderer());
	}

	public void deinitialize() {
	}

	public void initialize() {

	}

	public Random getRNG() {
		return (this.getWorld().getRNG());
	}
}
