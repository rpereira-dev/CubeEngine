package com.grillecube.common.world.entity.ai;

import com.grillecube.common.world.entity.Entity;

public abstract class EntityAI {
	
	/** the entity for this ai */
	private final Entity _entity;
	
	/** in ms */
	private long _last_update;

	/** time to update */
	private long _update_time;

	public EntityAI(Entity entity) {
		this._entity = entity;
		this._update_time = Long.MAX_VALUE;
	}
	
	/** called on every entity's update */
	public final void update() {
		this.onUpdate();
		long now = System.currentTimeMillis();
		if (now - this.getLastUpdate() > this.getUpdateTime()) {
			this.onTimedUpdate();
			this._last_update = now;			
		}
	}

	/** called on every entity's update */
	protected abstract void onUpdate();
	
	/** called every 'this._update_time' in ms */
	protected abstract void onTimedUpdate();
	
	public long getUpdateTime() {
		return (this._update_time);
	}

	/** set the time on which the update ufnciotn should be called */
	public void setUpdateTime(long time) {
		this._update_time = time;
	}
	
	public long getLastUpdate() {
		return (this._last_update);
	}

	public Entity getEntity() {
		return (this._entity);
	}
}
