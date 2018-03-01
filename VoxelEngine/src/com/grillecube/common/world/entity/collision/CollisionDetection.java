package com.grillecube.common.world.entity.collision;

import java.util.ArrayList;

import com.grillecube.common.maths.Maths;
import com.grillecube.common.world.physic.WorldObject;

public class CollisionDetection {

	/** the margin to use */
	public static final float MARGIN = 0.0f;

	/** the moving physic object which collided with 'collided' */
	public final WorldObject moving;

	/** the physic object with which the collision occured */
	public final WorldObject collided;

	/** normal x between the face and the direction */
	public final float nx;

	/** normal y between the face and the direction */
	public final float ny;

	/** normal z between the face and the direction */
	public final float nz;

	/** time until the collision will happens */
	public final float dt;

	public CollisionDetection(WorldObject moving, WorldObject collided, float nx, float ny, float nz, float dt) {
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
		return (Maths.abs(this.nx - o.nx) <= MARGIN && Maths.abs(this.ny - o.ny) <= MARGIN
				&& Maths.abs(this.nz - o.nz) <= MARGIN
				&& Maths.abs(this.dt - o.dt) <= o.moving.getPositionVelocity() / MARGIN);
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
	public static final CollisionDetection detectAABB(WorldObject b1, WorldObject b2, double dt) {
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
		float sx1 = b1.getSizeX() - MARGIN;
		float sy1 = b1.getSizeY() - MARGIN;
		float sz1 = b1.getSizeZ() - MARGIN;

		float sx2 = b2.getSizeX() - MARGIN;
		float sy2 = b2.getSizeY() - MARGIN;
		float sz2 = b2.getSizeZ() - MARGIN;

		if (vx == 0.0f && (x1 + sx1 <= x2 || x1 >= x2 + sx2)) {
			return (null);
		}
		if (vy == 0.0f && (y1 + sy1 <= y2 || y1 >= y2 + sy2)) {
			return (null);
		}
		if (vz == 0.0f && (z1 + sz1 <= z2 || z1 >= z2 + sz2)) {
			return (null);
		}
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
	public static final CollisionDetection detect(WorldObject b1, ArrayList<WorldObject> b2s, double dt) {
		// TODO : keep this? two entities may overlap
		if (b1.getPositionVelocityX() == 0.0f && b1.getPositionVelocityY() == 0.0f
				&& b1.getPositionVelocityZ() == 0.0f) {
			return (null);
		}

		CollisionDetection collisionResponse = null;
		for (WorldObject b2 : b2s) {
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
