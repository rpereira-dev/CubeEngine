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

package com.grillecube.client.renderer.model.builder;

import com.grillecube.client.renderer.model.ModelPart;
import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;
import com.grillecube.common.maths.Vector3f;

public class ModelPartBuilder extends ModelPart {

	public static final int DIM_X = 64; // 16 is 1 block
	public static final int DIM_Y = 64;
	public static final int DIM_Z = 64;

	public static final int MAX_X = DIM_X / 2;
	public static final int MAX_Y = DIM_Y / 2;
	public static final int MAX_Z = DIM_Z / 2;

	public static final int MIN_X = -DIM_X / 2;
	public static final int MIN_Y = -DIM_Y / 2;
	public static final int MIN_Z = -DIM_Z / 2;

	private boolean[] _blocks;
	private int _block_count;
	private int _dimx, _dimy, _dimz;

	public ModelPartBuilder() {
		this("unknown");
	}

	public ModelPartBuilder(String name) {
		super(name);

		this._dimx = ModelPartBuilder.DIM_X;
		this._dimy = ModelPartBuilder.DIM_Y;
		this._dimz = ModelPartBuilder.DIM_Z;
		this._blocks = new boolean[this._dimx * this._dimy * this._dimz];

		long t = System.currentTimeMillis() * System.nanoTime();
		float r = ((t >> 0) & 0xFF) / 255.0f;
		float g = ((t >> 8) & 0xFF) / 255.0f;
		float b = ((t >> 16) & 0xFF) / 255.0f;
		float a = 1.0f;
		super.getBoundingBox().getColor().set(r, g, b, a);
	}

	public int getBlockDimX() {
		return (this._dimx);
	}

	public int getBlockDimY() {
		return (this._dimy);
	}

	public int getBlockDimZ() {
		return (this._dimz);
	}

	public void setBlockDimX(int dimx) {
		this.setBlockDim(dimx, this.getBlockDimY(), this.getBlockDimZ());
	}

	public void setBlockDimY(int dimy) {
		this.setBlockDim(this.getBlockDimX(), dimy, this.getBlockDimZ());
	}

	public void setBlockDimZ(int dimz) {
		this.setBlockDim(this.getBlockDimX(), this.getBlockDimY(), dimz);
	}

	public void setBlockDim(int dimx, int dimy, int dimz) {
		boolean[] new_blocks = new boolean[dimx * dimy * dimz];
		int current_dimx = this.getBlockDimX();
		int current_dimy = this.getBlockDimY();
		int current_dimz = this.getBlockDimZ();
		int endx = dimx < current_dimx ? dimx : current_dimx;
		int endy = dimy < current_dimy ? dimy : current_dimy;
		int endz = dimz < current_dimz ? dimz : current_dimz;
		int x, y, z;
		for (x = 0; x < endx; x++) {
			for (y = 0; y < endy; y++) {
				for (z = 0; z < endz; z++) {
					int nindex = getIndex(x, y, z, dimx, dimy);
					int oldindex = getIndex(x, y, z, this._dimx, this._dimy);
					new_blocks[nindex] = this._blocks[oldindex];
				}
			}
		}
		this._blocks = new_blocks;
	}

	public void translateBlocksX(int tx) {

		int dimx, dimy, dimz;

		dimx = this.getBlockDimX();
		dimy = this.getBlockDimY();
		dimz = this.getBlockDimZ();

		int startx, endx, stepx;

		if (tx > 0) {
			// e.g, tx = 4, dimx = 16
			startx = dimx - tx - 1; // from block 11
			endx = 0; // to 0
			stepx = -1; // step -1
		} else {
			// e.g, tx = -4, dimx = 16
			startx = -tx; // from block 4
			endx = dimx - 1; // to 15
			stepx = 1;
		}

		int x, y, z;

		for (x = startx; x != endx; x += stepx) {
			for (y = 0; y < dimy; y++) {
				for (z = 0; z < dimz; z++) {
					int nindex = getIndex(x + tx, y, z, this._dimx, this._dimy);
					int oldindex = getIndex(x, y, z, this._dimx, this._dimy);
					this._blocks[nindex] = this._blocks[oldindex];
				}
			}
		}
	}

	public void translateBlocksY(int ty) {

		int dimx, dimy, dimz;

		dimx = this.getBlockDimX();
		dimy = this.getBlockDimY();
		dimz = this.getBlockDimZ();

		int starty, endy, stepy;

		if (ty > 0) {
			starty = dimy - ty - 1;
			endy = 0;
			stepy = -1;
		} else {
			starty = -ty;
			endy = dimy - 1;
			stepy = 1;
		}

		int x, y, z;

		for (x = 0; x < dimx; x++) {
			for (y = starty; y != endy; y += stepy) {
				for (z = 0; z < dimz; z++) {
					int nindex = getIndex(x, y + ty, z, this._dimx, this._dimy);
					int oldindex = getIndex(x, y, z, this._dimx, this._dimy);
					this._blocks[nindex] = this._blocks[oldindex];
				}
			}
		}
	}

	public void translateBlocksZ(int tz) {

		int dimx, dimy, dimz;

		dimx = this.getBlockDimX();
		dimy = this.getBlockDimY();
		dimz = this.getBlockDimZ();

		int startz, endz, stepz;

		if (tz > 0) {
			startz = dimy - tz - 1;
			endz = 0;
			stepz = -1;
		} else {
			startz = -tz;
			endz = dimz - 1;
			stepz = 1;
		}

		int x, y, z;

		for (x = 0; x < dimx; x++) {
			for (y = 0; y < dimy; y++) {
				for (z = startz; z != endz; z += stepz) {
					int nindex = getIndex(x, y, z + tz, this._dimx, this._dimy);
					int oldindex = getIndex(x, y, z, this._dimx, this._dimy);
					this._blocks[nindex] = this._blocks[oldindex];
				}
			}
		}
	}

