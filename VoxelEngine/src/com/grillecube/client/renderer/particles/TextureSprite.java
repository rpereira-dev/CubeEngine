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

package com.grillecube.client.renderer.particles;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.GLTexture;

/** there should be only one instance per texture of this object */
public class TextureSprite {
	private GLTexture texture;

	private int cols;
	private int lines;
	private int maxID;

	/**
	 * particle constructor which generate a gl texture name, so it should be
	 * call in a gl context!
	 */
	public TextureSprite(String filepath, int cols, int lines) {
		this.texture = GLH.glhGenTexture(filepath);
		this.cols = cols;
		this.lines = lines;
		this.maxID = cols * lines;
	}

	/** return the number of cols for this sprite */
	public int getCols() {
		return (this.cols);
	}

	/** return number of lines for this sprite */
	public int getLines() {
		return (this.lines);
	}

	/** return opengl texture id */
	public GLTexture getTexture() {
		return (this.texture);
	}

	public int getMaxID() {
		return (this.maxID);
	}
}
