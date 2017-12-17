package com.grillecube.client.event.renderer.model;

import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.event.Event;

public abstract class EventModelInstance extends Event {
	private final ModelInstance modelInstance;

	public EventModelInstance(ModelInstance modelInstance) {
		super();
		this.modelInstance = modelInstance;
	}

	public final ModelInstance getModelInstance() {
		return (this.modelInstance);
	}
}
