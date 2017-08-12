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

package com.grillecube.client.renderer.model;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;

public class ModelSkin {

	/** the skin name */
	private String name;

	/** the skin filepath */
	private String filepath;

	/** the gl texture object */
	private GLTexture glTexture;

	public ModelSkin() {
		this(null);
	}

	public ModelSkin(String name) {
		this(name, null);
	}

	public ModelSkin(String name, String filepath) {
		this.name = name;
		this.filepath = filepath;
	}

	public String getName() {
		return (this.name);
	}

	public String getFilepath() {
		return (this.filepath);
	}

	@Override
	public String toString() {
		return (this.getFilepath());
	}

	public void bind(int texture, int target) {
		if (this.glTexture == null) {
			// TODO : clean it when unused
			if (this.getFilepath() == null) {
				return;
			}
			this.glTexture = GLH.glhGenTexture(this.getFilepath());
		}
		this.glTexture.bind(texture, target);
	}
}
