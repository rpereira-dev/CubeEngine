/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.opengl.window.event;

import com.grillecube.client.opengl.window.GLFWWindow;

public class GLFWEventKeyRelease extends GLFWEventKey {

	public GLFWEventKeyRelease(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		super(glfwWindow, key, scancode, mods);
	}
}
