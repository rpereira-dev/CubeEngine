package com.grillecube.client.renderer.model.editor.mesher;

import com.grillecube.common.maths.Vector3f;

/** an holder for the model we are building data */
public class ModelBuildingData {

	/**
	 * the size of a single block of this model (N.B: a terrain block size is
	 * 1.0f)
	 */
	private float blockSizeUnit;

	/** the data of each blocks of this model (null if empty block) */
	private ModelBlockData[][][] modelBlocksData;

	/**
	 * the model origin (the origin coordinates, in the model referential), so
	 * when and entity is located at (x, y, z), the concrete model's block at
	 * coordinate (x, y, z) is the one at this origin
	 */
	private final Vector3f origin;

	public ModelBuildingData() {
		this.blockSizeUnit = 1.0f;
		this.modelBlocksData = new ModelBlockData[0][0][0];
		this.origin = new Vector3f(0, 0, 0);
	}

	/** @see ModelBuildingData#origin */
	public final Vector3f getOrigin() {
		return (this.origin);
	}

	/** @see ModelBuildingData#origin */
	public final void setOrigin(Vector3f origin) {
		this.setOrigin(origin.x, origin.y, origin.z);
	}

	/** @see ModelBuildingData#origin */
	public final void setOrigin(float x, float y, float z) {
		this.origin.set(x, y, z);
	}

	/**
	 * the size of a single block of this model (N.B: a terrain block size is
	 * 1.0f).
	 * 
	 * Notice that each block of a model has the same size, they are "uniforms"
	 * to make things easier for now
	 */
	public final void setBlockSizeUnit(float size) {
		this.blockSizeUnit = size;
	}

	public final float getBlockSizeUnit() {
		return (this.blockSizeUnit);
	}

	/** @see ModelBuildingData#resize(int x, int y, int z) */
	public final int getSizeX() {
		return (this.modelBlocksData.length);
	}

	/** @see ModelBuildingData#resize(int x, int y, int z) */
	public final int getSizeY() {
		return (this.modelBlocksData[0].length);
	}

	/** @see ModelBuildingData#resize(int x, int y, int z) */
	public final int getSizeZ() {
		return (this.modelBlocksData[0][0].length);
	}

	/** @see ModelBuildingData#resize(int x, int y, int z) */
	public final void resizeX(int x) {
		this.resize(x, this.getSizeY(), this.getSizeZ());
	}

	/** @see ModelBuildingData#resize(int x, int y, int z) */
	public final void resizeY(int y) {
		this.resize(this.getSizeX(), y, this.getSizeZ());
	}

	/** @see ModelBuildingData#resize(int x, int y, int z) */
	public final void resizeZ(int z) {
		this.resize(this.getSizeX(), this.getSizeY(), z);
	}

	/** resize the capacity this modle building data can hold */
	public final void resize(int x, int y, int z) {
		ModelBlockData[][][] newModelBlocksData = new ModelBlockData[x][y][z];
		int endx = x < this.getSizeX() ? x : this.getSizeX();
		int endy = y < this.getSizeY() ? y : this.getSizeY();
		int endz = z < this.getSizeZ() ? z : this.getSizeZ();

		for (int dx = 0; dx < endx; dx++) {
			for (int dy = 0; dy < endy; dy++) {
				for (int dz = 0; dz < endz; dz++) {
					newModelBlocksData[dx][dy][dz] = this.modelBlocksData[x][y][z];
				}
			}
		}
		this.modelBlocksData = newModelBlocksData;
	}

	public final ModelBlockData getBlockData(int x, int y, int z) {
		return (this.modelBlocksData[x][y][z]);
	}
}
