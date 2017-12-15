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
import com.grillecube.client.opengl.GLTexture;
import com.grillecube.client.opengl.ImageUtils;

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
		this.glTexture = GLH.glhGenTexture();
		this.load();
	}

	public void setName(String name) {
		this.name = name;
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
		this.glTexture.bind(texture, target);
	}

	public final GLTexture getGLTexture() {
		return (this.glTexture);
	}

	public final void save() {
		ImageUtils.exportPNGImage(this.getFilepath(), this.getGLTexture().getData());
	}

	public final void load() {
		this.glTexture.setData(ImageUtils.readImage(this.getFilepath()));
	}
}
