package com.grillecube.client.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class GLWindowKeyCallback extends GLFWKeyCallback
{
	private GLWindow	_window;
	
	public GLWindowKeyCallback(GLWindow w)
	{
		this._window = w;
	}
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods)
	{
		if (key == GLFW.GLFW_KEY_ESCAPE)
		{
			GLFW.glfwSetWindowShouldClose(window, 1);
		}
		else if (key == GLFW.GLFW_KEY_T && action == GLFW.GLFW_PRESS)
		{
			this.toggleMouse(window);
		}
		else if (action == GLFW.GLFW_RELEASE)
		{
			this._window.getCamera().onKeyReleased(key);
		}
		else if (action == GLFW.GLFW_PRESS)
		{
			this._window.getCamera().onKeyPressed(key);
		}
	}

	private void toggleMouse(long window)
	{
		if (GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED)
		{
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
		else
		{
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		}
		GLFW.glfwSetCursorPos(window, (int)(this._window.getWidth() / 2), (int)(this._window.getHeight() / 2));
	}

}
