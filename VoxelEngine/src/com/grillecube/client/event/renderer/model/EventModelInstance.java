package com.grillecube.client.event.renderer.model;

import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.event.Event;

public class EventModelInstance extends Event {
	private final ModelInstance modelInstance;

	public EventModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}

	public final ModelInstance getModelInstance() {
		return (this.modelInstance);
	}
}
