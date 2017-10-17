package com.grillecube.client.renderer.camera;

import com.grillecube.common.maths.Vector3i;

public interface RaycastingCallback {
	/** return true if the raytracing should stop */
	public boolean onRaycastCoordinates(int x, int y, int z, Vector3i face);

}