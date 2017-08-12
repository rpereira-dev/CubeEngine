package com.grillecube.client.renderer.model.editor.mesher;

/** hold the data of a single block of the model */
public class ModelBlockData {

	private final int[] jointIds;
	private final float[] jointWeights;

	public ModelBlockData() {
		this.jointIds = new int[3];
		this.jointWeights = new float[3];
	}

	public final int getJoint(int i) {
		return (this.jointIds[i]);
	}

	public final float getJointWeight(int i) {
		return (this.jointWeights[i]);
	}

	public final void setJoint(int i, int jointID) {
		this.jointIds[i] = jointID;
	}

	public final void setJointWeight(int i, float weight) {
		this.jointWeights[i] = weight;
	}
}
