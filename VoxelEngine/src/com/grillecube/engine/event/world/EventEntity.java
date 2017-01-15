package com.grillecube.engine.event.world;

import com.grillecube.engine.event.Event;
import com.grillecube.engine.world.entity.Entity;

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
