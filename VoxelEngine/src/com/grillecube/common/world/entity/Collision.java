package com.grillecube.common.world.entity;

import com.grillecube.common.maths.AABB;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.block.Block;

/** https://github.com/minetest/minetest/blob/master/src/collision.cpp */
public class Collision {

	public static final void collisionMoveSimple(Entity entity, float dx, float dy, float dz, float dt) {

		float x = entity.getPosition().x;
		float y = entity.getPosition().y;
		float z = entity.getPosition().z;

		float nx = x + dx * dt;
		float ny = y + dy * dt;
		float nz = z + dz * dt;

		int mx = Maths.floor(Maths.min(x, nx));
		int my = Maths.floor(Maths.min(y, ny));
		int mz = Maths.floor(Maths.min(z, nz));

		int Mx = Maths.ceil(Maths.max(x, nx));
		int My = Maths.ceil(Maths.max(y, ny));
		int Mz = Maths.ceil(Maths.max(z, nz));

		//for each potential collided-blocks
		for (int i = mx; i < Mx; i++) {
			for (int j = my; j < My; j++) {
				for (int k = mz; k < Mz; k++) {
					//get the block
					Block block = entity.getWorld().getBlock(i, j, k);
					//does it interfere with collision?
					if (!block.isWalkable()) {
						continue;
					}
					//if so, 
				}
			}
		}
	}

	// Helper function:
	// Checks for collision of a moving aabbox with a static aabbox
	// Returns -1 if no collision, 0 if X collision, 1 if Y collision, 2 if Z
	// collision
	// The time after which the collision occurs is stored in dtime.
	private static final int axisAlignedCollision(AABB staticbox, AABB movingbox, Vector3f speed, float d) {
		float xsize = staticbox.getSize().x;
		float ysize = staticbox.getSize().y;
		float zsize = staticbox.getSize().z;

		AABB relbox = new AABB();
		relbox.setMinMax(movingbox.getMin().x - staticbox.getMin().x, movingbox.getMin().y - staticbox.getMin().y,
				movingbox.getMin().z - staticbox.getMin().z, movingbox.getMax().x - staticbox.getMin().x,
				movingbox.getMax().y - staticbox.getMin().y, movingbox.getMax().z - staticbox.getMin().z);

		float dtime = 0.0f;

		if (speed.x > 0) { // Check for collision with X- plane
			if (relbox.getMax().x <= d) {
				dtime = -relbox.getMax().x / speed.x;
				if ((relbox.getMin().y + speed.y * dtime < ysize) && (relbox.getMax().y + speed.y * dtime > 0.0f)
						&& (relbox.getMin().z + speed.z * dtime < zsize)
						&& (relbox.getMax().z + speed.z * dtime > 0.0f))
					return 0;
			} else if (relbox.getMin().x > xsize) {
				return -1;
			}
		} else if (speed.x < 0) // Check for collision with X+ plane
		{
			if (relbox.getMin().x >= xsize - d) {
				dtime = (xsize - relbox.getMin().x) / speed.x;
				if ((relbox.getMin().y + speed.y * dtime < ysize) && (relbox.getMax().y + speed.y * dtime > 0.0f)
						&& (relbox.getMin().z + speed.z * dtime < zsize)
						&& (relbox.getMax().z + speed.z * dtime > 0.0f))
					return 0;
			} else if (relbox.getMax().x < 0) {
				return -1;
			}
		}

		// NO else if here

		if (speed.y > 0) // Check for collision with Y- plane
		{
			if (relbox.getMax().y <= d) {
				dtime = -relbox.getMax().y / speed.y;
				if ((relbox.getMin().x + speed.x * dtime < xsize) && (relbox.getMax().x + speed.x * dtime > 0.0f)
						&& (relbox.getMin().z + speed.z * dtime < zsize)
						&& (relbox.getMax().z + speed.z * dtime > 0.0f))
					return 1;
			} else if (relbox.getMin().y > ysize) {
				return -1;
			}
		} else if (speed.y < 0) // Check for collision with Y+ plane
		{
			if (relbox.getMin().y >= ysize - d) {
				dtime = (ysize - relbox.getMin().y) / speed.y;
				if ((relbox.getMin().x + speed.x * dtime < xsize) && (relbox.getMax().x + speed.x * dtime > 0.0f)
						&& (relbox.getMin().z + speed.z * dtime < zsize)
						&& (relbox.getMax().z + speed.z * dtime > 0.0f))
					return 1;
			} else if (relbox.getMax().y < 0) {
				return -1;
			}
		}

		// NO else if here

		if (speed.z > 0) // Check for collision with Z- plane
		{
			if (relbox.getMax().z <= d) {
				dtime = -relbox.getMax().z / speed.z;
				if ((relbox.getMin().x + speed.x * dtime < xsize) && (relbox.getMax().x + speed.x * dtime > 0.0f)
						&& (relbox.getMin().y + speed.y * dtime < ysize)
						&& (relbox.getMax().y + speed.y * dtime > 0.0f))
					return 2;
			}
			// else if(relbox.getMin().z > zsize)
			// {
			// return -1;
			// }
		} else if (speed.z < 0) // Check for collision with Z+ plane
		{
			if (relbox.getMin().z >= zsize - d) {
				dtime = (zsize - relbox.getMin().z) / speed.z;
				if ((relbox.getMin().x + speed.x * dtime < xsize) && (relbox.getMax().x + speed.x * dtime > 0.0f)
						&& (relbox.getMin().y + speed.y * dtime < ysize)
						&& (relbox.getMax().y + speed.y * dtime > 0.0f))
					return 2;
			}
			// else if(relbox.getMax().z < 0)
			// {
			// return -1;
			// }
		}

		return -1;
	}
}
