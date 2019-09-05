package com.grillecube.client.event.renderer;

import com.grillecube.client.renderer.world.WorldRenderer;

public class EventPostWorldRender extends EventWorldRender {
	public EventPostWorldRender(@SuppressWarnings("rawtypes") WorldRenderer worldRenderer) {
		super(worldRenderer);
	}

	@Override
	public String getName() {
		return ("Post World Render");
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
