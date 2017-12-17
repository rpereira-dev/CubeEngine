package com.grillecube.client.event.renderer;

import com.grillecube.client.renderer.world.WorldRenderer;

@SuppressWarnings("rawtypes")
public abstract class EventWorldRender extends EventRender {

	private final WorldRenderer worldRenderer;

	public EventWorldRender(WorldRenderer worldRenderer) {
		super(worldRenderer.getMainRenderer());
		this.worldRenderer = worldRenderer;
	}

	public final WorldRenderer getWorldRenderer() {
		return (this.worldRenderer);
	}

}
