package com.grillecube.common.world.entity.forces;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.entity.collision.PhysicObject;

/**
 * Represent a force to be applied to entities
 * 
 * @author Romain
 *
 */
public abstract class Force<T extends PhysicObject> {

	/** gravity */
	public static final ForceGravity GRAVITY = new ForceGravity();
	public static final ForceFriction FRICTION = new ForceAirFriction();

	public final void updateResultant(T object, Vector3f resultant) {
		this.onUpdateResultant(object, resultant);
	}

	/** called when this state is set and the object is updated. */
	public abstract void onUpdateResultant(T object, Vector3f resultant);
}