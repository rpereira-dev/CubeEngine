package com.grillecube.client.renderer.camera;

import com.grillecube.common.maths.Vector3f;

public interface RaycastingCallback {
	/** return true if the raytracing should stop */
	public boolean onRaycastCoordinates(float x, float y, float z, Vector3f face);

}