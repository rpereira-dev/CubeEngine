package com.grillecube.common.world.entity.collision;

public abstract class PhysicObject implements Positioneable, Rotationable, Sizeable {

	/**
	 * @return the physic object mass in kg
	 */
	public abstract float getMass();

	/** set the mass for this object */
	public abstract void setMass(float mass);
}
