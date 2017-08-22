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

public class GLFWEventMouseScroll extends GLFWEventMouse {

	private final double xpos;
	private final double ypos;

	public GLFWEventMouseScroll(GLFWWindow window, double xpos, double ypos) {
		super(window);
		this.xpos = xpos;
		this.ypos = ypos;
	}

	public final double getScrollX() {
		return (this.xpos);
	}

	public final double getScrollY() {
		return (this.ypos);
	}
}
