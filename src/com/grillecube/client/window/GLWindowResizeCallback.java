package com.grillecube.client.window;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

public class GLWindowResizeCallback extends GLFWWindowSizeCallback
{
	private	GLWindow	_window;
	
	public GLWindowResizeCallback(GLWindow window)
	{
		this._window = window;
	}
	
	@Override
	public void invoke(long window, int width, int height)
	{
		this._window.resize(width, height);
	}
}
