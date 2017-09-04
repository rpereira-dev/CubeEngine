package com.grillecube.client.renderer.factories;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.resources.ResourceManagerClient;

public abstract class RendererFactory {

	private final MainRenderer mainRenderer;

	public RendererFactory(MainRenderer mainRenderer) {
		this.mainRenderer = mainRenderer;
	}

	public final MainRenderer getMainRenderer() {
		return (this.mainRenderer);
	}

	public final ResourceManagerClient getResourceManager() {
		return (this.getMainRenderer().getResourceManager());
	}

	/** update the factory to generate rendering lists */
	public abstract void update();

	/** render */
	public abstract void render();

	public void deinitialize() {
	}

	public void initialize() {

	}
}
