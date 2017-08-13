package com.grillecube.client.renderer.model.editor.mesher;

/** hold the data of a single block of the model */
public class BlockData {

	private final int x;
	private final int y;
	private final int z;
	private final int[] boneIds;
	private final float[] weights;

	public BlockData(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.boneIds = new int[3];
		this.weights = new float[3];
	}

	public final int getX() {
		return (this.x);
	}

	public final int getY() {
		return (this.y);
	}

	public final int getZ() {
		return (this.z);
	}

	public final int getBoneID(int i) {
		return (this.boneIds[i]);
	}

	public final float getBoneWeight(int i) {
		return (this.weights[i]);
	}

	public final float getBoneWeightByBoneID(int boneID) {
		for (int i = 0; i < this.boneIds.length; i++) {
			if (this.boneIds[i] == boneID) {
				return (this.weights[i]);
			}
		}
		return (0);
	}

	public final void setBoneWeight(int i, int boneID, float weight) {
		this.boneIds[i] = boneID;
		this.weights[i] = weight;
	}
}
