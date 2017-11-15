package com.grillecube.client.event.renderer;

import com.grillecube.client.renderer.world.WorldRenderer;

public class EventPostWorldRender extends EventWorldRender {
	public EventPostWorldRender(WorldRenderer renderer) {
		super(renderer);
	}

	@Override
	public String getName() {
		return ("Post World Render");
	}
}
