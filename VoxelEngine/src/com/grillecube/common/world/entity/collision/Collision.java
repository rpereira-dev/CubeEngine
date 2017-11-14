package com.grillecube.common.world.entity.collision;

import org.junit.Test;

import com.grillecube.common.maths.Maths;
import com.grillecube.common.world.entity.Entity;

import junit.framework.Assert;

public class Collision {

	/** return every blocks which collides with the given bounding box */
	// public ArrayList<Block> getCollidingBlocks(AABB box) {
	// ArrayList<Block> lst = new ArrayList<Block>();
	//
	// int minx = Maths.floor(box.getMinX());
	// int maxx = Maths.floor(box.getMaxX() + 1.0D);
	// int miny = Maths.floor(box.getMinY());
	// int maxy = Maths.floor(box.getMaxY() + 1.0D);
	// int minz = Maths.floor(box.getMinZ());
	// int maxz = Maths.floor(box.getMaxZ() + 1.0D);
	//
	// Vector3i pos = new Vector3i();
	//
	// // iterate though each blocks
	// for (pos.x = minx; pos.x < maxx; ++pos.x) {
	// for (pos.z = mi.getPositionY(); pos.z < maxz; ++pos.z) {
	// for (pos.y = miny; pos.y < maxy; ++pos.y) {
	// Block block = this.getBlock(pos.x, pos.y, pos.z);
	// if (block.isWalkable()) {
	// lst.add(block);
	// }
	// }
	// }
	// }
	// return (lst);
	// }

	/** return every blocks which collides with the given bounding box */
	// public ArrayList<Entity> getCollidingEntities(AABB box) {
	// ArrayList<Entity> lst = new ArrayList<Entity>();
	// Collection<Entity> entities = this.getEntityStorage().getEntities();
	// for (Entity entity : entities) {
	// if (entity.getBoundingBox().intersect(box)) {
	// lst.add(entity);
	// }
	// }
	//
	// return (lst);
	// }

	/**
	 * https://www.gamedev.net/articles/programming/general-and-gameplay-programming/swept-aabb-collision-detection-and-response-r3084/
	 * 
	 */

	/**
	 * get a collision response from the two given object
	 * 
	 * @param b1
	 * @param b2
	 * @param vx
	 * @param vy
	 * @param vz
	 * @return
	 */
	public static final CollisionResponse collisionResponseAABBSwept(PhysicObject b1, PhysicObject b2) {

		// extract positions
		float x1 = b1.getPositionX();
		float y1 = b1.getPositionY();
		float z1 = b1.getPositionZ();

		float x2 = b2.getPositionX();
		float y2 = b2.getPositionY();
		float z2 = b2.getPositionZ();

		// extract velocities
		float vx = b1.getPositionVelocityX();
		float vy = b1.getPositionVelocityY();
		float vz = b1.getPositionVelocityZ();

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
		if (entryTime > exitTime || (xEntry < 0.0f && yEntry < 0.0f && zEntry < 0.0f)) {
			return (null);
		}

		// if there was a collision
		// calculate normal of collided surface
		float nx, ny, nz;
		if (xEntry > yEntry && xEntry > zEntry) {
			if (xInvEntry < 0.0f) {
				nx = 1.0f;
				ny = 0.0f;
				nz = 0.0f;
			} else {
				nx = -1.0f;
				ny = 0.0f;
				nz = 0.0f;
			}
		} else if (yEntry > xEntry && yEntry > zEntry) {
			if (yInvEntry < 0.0f) {
				nx = 0.0f;
				ny = 1.0f;
				nz = 0.0f;
			} else {
				nx = 0.0f;
				ny = -1.0f;
				nz = 0.0f;
			}
		} else {
			if (zInvEntry < 0.0f) {
				nx = 0.0f;
				ny = 0.0f;
				nz = 1.0f;
			} else {
				nx = 0.0f;
				ny = 0.0f;
				nz = -1.0f;
			}
		}

		// time of collision
		float dt = entryTime;
		return (new CollisionResponse(nx, ny, nz, dt));
	}

	/**
	 * Simulate the movement and return a proper CollisionResponse
	 *
	 * @param positioneable
	 *            : the physic object
	 * @param vx
	 *            : x velocity
	 * @param vy
	 *            : y velocity
	 * @param vz
	 *            : z velocity
	 * @param dt
	 *            : the time length to move
	 * @return a proper CollisionResponse
	 */
	public static CollisionResponse collisionResponse(PhysicObject physicObject, float vx, float vy, float vz,
			float dt) {
		float dx = vx * dt;
		float dy = vy * dt;
		float dz = vz * dt;
		// TODO : final collision response, on closest object
		// for each other objects
		// get collision response
		// get closest response
		// return it
		return (null);
	}

	/**
	 * 
	 * Simulates a deflection on after the 'physicObject' enters in collision
	 * with 'collisionResponse'
	 * 
	 * @param physicObject
	 *            : the physic object
	 * @param collisionResponse
	 *            : the collision response, (returned by
	 *            {@link #collisionResponseAABBSwept(PhysicObject, PhysicObject)}
	 * @param absorption
	 *            : amount of velocity to be absorbed by the collision
	 */
	public static final void deflects(PhysicObject physicObject, CollisionResponse collisionResponse,
			float absorption) {
		if (Maths.abs(collisionResponse.nx) > 0.0001f) {
			physicObject.setPositionVelocityX(-physicObject.getPositionVelocityX() * absorption);
		}
		if (Maths.abs(collisionResponse.ny) > 0.0001f) {
			physicObject.setPositionVelocityY(-physicObject.getPositionVelocityY() * absorption);
		}
		if (Maths.abs(collisionResponse.nz) > 0.0001f) {
			physicObject.setPositionVelocityZ(-physicObject.getPositionVelocityZ() * absorption);
		}
	}

	/**
	 * Simulates a push response after the 'physicObject' enters in collision
	 * with 'collisionResponse'
	 * 
	 * @param physicObject
	 *            : the physic object
	 * @param collisionResponse
	 *            : the collision response, (returned by
	 *            {@link #collisionResponseAABBSwept(PhysicObject, PhysicObject)}
	 * @param absorption
	 *            : amount of velocity to be absorbed by the collision
	 */
	public static final void push(PhysicObject physicObject, CollisionResponse collisionResponse) {
		// TODO
	}

	/**
	 * 
	 * Simulates a slide response after the 'physicObject' enters in collision
	 * with 'collisionResponse'
	 * 
	 * @param physicObject
	 *            : the physic object
	 * @param collisionResponse
	 *            : the collision response, (returned by
	 *            {@link #collisionResponseAABBSwept(PhysicObject, PhysicObject)}
	 * @param absorption
	 *            : amount of velocity to be absorbed by the collision
	 */
	public static final void slide(PhysicObject physicObject, CollisionResponse collisionResponse) {
		// TODO
	}

	@Test
	public void entityToWallCollision() {
		Entity entity = new Entity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		entity.setSize(1.0f, 2.0f, 1.0f);
		entity.setPosition(0.0f, 00f, 0.0f);
		entity.setPositionVelocity(1.0f, 0.0f, 0.0f);

		Entity block = new Entity() {
			@Override
			protected void onUpdate(double dt) {
			}
		};
		block.setSize(1.0f, 1.0f, 1.0f);
		block.setPosition(6.0f, 0, 0.0f);

		CollisionResponse collisionResponse = Collision.collisionResponseAABBSwept(entity, block);
		Assert.assertEquals(collisionResponse, new CollisionResponse(-1.0f, 0.0f, 0.0f, 5.0f));
	}
}
