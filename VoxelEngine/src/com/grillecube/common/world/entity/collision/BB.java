/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

/**
   o-----------------MAX
  /|       ^          /|
 o-------------------o |
 | |       |       ^ | |
 | |       |      /  | |
 | |     s |     /   | |
 | |     i |    /    | |
 | |     z |   /     | |
 | |     e |  /      | |
 | |     . | / size.x| |
 | |     y |/        | |
 | |       C-------->| |
 | |         size.z  | |
 | |                 | |
 | |                 | |
 | |                 | |
 | |                 | |
 | |                 | |
 | o_______________ _|_|
 |/                  | /
 MIN-----------------o
*/

package com.grillecube.common.world.entity.collision;

import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;

/**
 * Abstract class for bounding boxes
 * 
 * @author Romain
 *
 */
public class BB implements Positioneable, Sizeable {

	public static final AABB EMPTY_BOX = new AABB();

	private final Vector3f min;
	private final Vector3f size;

	public BB() {
		this.min = new Vector3f();
		this.size = new Vector3f();
	}

	public BB(BB bb) {
		this();
		this.min.set(bb.min);
		this.size.set(bb.size);
	}

	/** return the area of the bounding box */
	public final float getArea() {
		return (this.min.x * (this.min.x + this.size.x) + this.min.y * (this.min.y + this.size.y)
				+ this.min.z * (this.min.z + this.size.z));
	}

	/**
	 * @param point:
	 *            point to test
	 * @return true if the point is in the box
	 */
	public final boolean contains(Vector3f point) {
		return (this.contains(point.x, point.y, point.z));
	}

	/**
	 * @param x,
	 *            y, z: point to test
	 * @return true if the point is in the box
	 */
	public boolean contains(float x, float y, float z) {
		return (x >= this.min.x && x <= this.min.x + this.size.x && y >= this.min.y && y <= this.min.y + this.size.y
				&& z >= this.min.z && z <= this.min.z + this.size.z);
	}

	/**
	 * @return true if this boundingbox contains the given one
	 */
	public boolean contains(BB box) {
		return (this.getMinX() < box.getMinX() && this.getMinY() < box.getMinY() && this.getMinZ() < box.getMinZ()
				&& this.getMaxX() > box.getMaxX() && this.getMaxY() > box.getMaxY() && this.getMaxZ() > box.getMaxZ());
	}

	/**
	 * @return true if this boundingbox intersect with the given one
	 */
	public boolean intersect(BB box) {
		return ((this.getMinX() <= box.getMaxX() && this.getMaxX() >= box.getMinX())
				&& (this.getMinY() <= box.getMaxY() && this.getMaxY() >= box.getMinY())
				&& (this.getMinZ() <= box.getMaxZ() && this.getMaxZ() >= box.getMinZ()));

	}

	/** contains version for a sphere */
	public boolean contains(Vector3f center, float radius) {
		float s = 0;
		float d = 0;

		if (center.x < this.min.x) {
			s = center.x - this.min.x;
			d = s * s;
		} else {
			float maxx = this.min.x + this.size.x;
			if (center.x > maxx) {
				s = center.x - maxx;
				d += s * s;
			}
		}

		if (center.y < this.min.y) {
			s = center.y - this.min.y;
			d += s * s;
		} else {
			float maxy = this.min.y + this.size.y;
			if (center.y > maxy) {
				s = center.y - maxy;
				d += s * s;
			}
		}

		if (center.z < this.min.z) {
			s = center.z - this.min.z;
			d += s * s;
		} else {
			float maxz = this.min.z + this.size.z;
			if (center.z > maxz) {
				s = center.z - maxz;
				d += s * s;
			}
		}
		return (d <= radius * radius);

	}

	public void join(BB box) {
		// min
		if (box.min.x < this.min.x) {
			this.min.x = box.min.x;
		}

		if (box.min.y < this.min.y) {
			this.min.y = box.min.y;
		}

		if (box.min.z < this.min.z) {
			this.min.z = box.min.z;
		}

		// max
		if (box.min.x + box.size.x > this.min.x + this.size.x) {
			this.size.x = box.min.x + box.size.x - this.min.x;
		}

		if (box.min.y + box.size.y > this.min.y + this.size.y) {
			this.size.y = box.min.y + box.size.y - this.min.y;
		}

		if (box.min.z + box.size.z > this.min.z + this.size.z) {
			this.size.z = box.min.z + box.size.z - this.min.z;
		}
	}

