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

package com.grillecube.client.renderer;

import com.grillecube.common.Logger;
import com.grillecube.common.Taskable;

public abstract class Renderer implements Taskable {
	private MainRenderer _main_renderer;

	public Renderer(MainRenderer main_renderer) {
		this._main_renderer = main_renderer;
		Logger.get().log(Logger.Level.DEBUG, "instancing new " + this.getClass().getSimpleName());
	}

	public MainRenderer getParent() {
		return (this._main_renderer);
	}

	/** call on initialization : load your shaders / and buffer heres */
	public abstract void initialize();

	public abstract void deinitialize();

	/** called right before the world is rendered on each frames */
	public abstract void preRender();

	/** main rendering function : draw your stuff here */
	public abstract void render();

	/** called right after the world is rendered on each frames */
	public abstract void postRender();
}
