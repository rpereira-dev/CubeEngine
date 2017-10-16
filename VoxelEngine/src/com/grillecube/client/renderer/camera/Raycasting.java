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

package com.grillecube.client.renderer.camera;

import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;

public class Raycasting {
	/**
	 * From "A Fast Voxel Traversal Algorithm for Ray Tracing" by John
	 * Amanatides and Andrew Woo, 1987
	 * 
	 * @param origin
	 * @param dir
	 * @param radius
	 * @param callback
	 * @return the face normal of collision
	 */
	public static Vector3f raycast(Vector3f origin, Vector3f dir, float radius, RaycastingCallback callback) {
		return raycast(origin, dir, radius, radius, radius, 1.0f, 1.0f, 1.0f, callback);
	}

	public static Vector3f raycast(Vector3f origin, Vector3f dir, float radius, float scale,
			RaycastingCallback callback) {
		return raycast(origin, dir, radius, radius, radius, scale, scale, scale, callback);
	}

	/**
	 * Launch a ray, and callback touched blocks
	 * 
	 * @param origin
	 *            : origin of the raycast
	 * @param dir
	 *            : direction of the raycast (no need to normalize it)
	 * @param rx
	 * @param ry
	 * @param rz
	 * @param sx
	 * @param sy
	 * @param sz
	 * @param callback
	 * @return
	 */
	public static Vector3f raycast(Vector3f origin, Vector3f dir, float rx, float ry, float rz, float sx, float sy,
			float sz, RaycastingCallback callback) {

		if (dir.lengthSquared() < 0.001f) {
			return (null);
		}

		float ox = Maths.floor(origin.x, sx);
		float oy = Maths.floor(origin.y, sy);
		float oz = Maths.floor(origin.z, sz);
		float x = ox;
		float y = oy;
		float z = oz;
		int nx = 1;
		int ny = 1;
		int nz = 1;

		float stepX = Maths.sign(dir.x) * sx;
		float stepY = Maths.sign(dir.y) * sy;
		float stepZ = Maths.sign(dir.z) * sz;

		float scaleX = 1.0f / sx;
		float scaleY = 1.0f / sy;
		float scaleZ = 1.0f / sz;

		float maxX = Maths.intbound(origin.x, dir.x);
		float maxY = Maths.intbound(origin.y, dir.y);
		float maxZ = Maths.intbound(origin.z, dir.z);

		float dx = stepX / dir.x;
		float dy = stepY / dir.y;
		float dz = stepZ / dir.z;

		Vector3f face = new Vector3f();

		while (true) {
			if (callback.onRaycastCoordinates((int) (x * scaleX), (int) (y * scaleY), (int) (z * scaleZ), face)) {
				return (face);
			}

			if (maxX < maxY) {
				if (maxX < maxZ) {
					if (Maths.abs(maxX) > rx) {
						break;
					}
					x = ox + nx++ * stepX;
					maxX += dx;
					face.x = -stepX;
					face.y = 0;
					face.z = 0;
				} else {
					if (Maths.abs(maxZ) > rz) {
						break;
					}
					z = oz + nz++ * stepZ;
					maxZ += dz;
					face.x = 0;
					face.y = 0;
					face.z = -stepZ;
				}
			} else {
				if (maxY < maxZ) {
					if (Maths.abs(maxY) > ry) {
						break;
					}
					y = oy + ny++ * stepY;
					maxY += dy;
					face.x = 0;
					face.y = -stepY;
					face.z = 0;
				} else {
					if (Maths.abs(maxZ) > rz) {
						break;
					}
					z = oz + nz++ * stepZ;
					maxZ += dz;
					face.x = 0;
					face.y = 0;
					face.z = -stepZ;
				}
			}
		}
		return (null);
	}
}