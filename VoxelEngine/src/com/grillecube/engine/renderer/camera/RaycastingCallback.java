package com.grillecube.engine.renderer.camera;

import com.grillecube.engine.maths.Vector3f;

public interface RaycastingCallback
{
	/** return true if the raytracing should stop */
	public boolean onRaycastCoordinates(float x, float y, float z, Vector3f face);

}