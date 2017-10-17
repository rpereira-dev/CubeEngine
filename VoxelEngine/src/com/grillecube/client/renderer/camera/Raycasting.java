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
import com.grillecube.common.maths.Vector3i;

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
	public static Vector3i raycast(Vector3f origin, Vector3f dir, float radius, RaycastingCallback callback) {
		return raycast(origin, dir, radius, radius, radius, callback);
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
	public static Vector3i raycast(Vector3f origin, Vector3f dir, float rx, float ry, float rz,
			RaycastingCallback callback) {
		return (raycast(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, rx, ry, rz, callback));
	}

	public static Vector3i raycast(float originx, float originy, float originz, float dirx, float diry, float dirz,
			float rx, float ry, float rz, RaycastingCallback callback) {

		if (dirx * dirx + diry * diry + dirz * dirz < 0.00001f) {
			return (null);
		}

		int ox = Maths.floor(originx);
		int oy = Maths.floor(originy);
		int oz = Maths.floor(originz);
		int x = ox;
		int y = oy;
		int z = oz;

		int stepX = Maths.sign(dirx);
		int stepY = Maths.sign(diry);
		int stepZ = Maths.sign(dirz);

		float maxX = Maths.intbound(originx, dirx);
		float maxY = Maths.intbound(originy, diry);
		float maxZ = Maths.intbound(originz, dirz);

		float dx = stepX / dirx;
		float dy = stepY / diry;
		float dz = stepZ / dirz;

		Vector3i face = new Vector3i();

		while (true) {
			if (callback.onRaycastCoordinates(x, y, z, face)) {
				return (face);
			}

			if (maxX < maxY) {
				if (maxX < maxZ) {
					if (Maths.abs(maxX) > rx) {
						break;
					}
					x += stepX;
					maxX += dx;
					face.x = -stepX;
					face.y = 0;
					face.z = 0;
				} else {
					if (Maths.abs(maxZ) > rz) {
						break;
					}
					z += stepZ;
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
					y += stepY;
					maxY += dy;
					face.x = 0;
					face.y = -stepY;
					face.z = 0;
				} else {
					if (Maths.abs(maxZ) > rz) {
						break;
					}
					z += stepZ;
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