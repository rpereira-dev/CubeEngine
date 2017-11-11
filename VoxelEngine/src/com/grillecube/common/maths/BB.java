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

package com.grillecube.common.maths;

/**
 * Abstract class for bounding boxes
 * 
 * @author Romain
 *
 */
public abstract class BB {

	public BB() {
	}

	/** return the area of the bounding box */
	public abstract float getArea();

	/**
	 * @param point:
	 *            point to test
	 * @return true if the point is in the box
	 */
	public final boolean contains(Vector3f point) {
		return (this.contains(point.x, point.y, point.z));
	}

	/**
	 * @param x, y, z:
	 *            point to test
	 * @return true if the point is in the box
	 */
	public abstract boolean contains(float x, float y, float z);

	/** contains version for a sphere */
	public abstract boolean contains(Vector3f center, float radius);

	public abstract Vector3f getCenter();
}
