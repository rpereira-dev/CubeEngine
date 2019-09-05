package com.grillecube.common.world.physic;

import java.util.ArrayList;

import com.grillecube.common.Logger;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.collision.CollisionDetection;
import com.grillecube.common.world.entity.collision.CollisionResponse;

/**
 * Abstract class for a World object (coordinates should be world-relative)
 * 
 * @author Romain
 *
 */
public abstract class WorldObject implements Positioneable, Rotationable, Sizeable {

	private World world;

	public WorldObject(World world) {
		this.setWorld(world);
	}

	public final World getWorld() {
		return (this.world);
	}

	public final void setWorld(World world) {
		this.onWorldSet(world, this.world);
		this.world = world;
	}

	/**
	 * called when the world of this object changes
	 * 
	 * @param newWorld
	 * @param oldWorld
	 */
	protected void onWorldSet(World newWorld, World oldWorld) {
	}

	/**
	 * @return the physic object mass in kg
	 */
	public abstract float getMass();

	/** set the mass for this object */
	public abstract void setMass(float mass);

	/** an update method, called right after the JBullet world update */
	protected void postWorldUpdate(double dt) {
	}

	/** an update method, called right before the JBullet world update */
	protected void preWorldUpdate(double dt) {
	}

	/** position */
	public final void setPosition(Vector3f pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

	public void setPosition(float x, float y, float z) {
		this.setPositionX(x);
		this.setPositionY(y);
		this.setPositionZ(z);
	}

	public void setPositionVelocity(Vector3f size) {
		this.setPositionVelocity(size.x, size.y, size.z);
	}

	public void setPositionVelocity(float x, float y, float z) {
		this.setPositionVelocityX(x);
		this.setPositionVelocityY(y);
		this.setPositionVelocityZ(z);
	}

	public void setPositionAcceleration(Vector3f size) {
		this.setPositionAcceleration(size.x, size.y, size.z);
	}

	public void setPositionAcceleration(float x, float y, float z) {
		this.setPositionAccelerationX(x);
		this.setPositionAccelerationY(y);
		this.setPositionAccelerationZ(z);
	}

	/** rotation */
	public void setRotation(Vector3f rot) {
		this.setRotation(rot.x, rot.y, rot.z);
	}

	public void setRotation(float x, float y, float z) {
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

	public void setSize(float x, float y, float z) {
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

	/**
	 * move the given physic object in the given world, moving at velocity (vx, vy,
	 * vz) for a time of 'dt'
	 * 
	 * @param world
	 * @param worldObject
	 * @param vx
	 * @param vy
	 * @param vz
	 * @param dt
	 */
	public static final boolean move(World world, WorldObject worldObject, double dt) {
		// swept
		float x = worldObject.getPositionX();
		float y = worldObject.getPositionY();
		float z = worldObject.getPositionZ();
		float vx = worldObject.getPositionVelocityX();
		float vy = worldObject.getPositionVelocityY();
		float vz = worldObject.getPositionVelocityZ();
		float sx = worldObject.getSizeX();
		float sy = worldObject.getSizeY();
		float sz = worldObject.getSizeZ();
		float dx = (float) (vx * dt);
		float dy = (float) (vy * dt);
		float dz = (float) (vz * dt);

		float minx, miny, minz;
		float maxx, maxy, maxz;

		if (dx >= 0) {
			minx = x;
			maxx = x + sx + dx;
		} else {
			minx = x + dx - sx;
			maxx = x + sx;
		}

		if (dy >= 0) {
			miny = y;
			maxy = y + sy + dy;
		} else {
			miny = y + dy - sy;
			maxy = y + sy;
		}

		if (dz >= 0) {
			minz = z;
			maxz = z + sz + dz;
		} else {
			minz = z + dz - sz;
			maxz = z + sz;
		}

		boolean deflected = false;
		// Logger.get().log(Level.DEBUG, minx, miny, minz, maxx, maxy, maxz);
		int i = 0;
		while (dt > Maths.ESPILON) {
			ArrayList<WorldObject> objects = world.getCollidingPhysicObjects(worldObject, minx, miny, minz, maxx, maxy,
					maxz);
			CollisionDetection collisionDetection = CollisionDetection.detect(worldObject, objects, dt);

			// if no collision, move
			// System.out.println(collisionResponse);;

			if (collisionDetection == null || collisionDetection.dt >= dt) {
				Positioneable.position(worldObject, dt);
				break;
			}

			// if collision, move just before it collides
			Positioneable.position(worldObject, collisionDetection.dt);

			// dt now contains the remaning time
			dt -= collisionDetection.dt;

			// stick right before collision, and continue collisions
			CollisionResponse.deflects(worldObject, collisionDetection, 0.15f);
			deflected = true;
			// CollisionResponse.push(physicObject, collisionDetection);

			if (++i >= 5) {
				Logger.get().log(Logger.Level.WARNING,
						"Did 5 iterations when moving physic object... position may be wrong", worldObject);
				break;
			}
		}
		return (deflected);
	}

	public final float getPositionVelocity() {
		return (Vector3f.length(this.getPositionVelocityX(), this.getPositionVelocityY(), this.getPositionVelocityZ()));
	}
}
