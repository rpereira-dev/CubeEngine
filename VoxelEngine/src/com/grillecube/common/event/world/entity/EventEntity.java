package com.grillecube.common.event.world.entity;

import com.grillecube.common.event.Event;
import com.grillecube.common.world.entity.Entity;

public abstract class EventEntity extends Event {
	private final Entity entity;

	public EventEntity(Entity entity) {
		super();
		this.entity = entity;
	}

	public final Entity getEntity() {
		return (this.entity);
	}

}
