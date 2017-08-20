package com.grillecube.client.event.world;

import com.grillecube.client.renderer.world.factories.ModelRendererFactory;
import com.grillecube.common.event.Event;

/**
 * an event which is called whenever a new entity rendering list is generated
 */
public class EventEntityList extends Event {

	private final ModelRendererFactory modelRendererFactory;

	public EventEntityList(ModelRendererFactory modelRendererFactory) {
		this.modelRendererFactory = modelRendererFactory;
	}

	public final ModelRendererFactory getmodelRendererFactory() {
		return (this.modelRendererFactory);
	}

}
