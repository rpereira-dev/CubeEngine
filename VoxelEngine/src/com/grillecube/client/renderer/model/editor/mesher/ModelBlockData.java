package com.grillecube.client.renderer.model.editor.mesher;

import com.grillecube.common.faces.Face;

/** hold the data of a single block of the model */
public class ModelBlockData {

	/** the plan linked to this block */
	private final int[] boneIds;
	private final float[] weights;
	private final int x, y, z;
	private final ModelPlane[] planes;

	public ModelBlockData(int bx, int by, int bz) {
		this.x = bx;
		this.y = by;
		this.z = bz;
		this.boneIds = new int[] { 0, 0, 0 };
		this.weights = new float[] { 1, 0, 0 };
		this.planes = new ModelPlane[Face.faces.length];
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

	public final int getX() {
		return (this.x);
	}

	public final int getY() {
		return (this.y);
	}

	public final int getZ() {
		return (this.z);
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

	public final void resetPlanes() {
		for (Face face : Face.faces) {
			this.planes[face.getID()] = null;
		}
	}

	public final void setPlane(ModelPlane modelPlane, Face face) {
		this.planes[face.getID()] = modelPlane;
	}

	public final ModelPlane getPlane(Face face) {
		return (this.planes[face.getID()]);
	}
}
