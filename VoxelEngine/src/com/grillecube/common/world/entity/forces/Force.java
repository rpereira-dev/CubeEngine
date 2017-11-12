package com.grillecube.common.world.entity.forces;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.Entity;

/**
 * Represent a force to be applied to entities
 * 
 * @author Romain
 *
 */
public abstract class Force<T extends Entity> {

	/** gravity */
	public static final ForceGravity GRAVITY = new ForceGravity();
	public static final ForceFriction FRICTION = new ForceAirFriction();

	public final void updateResultant(T entity, Vector3f resultant) {
		this.onUpdateResultant(entity, resultant);
	}

	/** called when this state is set and the entity is updated. */
	public abstract void onUpdateResultant(T entity, Vector3f resultant);
}