	public void join(Vector4f vec) {
		if (vec.x > this.min.x + this.size.x) {
			this.size.x = vec.x - this.min.x;
		} else if (vec.x < this.min.x) {
			this.min.x = vec.x;
		}
		if (vec.y > this.min.y + this.size.y) {
			this.size.y = vec.y - this.min.y;
		} else if (vec.y < this.min.y) {
			this.min.y = vec.y;
		}
		if (vec.z > this.min.z + this.size.z) {
			this.size.z = vec.z - this.min.z;
		} else if (vec.z < this.min.z) {
			this.min.z = vec.z;
		}
	}

	/** getters and setters */
	public final float getMinX() {
		return (this.min.x);
	}

	public final float getMinY() {
		return (this.min.y);
	}

	public final float getMinZ() {
		return (this.min.z);
	}

	public final float getMaxX() {
		return (this.getMinX() + this.getSizeX());
	}

	public final float getMaxY() {
		return (this.getMinY() + this.getSizeY());
	}

	public final float getMaxZ() {
		return (this.getMinZ() + this.getSizeZ());
	}

	public final float getCenterX() {
		return (this.min.x + this.size.x * 0.5f);
	}

	public final float getCenterY() {
		return (this.min.y + this.size.y * 0.5f);
	}

	public final float getCenterZ() {
		return (this.min.z + this.size.z * 0.5f);
	}

	public final float getSizeX() {
		return (this.size.x);
	}

	public final float getSizeY() {
		return (this.size.y);
	}

	public final float getSizeZ() {
		return (this.size.z);
	}

	public final void setMinX(float x) {
		this.min.x = x;
	}

	public final void setMinY(float y) {
		this.min.y = y;
	}

	public final void setMinZ(float z) {
		this.min.z = z;
	}

	@Override
	public float getSizeVelocityX() {
		return (0);
	}

	@Override
	public float getSizeVelocityY() {
		return (0);
	}

	@Override
	public float getSizeVelocityZ() {
		return (0);
	}

	@Override
	public float getSizeAccelerationX() {
		return (0);
	}

	@Override
	public float getSizeAccelerationY() {
		return (0);
	}

	@Override
	public float getSizeAccelerationZ() {
		return (0);
	}

	@Override
	public final void setSizeX(float sx) {
		this.size.x = sx;
	}

	@Override
	public final void setSizeY(float sy) {
		this.size.y = sy;
	}

	@Override
	public final void setSizeZ(float sz) {
		this.size.z = sz;
	}

	@Override
	public void setSizeVelocityX(float vx) {
	}

	@Override
	public void setSizeVelocityY(float vy) {
	}

	@Override
	public void setSizeVelocityZ(float vz) {
	}

	@Override
	public void setSizeAccelerationX(float ax) {
	}

	@Override
	public void setSizeAccelerationY(float ay) {
	}

	@Override
	public void setSizeAccelerationZ(float az) {
	}

	@Override
	public float getPositionX() {
		return (this.getMinX());
	}

	@Override
	public float getPositionY() {
		return (this.getMinY());
	}

	@Override
	public float getPositionZ() {
		return (this.getMinZ());
	}

	@Override
	public float getPositionVelocityX() {
		return (0);
	}

	@Override
	public float getPositionVelocityY() {
		return (0);
	}

	@Override
	public float getPositionVelocityZ() {
		return (0);
	}

	@Override
	public float getPositionAccelerationX() {
		return (0);
	}

	@Override
	public float getPositionAccelerationY() {
		return (0);
	}

	@Override
	public float getPositionAccelerationZ() {
		return (0);
	}

	@Override
	public void setPositionX(float x) {
		this.min.setX(x);
	}

	@Override
	public void setPositionY(float y) {
		this.min.setY(y);
	}

	@Override
	public void setPositionZ(float z) {
		this.min.setZ(z);
	}

	@Override
	public void setPositionVelocityX(float vx) {
	}

	@Override
	public void setPositionVelocityY(float vy) {
	}

	@Override
	public void setPositionVelocityZ(float vz) {
	}

	@Override
	public void setPositionAccelerationX(float ax) {
	}

	@Override
	public void setPositionAccelerationY(float ay) {
	}

	@Override
	public void setPositionAccelerationZ(float az) {
	}
}
