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

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLTexture;
import com.grillecube.client.opengl.object.ImageUtils;

public class ModelSkin {

	public static final int DEFAULT_PIXELS_PER_LINE = 2;

	/** the skin name */
	private String name;

	/** the skin filepath */
	private String filepath;

	/** the gl texture object */
	private GLTexture glTexture;

	private int pixelPerLine;

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
		this.pixelPerLine = DEFAULT_PIXELS_PER_LINE;
		this.load();
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

	public final void setPixelsPerLine(int pixelPerLine) {
		if (this.getGLTexture() != null) {
			BufferedImage prevImg = this.getGLTexture().getData();
			float ratio = pixelPerLine / (float) this.pixelPerLine;
			int width = (int) (prevImg.getWidth() * ratio);
			int height = (int) (prevImg.getHeight() * ratio);
			Image img = prevImg.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
			this.getGLTexture().setData(img, width, height);
		}
		this.pixelPerLine = pixelPerLine;
	}

	public final int getPixelsPerLine() {
		return (this.pixelPerLine);
	}
}
