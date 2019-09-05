package com.grillecube.client.event.renderer;

import com.grillecube.client.renderer.MainRenderer;

/** an event which is called after the MainRenderer being initialized */
public class EventPostRendererInitialisation extends EventRender {

	public EventPostRendererInitialisation(MainRenderer renderer) {
		super(renderer);
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
