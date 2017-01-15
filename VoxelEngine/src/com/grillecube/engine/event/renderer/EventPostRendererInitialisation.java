package com.grillecube.engine.event.renderer;

import com.grillecube.engine.renderer.MainRenderer;

/** an event which is called after the MainRenderer being initialized */
public class EventPostRendererInitialisation extends EventRender {

	public EventPostRendererInitialisation(MainRenderer renderer) {
		super(renderer);
	}
}
