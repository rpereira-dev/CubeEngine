package com.grillecube.common.world.entity.ai;

import com.grillecube.common.world.entity.WorldEntity;

public abstract class EntityAI<T extends WorldEntity> {

	/** in seconds, accumulate times since last update */
	private double accumulator;

	/** time needed to accumulate before updating */
	private double updateTime;

	public EntityAI() {
		this.accumulator = 0;
		this.updateTime = Double.MAX_VALUE;
	}

	/** called on every entity's update */
	public final void update(T entity, double dt) {
		this.onUpdate(entity, dt);
		this.accumulator += dt;
		int n = (int) (this.accumulator / this.updateTime);

		if (n > 0) {
			do {
				--n;
				this.onTimedUpdate(entity);
			} while (n > 0);
			this.accumulator -= n * this.updateTime;
		}
	}

	/**
	 * called on every entity's update
	 * 
	 * @param entity
	 *            the entity
	 * @param dt
	 */
	protected abstract void onUpdate(T entity, double dt);

	/**
	 * called until the time stored in {@link #accumulator} < {@link #updateTime}
	 * needed by the accumulator
	 */
	protected abstract void onTimedUpdate(T entity);

	public final double getUpdateTime() {
		return (this.updateTime);
	}

	/** set the time on which the update function should be called */
	public final void setUpdateTime(double time) {
		this.updateTime = time;
	}

	public final double getAccumulator() {
		return (this.accumulator);
	}
}
