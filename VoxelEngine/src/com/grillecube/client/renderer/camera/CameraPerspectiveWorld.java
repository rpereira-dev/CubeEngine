package com.grillecube.client.renderer.camera;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.common.Logger;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.world.terrain.WorldObjectTerrain;

public class CameraPerspectiveWorld extends CameraProjectiveWorld {

	public static final int TERRAIN_RENDER_DISTANCE = 32;
	public static final float RENDER_DISTANCE = WorldObjectTerrain.DIMX_SIZE * TERRAIN_RENDER_DISTANCE;
	public static final float FAR_DISTANCE = RENDER_DISTANCE * 1.2f;
	/** planes attributes */
	private float fov;

	private float nearDistance;
	private float farDistance;

	class CameraPlane {

		Vector3f normal;
		Vector3f point;
		float d;

		CameraPlane() {
			this.normal = new Vector3f();
			this.point = new Vector3f();
		}

		void set(Vector3f a, Vector3f b, Vector3f c) {

			Vector3f aux1 = Vector3f.sub(a, b, null);
			Vector3f aux2 = Vector3f.sub(c, b, null);
			Vector3f.cross(aux2, aux1, this.normal);
			this.normal.normalise();
			this.point.set(b);
			this.d = -(Vector3f.dot(this.normal, this.point));
		}

		public float distance(float x, float y, float z) {
			return (d + Vector3f.dot(this.normal, x, y, z));
		}

		public float distance(Vector3f point) {
			return (this.distance(point.x, point.y, point.z));
		}
	}

	private static final int PLANE_TOP = 0;
	private static final int PLANE_BOT = 1;
	private static final int PLANE_LEFT = 2;
	private static final int PLANE_RIGHT = 3;
	private static final int PLANE_NEAR = 4;
	private static final int PLANE_FAR = 5;
	private CameraPlane[] planes;

	public CameraPerspectiveWorld(GLFWWindow window) {
		super(window);
		super.setPosition(0, 0, 16);
		this.setRotX(0);
		this.setRotY(0);
		this.setRotZ(0);

		this.planes = new CameraPlane[6];
		for (int i = 0; i < this.planes.length; i++) {
			this.planes[i] = new CameraPlane();
		}

		this.setFov(70);
		this.setNearDistance(0.01f);
		this.setFarDistance(RENDER_DISTANCE);
		this.setRenderDistance(RENDER_DISTANCE);
	}

	@Override
	public Camera clone() {
		CameraPerspectiveWorld camera = new CameraPerspectiveWorld(null);
		camera.setAspect(this.getAspect());
		camera.setRot(this.getRot());
		camera.setFov(this.getFov());
		camera.setNearDistance(this.getNearDistance());
		camera.setFarDistance(this.getFarDistance());
		camera.setPosition(this.getPosition());
		camera.setRenderDistance(this.getRenderDistance());
		camera.setWorld(this.getWorld());
		return (camera);
	}

	@Override
	public void update() {
		super.update();
		this.createPlanes();
	}

	@Override
	protected void createProjectionMatrix(Matrix4f dst) {
		Matrix4f.perspective(dst, this.getAspect(), (float) Math.toRadians(this.getFov()), this.getNearDistance(),
				this.getFarDistance());
	}

	/** recalculate the frustum planes */
	Vector3f nc = new Vector3f();

	Vector3f ntl = new Vector3f();
	Vector3f ntr = new Vector3f();
	Vector3f nbl = new Vector3f();
	Vector3f nbr = new Vector3f();

	Vector3f fc = new Vector3f();

	Vector3f ftl = new Vector3f();
	Vector3f ftr = new Vector3f();
	Vector3f fbl = new Vector3f();
	Vector3f fbr = new Vector3f();