	public boolean setBlock(int x, int y, int z) {
		return (this.setBlock(x, y, z, true));
	}

	public boolean unsetBlock(int x, int y, int z) {
		return (this.setBlock(x, y, z, false));
	}

	/** set a block, return true if the model changed */
	public boolean setBlock(int x, int y, int z, boolean value) {
		x = x + ModelPartBuilder.DIM_X / 2;
		y = y + ModelPartBuilder.DIM_Y / 2;
		z = z + ModelPartBuilder.DIM_Z / 2;
		if (x < 0 || x >= ModelPartBuilder.DIM_X || y < 0 || y >= ModelPartBuilder.DIM_Y || z < 0
				|| z >= ModelPartBuilder.DIM_Z) {
			return (false);
		}
		int index = getIndex(x, y, z, this._dimx, this._dimy);
		if (this._blocks[index] == value) {
			return (false);
		}
		this._blocks[index] = value;
		this._block_count += (value) ? 1 : -1;
		return (true);
	}

	public boolean[] getRawBlock() {
		return (this._blocks);
	}

	public boolean isBlockSet(int x, int y, int z) {
		x = x + ModelPartBuilder.DIM_X / 2;
		y = y + ModelPartBuilder.DIM_Y / 2;
		z = z + ModelPartBuilder.DIM_Z / 2;

		if (x < 0 || y < 0 || z < 0 || x >= ModelPartBuilder.DIM_X || y >= ModelPartBuilder.DIM_Y
				|| z >= ModelPartBuilder.DIM_Z) {
			return (false);
		}
		int index = getIndex(x, y, z, this._dimx, this._dimy);
		return (this._blocks[index]);
	}

	public void setBlocks(int[] coordinates) {
		Logger.get().log(Level.DEBUG, "Setting ModelPartBuilder blocks: " + coordinates.length / 3 + " blocks");
		int i = 0;
		while (i < coordinates.length) {
			int x = coordinates[i++];
			int y = coordinates[i++];
			int z = coordinates[i++];
			this.setBlock(x, y, z, true);
		}
	}

	// public void addSkin(String name, int[] coordinates, int[] rgba) {
	// ModelPartSkinBuilder skin = new ModelPartSkinBuilder(name);
	// this.addSkin(skin);
	// int i = 0;
	// int j = 0;
	// while (i < coordinates.length && j < rgba.length) {
	// int x = coordinates[i++];
	// int y = coordinates[i++];
	// int z = coordinates[i++];
	// int r = rgba[j++];
	// int g = rgba[j++];
	// int b = rgba[j++];
	// int a = rgba[j++];
	// skin.setColor(x, y, z, ColorInt.get(r, g, b, a));
	// }
	// }

	public int[] getBlocksCoordinates() {
		int[] array = new int[this._block_count * 3];
		int i = 0;
		for (int x = 0; x < ModelPartBuilder.DIM_X; x++) {
			for (int y = 0; y < ModelPartBuilder.DIM_Y; y++) {
				for (int z = 0; z < ModelPartBuilder.DIM_Z; z++) {
					int index = getIndex(x, y, z, this._dimx, this._dimy);
					if (this._blocks[index]) {
						array[i++] = x - ModelPartBuilder.DIM_X / 2;
						array[i++] = y - ModelPartBuilder.DIM_Y / 2;
						array[i++] = z - ModelPartBuilder.DIM_Z / 2;
					}
				}
			}
		}

		return (array);
	}

	public void updateBox() {
		Vector3f max = new Vector3f(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		Vector3f min = new Vector3f(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

		for (int x = ModelPartBuilder.MIN_X; x < ModelPartBuilder.MAX_X; x++) {
			for (int y = ModelPartBuilder.MIN_Y; y < ModelPartBuilder.MAX_Y; y++) {
				for (int z = ModelPartBuilder.MIN_Z; z < ModelPartBuilder.MAX_Z; z++) {
					if (this.isBlockSet(x, y, z)) {
						if (x <= min.getX()) {
							min.setX(x);
						}
						if (y <= min.getY()) {
							min.setY(y);
						}
						if (z <= min.getZ()) {
							min.setZ(z);
						}

						if (x >= max.getX()) {
							max.setX(x + 1);
						}
						if (y >= max.getY()) {
							max.setY(y + 1);
						}
						if (z >= max.getZ()) {
							max.setZ(z + 1);
						}
					}
				}
			}
		}
		Vector3f size = Vector3f.sub(max, min, new Vector3f());
		min.scale(super.getBlockScale());
		size.scale(super.getBlockScale());
		this.getBoundingBox().setMinSize(min, size);
	}

	@Override
	public String toString() {
		return (this.getName());
	}

	public boolean[] copyRawBlocks() {
		int length = this._blocks.length;
		boolean[] dst = new boolean[length];
		System.arraycopy(this._blocks, 0, dst, 0, length);
		return (dst);
	}

	public void setRawBlocks(boolean[] blocks) {
		this._blocks = blocks;
		this.updateBlockCount();
	}

	public static int getIndex(int x, int y, int z, int dimx, int dimy) {
		return (x + dimx * (y + dimy * z));
	}

	private void updateBlockCount() {
		this._block_count = 0;
		int i;
		for (i = 0; i < this._blocks.length; i++) {
			if (this._blocks[i]) {
				this._block_count++;
			}
		}
	}
}
