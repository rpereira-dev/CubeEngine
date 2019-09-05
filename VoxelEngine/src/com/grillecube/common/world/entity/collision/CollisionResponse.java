package com.grillecube.common.world.entity.collision;

import com.grillecube.common.maths.Maths;
import com.grillecube.common.world.physic.WorldObject;

public class CollisionResponse {
	/**
	 * 
	 * Simulates a stick on after the 'physicObject' enters in collision with
	 * 'collisionResponse'
	 * 
	 * @param physicObject
	 *            : the physic object
	 * @param collisionResponse
	 *            : the collision response, (returned by
	 *            {@link #detectAABB(WorldObject, WorldObject)}
	 * @param absorption
	 *            : amount of velocity to be absorbed by the collision
	 */
	public static final void stick(WorldObject physicObject, CollisionDetection collisionResponse) {
		if (Maths.abs(collisionResponse.nx) > Maths.ESPILON) {
			physicObject.setPositionVelocityX(0.0f);
		}
		if (Maths.abs(collisionResponse.ny) > Maths.ESPILON) {
			physicObject.setPositionVelocityY(0.0f);
		}
		if (Maths.abs(collisionResponse.nz) > Maths.ESPILON) {
			physicObject.setPositionVelocityZ(0.0f);
		}
	}

	/**
	 * 
	 * Simulates a deflection on after the 'physicObject' enters in collision with
	 * 'collisionResponse'
	 * 
	 * @param physicObject
	 *            : the physic object
	 * @param collisionResponse
	 *            : the collision response, (returned by
	 *            {@link #detectAABB(WorldObject, WorldObject)}
	 * @param vx,
	 *            vy, vz : velocities on collision
	 * 
	 * @param absorption
	 *            : velocity percentage to be absorbed
	 */
	public static final void deflects(WorldObject physicObject, CollisionDetection collisionResponse, float vx,
			float vy, float vz, float absorption) {
		if (Maths.abs(collisionResponse.nx) > Maths.ESPILON) {
			physicObject.setPositionVelocityX(-vx * absorption);
		}
		if (Maths.abs(collisionResponse.ny) > Maths.ESPILON) {
			physicObject.setPositionVelocityY(-vy * absorption);
		}
		if (Maths.abs(collisionResponse.nz) > Maths.ESPILON) {
			physicObject.setPositionVelocityZ(-vz * absorption);
		}
	}

	public static final void deflects(WorldObject physicObject, CollisionDetection collisionResponse,
			float absorption) {
		float vx = physicObject.getPositionVelocityX();
		float vy = physicObject.getPositionVelocityY();
		float vz = physicObject.getPositionVelocityZ();
		deflects(physicObject, collisionResponse, vx, vy, vz, absorption);
	}

	/**
	 * Simulates a push response after the 'physicObject' enters in collision with
	 * 'collisionResponse'
	 * 
	 * @param physicObject
	 *            : the physic object
	 * @param collisionResponse
	 *            : the collision response, (returned by
	 *            {@link #detectAABB(WorldObject, WorldObject)}
	 * @param dt
	 *            : remaining time
	 */
	public static final void push(WorldObject physicObject, CollisionDetection collisionResponse) {
		// push
		float vx = physicObject.getPositionVelocityX();
		float vy = physicObject.getPositionVelocityY();
		float vz = physicObject.getPositionVelocityZ();
		float nx = collisionResponse.nx;
		float ny = collisionResponse.ny;
		float nz = collisionResponse.nz;
		physicObject.setPositionVelocity(0, 0, 0);

		if (nx != 0) {
			physicObject.setPositionVelocity(0, -vx * Maths.sign(vy) * nx, vz);
		}
	}

	/**
	 * 
	 * Simulates a slide response after the 'physicObject' enters in collision with
	 * 'collisionResponse'
	 * 
	 * @param physicObject
	 *            : the physic object
	 * @param collisionResponse
	 *            : the collision response, (returned by
	 *            {@link #detectAABB(WorldObject, WorldObject)}
	 * @param absorption
	 *            : amount of velocity to be absorbed by the collision
	 */
	public static final void slide(WorldObject physicObject, CollisionDetection collisionResponse) {
		// TODO
	}
}