	/**
	 * a function which creates the perspective view planes represented by the
	 * camera
	 */
	private void createPlanes() {

		Vector3f up = Vector3f.AXIS_Y;
		Vector3f forward = this.getViewVector();
		Vector3f right = Vector3f.cross(forward, up, null);

		up.normalise();
		forward = forward.normalise(null);
		right.normalise();

		float tang = (float) Math.tan(Math.toRadians(this.getFov() * 0.5f));

		float nh = this.nearDistance * tang;
		float nw = nh * this.getAspect();

		float fh = this.farDistance * tang;
		float fw = fh * this.getAspect();

		nc.x = this.getPosition().x + forward.x * this.getNearDistance();
		nc.y = this.getPosition().y + forward.y * this.getNearDistance();
		nc.z = this.getPosition().z + forward.z * this.getNearDistance();

		fc.x = this.getPosition().x + forward.x * this.getFarDistance();
		fc.y = this.getPosition().y + forward.y * this.getFarDistance();
		fc.z = this.getPosition().z + forward.z * this.getFarDistance();

		// calculate rectangle planes corners (ftl = far, top, left) (nbr =
		// near, bot, right)
		// near plane corner
		ntl.x = nc.x + (up.x * nh) - (right.x * nw);
		ntl.y = nc.y + (up.y * nh) - (right.y * nw);
		ntl.z = nc.z + (up.z * nh) - (right.z * nw);

		ntr.x = nc.x + (up.x * nh) + (right.x * nw);
		ntr.y = nc.y + (up.y * nh) + (right.y * nw);
		ntr.z = nc.z + (up.z * nh) + (right.z * nw);

		nbl.x = nc.x - (up.x * nh) - (right.x * nw);
		nbl.y = nc.y - (up.y * nh) - (right.y * nw);
		nbl.z = nc.z - (up.z * nh) - (right.z * nw);

		nbr.x = nc.x - (up.x * nh) + (right.x * nw);
		nbr.y = nc.y - (up.y * nh) + (right.y * nw);
		nbr.z = nc.z - (up.z * nh) + (right.z * nw);

		// far plane corners
		ftl.x = fc.x + (up.x * fh) - (right.x * fw);
		ftl.y = fc.y + (up.y * fh) - (right.y * fw);
		ftl.z = fc.z + (up.z * fh) - (right.z * fw);

		ftr.x = fc.x + (up.x * fh) + (right.x * fw);
		ftr.y = fc.y + (up.y * fh) + (right.y * fw);
		ftr.z = fc.z + (up.z * fh) + (right.z * fw);

		fbl.x = fc.x - (up.x * fh) - (right.x * fw);
		fbl.y = fc.y - (up.y * fh) - (right.y * fw);
		fbl.z = fc.z - (up.z * fh) - (right.z * fw);

		fbr.x = fc.x - (up.x * fh) + (right.x * fw);
		fbr.y = fc.y - (up.y * fh) + (right.y * fw);
		fbr.z = fc.z - (up.z * fh) + (right.z * fw);

		// set the planes
		this.planes[PLANE_TOP].set(ntr, ntl, ftl);
		this.planes[PLANE_BOT].set(nbl, nbr, fbr);
		this.planes[PLANE_LEFT].set(ntl, nbl, fbl);
		this.planes[PLANE_RIGHT].set(nbr, ntr, fbr);
		this.planes[PLANE_NEAR].set(ntl, ntr, nbr);
		this.planes[PLANE_FAR].set(ftr, ftl, fbl);
	}

	public void setNearDistance(float f) {
		this.nearDistance = f;
	}

	public void setFarDistance(float f) {
		this.farDistance = f;
	}

	public void setFov(float f) {
		this.fov = f;
	}

	public float getNearDistance() {
		return (this.nearDistance);
	}

	public float getFarDistance() {
		return (this.farDistance);
	}

	public float getFov() {
		return (this.fov);
	}

	@Override
	public boolean isPointInFrustum(float x, float y, float z) {
		long t = System.nanoTime();
		for (int i = 0; i < 6; i++) {
			if (this.planes[i].distance(x, y, z) < 0) {
				Logger.get().log(Logger.Level.DEBUG, "out: " + (System.nanoTime() - t));
				return (false);
			}
		}
		Logger.get().log(Logger.Level.DEBUG, "in: " + (System.nanoTime() - t));
		return (true);
	}

