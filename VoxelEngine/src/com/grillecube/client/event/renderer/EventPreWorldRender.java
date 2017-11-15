package com.grillecube.client.event.renderer;

import com.grillecube.client.renderer.world.WorldRenderer;

public class EventPreWorldRender extends EventWorldRender {
	public EventPreWorldRender(WorldRenderer renderer) {
		super(renderer);
	}

	@Override
	public String getName() {
		return ("Pre World Render");
	}
}
