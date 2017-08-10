package com.grillecube.client.renderer.model.collada;

public class SkeletonData {
	public final int jointCount;
	public final JointData headJoint;

	public SkeletonData(int jointCount, JointData headJoint) {
		this.jointCount = jointCount;
		this.headJoint = headJoint;
	}

}