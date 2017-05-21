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

public abstract class Camera {

	private int _state;

	/** aspect_ratio is WIDTH / HEIGHT */
	public Camera() {
		this._state = 0;
	}

	public boolean hasState(int state) {
		return ((this._state & state) == state);
	}

	public void setState(int state) {
		this._state = this._state | state;
	}

	public void unsetState(int state) {
		this._state = this._state & ~(state);
	}

	public void switchState(int state) {
		this._state = this._state ^ (state);
	}

	public void resetState() {
		this._state = 0;
	}

	public void update() {
		
	}
	
	public abstract Camera clone();

	
//	/** return true if the given point is inside the frustum of the camera */
//	public boolean isInFrustum(float x, float y, float z, float imprecision) {
//
//		// get the vector which point to the given point
//		float vx = x - this._pos.x;
//		float vy = y - this._pos.y;
//		float vz = z - this._pos.z;
//
//		// get it length
//		float length = Vector3f.length(vx, vy, vz);
//
//		// normalize the vector
//		vx /= length;
//		vy /= length;
//		vz /= length;
//
//		double dot = vx * this._look_vec.x + vy * this._look_vec.y + vz * this._look_vec.z;
//		double angle = Math.toDegrees(Math.acos(dot));
//		return (angle < (this._fov + imprecision) / 2);
//	}
//
//	public boolean isInFrustum(Vector3f point, float imprecision) {
//		return (this.isInFrustum(point.x, point.y, point.z));
//	}
//
//	public boolean isInFrustum(float x, float y, float z) {
//		return (this.isInFrustum(x, y, z, 0));
//	}
//	
//	public boolean isInFrustum(Vector3f point) {
//		return (this.isInFrustum(point.x, point.y, point.z));
//	}
//
//	/**
//	 * return true if the box of center "center" with dimension x, y, z is in
//	 * camera frustum
//	 */
//	public boolean isBoxInFrustum(Vector3f center, float x, float y, float z) {
//		return (this.isInFrustum(center.x + x, center.y + y, center.z + z)
//				|| this.isInFrustum(center.x - x, center.y + y, center.z + z)
//				|| this.isInFrustum(center.x + x, center.y + y, center.z - z)
//				|| this.isInFrustum(center.x - x, center.y + y, center.z - z)
//				|| this.isInFrustum(center.x + x, center.y - y, center.z + z)
//				|| this.isInFrustum(center.x - x, center.y - y, center.z + z)
//				|| this.isInFrustum(center.x + x, center.y - y, center.z - z)
//				|| this.isInFrustum(center.x - x, center.y - y, center.z - z));
//	}
//
//	public boolean isBoxInFrustum(Vector3f center, float demisize) {
//		return (this.isBoxInFrustum(center, demisize, demisize, demisize));
//	}
//	
//	public boolean isBoxInFrustum(Vector3f center, Vector3f demisize) {
//		return (this.isBoxInFrustum(center, demisize.x, demisize.y, demisize.z));
//	}
//
//	public boolean isBoxInFrustum(BoundingBox box) {
//		return (this.isBoxInFrustum(box.getCenter(), box.getSize().x / 2.0f, box.getSize().y / 2.0f, box.getSize().z / 2.0f));
//	}
}