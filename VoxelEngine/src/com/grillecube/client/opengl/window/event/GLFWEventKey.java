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

public abstract class GLFWEventKey extends GLFWEvent {

	private int key;
	private int scancode;
	private int mods;

	public GLFWEventKey(GLFWWindow glfwWindow, int key, int scancode, int mods) {
		super(glfwWindow);
		this.key = key;
		this.scancode = scancode;
		this.mods = mods;
	}

	public final int getKey() {
		return (this.key);
	}

	public final int getScancode() {
		return (this.scancode);
	}

	public final int getMods() {
		return (this.mods);
	}
}
