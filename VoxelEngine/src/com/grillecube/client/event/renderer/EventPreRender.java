package com.grillecube.client.event.renderer;

import com.grillecube.client.renderer.MainRenderer;

public class EventPreRender extends EventRender {
	public EventPreRender(MainRenderer renderer) {
		super(renderer);
	}

	@Override
	public String getName() {
		return ("Pre Render");
	}
}
