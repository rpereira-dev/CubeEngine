package com.grillecube.client.renderer.model.editor.mesher;

/** hold the data of a single block of the model */
public class BlockData {

	private final int[] boneIds;
	private final float[] weights;

	public BlockData() {
		this.boneIds = new int[3];
		this.weights = new float[3];
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BlockData{");
		sb.append(this.boneIds[0]);
		sb.append(",");
		sb.append(this.boneIds[1]);
		sb.append(",");
		sb.append(this.boneIds[2]);
		sb.append(",");
		sb.append(this.weights[0]);
		sb.append(",");
		sb.append(this.weights[1]);
		sb.append(",");
		sb.append(this.weights[2]);
		sb.append("}");
		return (sb.toString());
	}
}
