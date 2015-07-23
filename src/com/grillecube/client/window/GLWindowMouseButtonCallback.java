package com.grillecube.client.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class GLWindowMouseButtonCallback extends GLFWMouseButtonCallback
{
	public static boolean	MOUSE_IS_PRESSED = false;
	
	private GLWindow	_window;
	
	public GLWindowMouseButtonCallback(GLWindow w)
	{
		this._window = w;
	}
		
	@Override
	public void invoke(long window, int button, int action, int mods)
	{
		MOUSE_IS_PRESSED = (button == 0 && action == GLFW.GLFW_PRESS);
	}
}