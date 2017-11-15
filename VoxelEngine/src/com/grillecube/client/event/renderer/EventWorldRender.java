package com.grillecube.client.event.renderer;

import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.world.World;

public abstract class EventWorldRender extends EventRender {

	private final WorldRenderer<World> worldRenderer;

	public EventWorldRender(WorldRenderer<World> worldRenderer) {
		super(worldRenderer.getMainRenderer());
		this.worldRenderer = worldRenderer;
	}

	public final WorldRenderer<World> getWorldRenderer() {
		return (this.worldRenderer);
	}

}
