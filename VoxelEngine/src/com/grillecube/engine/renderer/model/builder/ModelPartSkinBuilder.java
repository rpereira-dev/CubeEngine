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

package com.grillecube.engine.renderer.model.builder;

import java.util.Arrays;

import com.grillecube.engine.renderer.model.ModelPartSkin;

public class ModelPartSkinBuilder extends ModelPartSkin {

	public static final int COLOR_UNSET = 0x42000000;

	private int[] _colors;

	public ModelPartSkinBuilder() {
		super();
		this._colors = new int[ModelPartBuilder.DIM_X * ModelPartBuilder.DIM_Y * ModelPartBuilder.DIM_Z];
		Arrays.fill(this._colors, COLOR_UNSET);
	}

	public boolean setColor(int x, int y, int z, int color) {
		x = x + ModelPartBuilder.DIM_X / 2;
		y = y + ModelPartBuilder.DIM_Y / 2;
		z = z + ModelPartBuilder.DIM_Z / 2;
		if (x >= ModelPartBuilder.DIM_X || y >= ModelPartBuilder.DIM_Y || z >= ModelPartBuilder.DIM_Z) {
			return (false);
		}
		int index = ModelPartBuilder.getIndex(x, y, z, ModelPartBuilder.DIM_X, ModelPartBuilder.DIM_Y);
		if (this._colors[index] == color) {
			return (false);
		}
		this._colors[index] = color;
		return (true);
	}

	public int getColor(int x, int y, int z) {
		x = x + ModelPartBuilder.DIM_X / 2;
		y = y + ModelPartBuilder.DIM_Y / 2;
		z = z + ModelPartBuilder.DIM_Z / 2;
		int index = ModelPartBuilder.getIndex(x, y, z, ModelPartBuilder.DIM_X, ModelPartBuilder.DIM_Y);
		return (this._colors[index]);
	}

	// TODO optimize this if export take too long
	public int[] getBlocks() {
		int count = 0;
		for (int x = 0; x < ModelPartBuilder.DIM_X; x++) {
			for (int y = 0; y < ModelPartBuilder.DIM_Y; y++) {
				for (int z = 0; z < ModelPartBuilder.DIM_Z; z++) {
					int index = ModelPartBuilder.getIndex(x, y, z, ModelPartBuilder.DIM_X, ModelPartBuilder.DIM_Y);
					if (this._colors[index] != COLOR_UNSET) {
						count++;
					}
				}
			}
		}

		int[] array = new int[count * 7];
		int i = 0;
		for (int x = 0; x < ModelPartBuilder.DIM_X; x++) {
			for (int y = 0; y < ModelPartBuilder.DIM_Y; y++) {
				for (int z = 0; z < ModelPartBuilder.DIM_Z; z++) {
					int index = ModelPartBuilder.getIndex(x, y, z, ModelPartBuilder.DIM_X, ModelPartBuilder.DIM_Y);
					int color = this._colors[index];
					if (color != COLOR_UNSET) {
						array[i++] = x;
						array[i++] = y;
						array[i++] = z;
						array[i++] = ColorInt.getRed(color);
						array[i++] = ColorInt.getGreen(color);
						array[i++] = ColorInt.getBlue(color);
						array[i++] = ColorInt.getAlpha(color);
					}
				}
			}
		}

		return (array);
	}

	public void setBlocks(int[] colors) {

		Arrays.fill(this._colors, COLOR_UNSET);
		for (int i = 0; i < colors.length;) {
			int x = colors[i++];
			int y = colors[i++];
			int z = colors[i++];
			int r = colors[i++];
			int g = colors[i++];
			int b = colors[i++];
			int a = colors[i++];
			int color = ColorInt.get(r, g, b, a);
			int index = ModelPartBuilder.getIndex(x, y, z, ModelPartBuilder.DIM_X, ModelPartBuilder.DIM_Y);
			this._colors[index] = color;
		}
	}

	public void setRawColors(int[] colors) {
		this._colors = colors;
	}

	public int[] getRawColors() {
		return (this._colors);
	}

	public boolean isBlockSet(int x, int y, int z) {
		return (this.getColor(x, y, z) != ModelPartSkinBuilder.COLOR_UNSET);
	}

	public int[] copyRawBlocks() {
		int length = this._colors.length;
		int[] dst = new int[length];
		System.arraycopy(this._colors, 0, dst, 0, length);
		return (dst);
	}
}
