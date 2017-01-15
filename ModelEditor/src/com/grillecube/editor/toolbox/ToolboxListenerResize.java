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

package com.grillecube.editor.toolbox;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ToolboxListenerResize implements ComponentListener {
	private Toolbox _toolbox;

	public ToolboxListenerResize(Toolbox toolbox) {
		this._toolbox = toolbox;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		this._toolbox.resize();
		this._toolbox.repaint();
	}

	@Override
	public void componentMoved(ComponentEvent paramComponentEvent) {
	}

	@Override
	public void componentShown(ComponentEvent paramComponentEvent) {
	}

	@Override
	public void componentHidden(ComponentEvent paramComponentEvent) {
	}
}
