package com.grillecube.common.world.events;

import com.grillecube.common.event.Event;
import com.grillecube.common.world.entity.Entity;

public abstract class EventEntity extends Event
{
	private Entity _entity;
	
	public EventEntity(Entity entity)
	{
		this._entity = entity;
	}
	
	public Entity getEntity()
	{
		return (this._entity);
	}

}
