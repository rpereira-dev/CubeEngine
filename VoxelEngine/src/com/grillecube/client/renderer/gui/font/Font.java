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

package com.grillecube.client.renderer.gui.font;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;

public class Font {
	/** opengl textureID */
	private GLTexture _texture;

	/** font data */
	private FontFile _file;

	public Font(String filepath) {
		this._texture = GLH.glhGenTexture(filepath + ".png");
		this._file = new FontFile(filepath + ".fnt");
	}

	public GLTexture getTexture() {
		return (this._texture);
	}

	public FontFile getFile() {
		return (this._file);
	}
}
