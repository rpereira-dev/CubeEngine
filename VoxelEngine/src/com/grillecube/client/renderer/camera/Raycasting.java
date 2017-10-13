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
		return raycast(origin, dir, radius, radius, radius, callback);
	}

	public static Vector3f raycast(Vector3f origin, Vector3f dir, float rx, float ry, float rz,
			RaycastingCallback callback) {

		if (dir.lengthSquared() < 0.001f) {
			return (null);
		}

		float ox = (float) Math.floor(origin.x);
		float oy = (float) Math.floor(origin.y);
		float oz = (float) Math.floor(origin.z);
		float x = ox;
		float y = oy;
		float z = oz;
		int nx = 1;
		int ny = 1;
		int nz = 1;
		
		float dirx = dir.x;
		float diry = dir.y;
		float dirz = dir.z;

		float stepX = Maths.sign(dirx);
		float stepY = Maths.sign(diry);
		float stepZ = Maths.sign(dirz);

		float maxX = Maths.intbound(origin.x, dirx);
		float maxY = Maths.intbound(origin.y, diry);
		float maxZ = Maths.intbound(origin.z, dirz);

		float dx = stepX / dirx;
		float dy = stepY / diry;
		float dz = stepZ / dirz;

		Vector3f face = new Vector3f();

		while (true) {
			if (callback.onRaycastCoordinates(x, y, z, face)) {
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