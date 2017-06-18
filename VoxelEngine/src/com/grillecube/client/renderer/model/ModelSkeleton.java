package com.grillecube.client.renderer.model;

import com.grillecube.client.renderer.model.animation.Joint;
import com.grillecube.client.renderer.model.dae.datastructures.JointData;
import com.grillecube.client.renderer.model.dae.datastructures.SkeletonData;

public class ModelSkeleton {

	/** the joint transforms array */
	private Joint rootJoint;
	private int jointCount;

	public ModelSkeleton() {
	}

	/**
	 * @return : the joint transform array
	 */
	public Joint getRootJoint() {
		return (this.rootJoint);
	}

	public void set(SkeletonData skeletonData) {
		this.jointCount = skeletonData.jointCount;
		this.rootJoint = createJoints(skeletonData.headJoint);
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data
	 *            - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return (joint);
	}

	public int getJointCount() {
		return (this.jointCount);
	}
}
