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

public class GLFWEventMousePress extends GLFWEventMouse {

	private final int button;
	private final int mods;

	public GLFWEventMousePress(GLFWWindow window, int button, int mods) {
		super(window);
		this.button = button;
		this.mods = mods;
	}

	public final int getButton() {
		return (this.button);
	}

	public final int getMods() {
		return (this.mods);
	}
}
