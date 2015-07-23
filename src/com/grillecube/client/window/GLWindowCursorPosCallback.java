package com.grillecube.client.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import com.grillecube.client.renderer.Camera;

public class GLWindowCursorPosCallback extends GLFWCursorPosCallback
{
	private static float	_MOUSE_SPEED = 0.004f;
	
	private static double	_mouseX;
	private static double	_mouseY;
	
	private static double	_prevX = 0.5D;
	private static double	_prevY = 0.5D;

	private GLWindow	_window;
	
	public GLWindowCursorPosCallback(GLWindow w)
	{
		this._window = w;
	}
	
	@Override
	public void invoke(long window, double xpos, double ypos)
	{		
		_mouseX = xpos;
		_mouseY = ypos;
		
		if (GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED)
		{
			this.moveCamera(this._window.getCamera());
		}
		
		_prevX = xpos;
		_prevY = ypos;
	}


	private void moveCamera(Camera camera)
	{
		camera.setYaw((float)(camera.getYaw() + (_mouseX - _prevX) * _MOUSE_SPEED));
		camera.setPitch((float)(camera.getPitch() + (_mouseY - _prevY) * _MOUSE_SPEED));	
	}

}
