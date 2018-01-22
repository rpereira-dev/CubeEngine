package com.grillecube.common.world.entity.collision;

import java.util.ArrayList;

import com.grillecube.common.maths.Maths;

public class CollisionDetection {

	/** the moving physic object which collided with 'collided' */
	public final PhysicObject moving;

	/** the physic object with which the collision occured */
	public final PhysicObject collided;

	/** normal x between the face and the direction */
	public final float nx;

	/** normal y between the face and the direction */
	public final float ny;

	/** normal z between the face and the direction */
	public final float nz;

	/** time until the collision will happens */
	public final float dt;

	public CollisionDetection(PhysicObject moving, PhysicObject collided, float nx, float ny, float nz, float dt) {
		this.moving = moving;
		this.collided = collided;
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
		this.dt = dt;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CollisionDetection)) {
			return (false);
		}
		CollisionDetection o = (CollisionDetection) other;
		return (this.nx == o.nx && this.ny == o.ny && this.nz == o.nz && this.dt == o.dt);
	}

	@Override
	public String toString() {
		return ("CollisionResponse{nx=" + this.nx + ";ny=" + this.ny + ";nz=" + this.nz + ";dt=" + this.dt + "}");
	}

	/**
	 * https://www.gamedev.net/articles/programming/general-and-gameplay-programming/swept-aabb-collision-detection-and-response-r3084/
	 * 
	 */

	/**
	 * get a collision response from the two given object
	 * 
	 * @param b1
	 *            : a moving object on which we test collision toward b2
	 * @param b2
	 *            : an object statically considered
	 * @param dt
	 *            : the time length during b1 moves
	 * @return
	 */
	public static final CollisionDetection detectAABB(PhysicObject b1, PhysicObject b2, double dt) {

		// velocity
		float vx = b1.getPositionVelocityX();
		float vy = b1.getPositionVelocityY();
		float vz = b1.getPositionVelocityZ();

		// extract positions
		float x1 = b1.getPositionX();
		float y1 = b1.getPositionY();
		float z1 = b1.getPositionZ();

		float x2 = b2.getPositionX();
		float y2 = b2.getPositionY();
		float z2 = b2.getPositionZ();

		// extract size
		float sx1 = b1.getSizeX();
		float sy1 = b1.getSizeY();
		float sz1 = b1.getSizeZ();

		float sx2 = b2.getSizeX();
		float sy2 = b2.getSizeY();
		float sz2 = b2.getSizeZ();

		// find the distance between the objects on the near and far sides of
		// each axis
		float xInvEntry, yInvEntry, zInvEntry;
		float xInvExit, yInvExit, zInvExit;

		// x axis entry and exit
		if (vx > 0.0f) {
			xInvEntry = x2 - (x1 + sx1);
			xInvExit = (x2 + sx2) - x1;
		} else {
			xInvEntry = (x2 + sx2) - x1;
			xInvExit = x2 - (x1 + sx1);
		}
		if (vy > 0.0f) {
			yInvEntry = y2 - (y1 + sy1);
			yInvExit = (y2 + sy2) - y1;
		} else {
			yInvEntry = (y2 + sy2) - y1;
			yInvExit = y2 - (y1 + sy1);
		}
		if (vz > 0.0f) {
			zInvEntry = z2 - (z1 + sz1);
			zInvExit = (z2 + sz2) - z1;
		} else {
			zInvEntry = (z2 + sz2) - z1;
			zInvExit = z2 - (z1 + sz1);
		}

		// find time of collision and time of leaving for each axis (if
		// statement is to prevent divide by zero)
		float xEntry, yEntry, zEntry;
		float xExit, yExit, zExit;
		if (vx == 0.0f) {
			xEntry = Float.NEGATIVE_INFINITY;
			xExit = Float.POSITIVE_INFINITY;
		} else {
			xEntry = xInvEntry / vx;
			xExit = xInvExit / vx;
		}
		if (vy == 0.0f) {
			yEntry = Float.NEGATIVE_INFINITY;
			yExit = Float.POSITIVE_INFINITY;
		} else {
			yEntry = yInvEntry / vy;
			yExit = yInvExit / vy;
		}

		if (vz == 0.0f) {
			zEntry = Float.NEGATIVE_INFINITY;
			zExit = Float.POSITIVE_INFINITY;
		} else {
			zEntry = zInvEntry / vz;
			zExit = zInvExit / vz;
		}

		// find the earliest/latest times of collision
		float entryTime = Maths.max(xEntry, Maths.max(yEntry, zEntry));
		float exitTime = Maths.min(xExit, Maths.min(yExit, zExit));

		// if there was no collision
		if (entryTime >= exitTime || entryTime < 0.0f) {
			return (null);
		}

		// if there was a collision
		// calculate normal of collided surface
		float nx, ny, nz;
		if (xEntry > yEntry && xEntry > zEntry) {
			if (vx < 0.0f) {
				nx = 1.0f;
				ny = 0.0f;
				nz = 0.0f;
			} else {
				nx = -1.0f;
				ny = 0.0f;
				nz = 0.0f;
			}
		} else if (yEntry > xEntry && yEntry > zEntry) {
			if (vy < 0.0f) {
				nx = 0.0f;
				ny = 1.0f;
				nz = 0.0f;
			} else {
				nx = 0.0f;
				ny = -1.0f;
				nz = 0.0f;
			}
		} else {
			if (vz < 0.0f) {
				nx = 0.0f;
				ny = 0.0f;
				nz = 1.0f;
			} else {
				nx = 0.0f;
				ny = 0.0f;
				nz = -1.0f;
			}
		}

		return (new CollisionDetection(b1, b2, nx, ny, nz, entryTime));
	}

	/**
	 * get a collision response from 'b1' moving, tested on each object of 'b2s'
	 * 
	 * @param b1
	 *            : moving entity to test
	 * @param b2s
	 *            : world collideable objects
	 * @param dt
	 *            : duration of movement
	 * @return : the collision detected, or NULL if no collision
	 */
	public static final CollisionDetection detect(PhysicObject b1, ArrayList<PhysicObject> b2s, double dt) {
		if (b1.getPositionVelocityX() == 0.0f && b1.getPositionVelocityY() == 0.0f
				&& b1.getPositionVelocityZ() == 0.0f) {
			return (null);
		}

		CollisionDetection collisionResponse = null;
		for (PhysicObject b2 : b2s) {
			if (b1 == b2) {
				continue;
			}

			CollisionDetection c = detectAABB(b1, b2, dt);
			if (c != null && (collisionResponse == null || c.dt < collisionResponse.dt)) {
				collisionResponse = c;
			}
		}
		return (collisionResponse);
	}
}
