package com.grillecube.client.renderer.world;

import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector4f;

public interface MVPObject
{
	public Matrix4f getTransformationMatrix();

	public Vector4f getColor();

	public void render();
}
