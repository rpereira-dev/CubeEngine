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

	@Override
	protected void process() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unprocess() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onReset() {
		// TODO Auto-generated method stub

	}
}