	@Override
	public boolean isBoxInFrustum(float x, float y, float z, float sx, float sy, float sz) {
		for (int i = 0; i < 6; i++) {
			CameraPlane plane = this.planes[i];
			if (plane.distance(this.getVertexP(plane.normal, x, y, z, sx, sy, sz)) < 10) {
				return (false); // outside
			}
			if (plane.distance(this.getVertexN(plane.normal, x, y, z, sx, sy, sz)) < 0) {
				return (true); // intersect
			}
		}
		return (true); // fully inside
	}

	private Vector3f getVertexP(Vector3f normal, float x, float y, float z, float sx, float sy, float sz) {
		Vector3f res = new Vector3f(x, y, z);
		if (normal.x >= 0) {
			res.x += sx;
		}
		if (normal.y >= 0) {
			res.y += sy;
		}
		if (normal.z >= 0) {
			res.z += sz;
		}
		return (res);
	}

	private Vector3f getVertexN(Vector3f normal, float x, float y, float z, float sx, float sy, float sz) {

		Vector3f res = new Vector3f(x, y, z);
		if (normal.x <= 0) {
			res.x += sx;
		}
		if (normal.y <= 0) {
			res.y += sy;
		}
		if (normal.z <= 0) {
			res.z += sz;
		}
		return (res);
	}

	@Override
	public boolean isSphereInFrustum(Vector3f center, float radius) {

		for (int i = 0; i < 6; i++) {
			float distance = this.planes[i].distance(center);
			if (distance < -radius) {
				return (false);
			}
			if (distance < radius) {
				return (true); // intersect
			}
		}
		return (true); // inside
	}

	// UNDER HERE ARE MY OLD FASHION WAY TO CULL POINTS, MUCH SLOWER THAN THE
	// NEW
	// METHOD VIA PLANES

	// @Override
	// public boolean isBoxInFrustum(float x, float y, float z, float sx, float
	// sy, float sz) {
	// return (this.isPointInFrustum(x, y, z) || this.isPointInFrustum(x + sx,
	// y, z)
	// || this.isPointInFrustum(x, y, z + sz) || this.isPointInFrustum(x + sx,
	// y, z + sz)
	// || this.isPointInFrustum(x, y + sy, z) || this.isPointInFrustum(x + sx, y
	// + sy, z)
	// || this.isPointInFrustum(x, y + sy, z + sz) || this.isPointInFrustum(x +
	// sx, y + sy, z + sz));
	// }
	//
	// @Override
	// public boolean isSphereInFrustum(Vector3f center, float radius) {
	// return (this.isPointInFrustum(center));
	// }
	//
	// @Override
	// public boolean isPointInFrustum(float x, float y, float z) {
	//
	// // get the vector which point to the given point
	// float vx = x - this.getPosition().x;
	// float vy = y - this.getPosition().y;
	// float vz = z - this.getPosition().z;
	//
	// // get it length
	// float length = Vector3f.length(vx, vy, vz);
	//
	// // normalize the vector
	// vx /= length;
	// vy /= length;
	// vz /= length;
	//
	// double dot = vx * this.getViewVector().x + vy * this.getViewVector().y +
	// vz * this.getViewVector().z;
	// double angle = Math.toDegrees(Math.acos(dot));
	// return (angle < this.getFov() / 2);
	// }
	//
	// @Override
	// public boolean isBoxInFrustum(float x, float y, float z, float sx, float
	// sy, float sz) {
	// return (this.isPointInFrustum(x, y, z) || this.isPointInFrustum(x + sx,
	// y, z)
	// || this.isPointInFrustum(x, y, z + sz) || this.isPointInFrustum(x + sx,
	// y, z + sz)
	// || this.isPointInFrustum(x, y + sy, z) || this.isPointInFrustum(x + sx, y
	// + sy, z)
	// || this.isPointInFrustum(x, y + sy, z + sz) || this.isPointInFrustum(x +
	// sx, y + sy, z + sz));
	// }
	//
	// @Override
	// public boolean isSphereInFrustum(Vector3f center, float radius) {
	// return (this.isPointInFrustum(center));
	// }
}
