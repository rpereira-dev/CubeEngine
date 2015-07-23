package com.grillecube.client.window;

import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.Camera;

public class GLWindowScrollCallback extends GLFWScrollCallback
{
	private GLWindow	_window;
	
	public GLWindowScrollCallback(GLWindow w)
	{
		this._window = w;
	}
	
	@Override
	public void invoke(long window, double xoffset, double yoffset)
	{
		Camera		camera;
		Vector3f	vec;
		float		speed;
		
		camera = this._window.getCamera();
		vec = camera.getLookVec();
		if (yoffset < 0)
		{
			vec.x = -vec.x;
			vec.y = -vec.y;
			vec.z = -vec.z;
		}
		speed = 1;
		vec.x *= speed;
		vec.y *= speed;
		vec.z *= speed;
		camera.move(vec);
	}

}
