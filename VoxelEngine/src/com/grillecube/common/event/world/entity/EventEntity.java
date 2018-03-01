package com.grillecube.common.event.world.entity;

import com.grillecube.common.event.Event;
import com.grillecube.common.world.entity.WorldEntity;

public abstract class EventEntity extends Event {
	private final WorldEntity entity;

	public EventEntity(WorldEntity entity) {
		super();
		this.entity = entity;
	}

	public final WorldEntity getEntity() {
		return (this.entity);
	}

}
