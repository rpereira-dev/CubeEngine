package com.grillecube.common.world.entity.collision;

import java.util.ArrayList;

import com.grillecube.common.Logger;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.World;

/**
 * Abstract class for a World object (coordinates should be world-relative)
 * 
 * @author Romain
 *
 */
public abstract class PhysicObject implements Positioneable, Rotationable, Sizeable {

	/**
	 * @return the physic object mass in kg
	 */
	public abstract float getMass();

	/** set the mass for this object */
	public abstract void setMass(float mass);

	/** position */
	public final void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public final void setPosition(float x, float y, float z) {
		this.setPositionX(x);
		this.setPositionY(y);
		this.setPositionZ(z);
	}

	public final void setPositionVelocity(Vector3f size) {
		this.setPositionVelocity(size.x, size.y, size.z);
	}

	public final void setPositionVelocity(float x, float y, float z) {
		this.setPositionVelocityX(x);
		this.setPositionVelocityY(y);
		this.setPositionVelocityZ(z);
	}

	public final void setPositionAcceleration(Vector3f size) {
		this.setPositionAcceleration(size.x, size.y, size.z);
	}

	public final void setPositionAcceleration(float x, float y, float z) {
		this.setPositionAccelerationX(x);
		this.setPositionAccelerationY(y);
		this.setPositionAccelerationZ(z);
	}

	/** rotation */
	public final void setRotation(Vector3f rot) {
		this.setRotation(rot.x, rot.y, rot.z);
	}

	public final void setRotation(float x, float y, float z) {
		this.setRotationX(x);
		this.setRotationY(y);
		this.setRotationZ(z);
	}

	public final void setRotationVelocity(Vector3f size) {
		this.setRotationVelocity(size.x, size.y, size.z);
	}

	public final void setRotationVelocity(float x, float y, float z) {
		this.setRotationVelocityX(x);
		this.setRotationVelocityY(y);
		this.setRotationVelocityZ(z);
	}

	public final void setRotationAcceleration(Vector3f size) {
		this.setRotationAcceleration(size.x, size.y, size.z);
	}

	public final void setRotationAcceleration(float x, float y, float z) {
		this.setRotationAccelerationX(x);
		this.setRotationAccelerationY(y);
		this.setRotationAccelerationZ(z);
	}

	/** size */
	public final void setSize(Vector3f size) {
		this.setSize(size.x, size.y, size.z);
	}

	public final void setSize(float x, float y, float z) {
		this.setSizeX(x);
		this.setSizeY(y);
		this.setSizeZ(z);
	}

	public final void setSizeVelocity(Vector3f size) {
		this.setSizeVelocity(size.x, size.y, size.z);
	}

	public final void setSizeVelocity(float x, float y, float z) {
		this.setSizeVelocityX(x);
		this.setSizeVelocityY(y);
		this.setSizeVelocityZ(z);
	}

	public final void setSizeAcceleration(Vector3f size) {
		this.setSizeAcceleration(size.x, size.y, size.z);
	}

	public final void setSizeAcceleration(float x, float y, float z) {
		this.setSizeAccelerationX(x);
		this.setSizeAccelerationY(y);
		this.setSizeAccelerationZ(z);
	}

	/** move this physic object in the given world, handling collisions */
	public static final void move(World world, PhysicObject physicObject, double dt) {

		// swept
		float x = physicObject.getPositionX();
		float y = physicObject.getPositionY();
		float z = physicObject.getPositionZ();
		float vx = physicObject.getPositionVelocityX();
		float vy = physicObject.getPositionVelocityY();
		float vz = physicObject.getPositionVelocityZ();
		float sx = physicObject.getSizeX();
		float sy = physicObject.getSizeY();
		float sz = physicObject.getSizeZ();
		float dx = (float) (vx * dt);
		float dy = (float) (vy * dt);
		float dz = (float) (vz * dt);

		float minx, miny, minz;
		float maxx, maxy, maxz;

		if (dx >= 0) {
			minx = x;
			maxx = x + sx + dx;
		} else {
			minx = x + dx;
			maxx = x + sx;
		}

		if (dy >= 0) {
			miny = y;
			maxy = y + sy + dy;
		} else {
			miny = y + dy;
			maxy = y + sy;
		}

		if (dz >= 0) {
			minz = z;
			maxz = z + sz + dz;
		} else {
			minz = z + dz;
			maxz = z + sz;
		}

		int i = 0;
		while (dt > 0.0001f) {
			ArrayList<PhysicObject> blocks = world.getCollidingPhysicObjects(minx, miny, minz, maxx, maxy, maxz);
			CollisionResponse collisionResponse = Collision.collisionResponseAABBSwept(physicObject, blocks);
			// if no collision, move
			if (collisionResponse == null || collisionResponse.dt > dt) {
				Positioneable.position(physicObject, dt);
				break;
			}
			// if collision, move just before it collides
			Positioneable.position(physicObject, collisionResponse.dt);

			// dt now contains the remaning time
			dt -= collisionResponse.dt;

			// stick right before collision, and continue collisions
			Collision.deflects(physicObject, collisionResponse, 0.2f);

			if (++i >= 5) {
				Logger.get().log(Logger.Level.WARNING,
						"Did 5 iterations when moving physic object... position may be wrong", physicObject);
				break;
			}
		}

	}
}
