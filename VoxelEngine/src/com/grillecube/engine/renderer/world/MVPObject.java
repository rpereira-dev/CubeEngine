package com.grillecube.engine.renderer.world;

import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.maths.Vector4f;

public interface MVPObject
{
	public Matrix4f getTransformationMatrix();

	public Vector4f getColor();

	public void render();
}
