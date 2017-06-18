package com.grillecube.client.renderer.model.instance;

import com.grillecube.client.renderer.model.animation.Joint;
import com.grillecube.common.maths.Matrix4f;

public class ModelSkeletonInstance {

	/** root joint instance */
	private JointInstance[] jointInstances;

	/** the matrices */
	private Matrix4f[] jointMatrices;

	public ModelSkeletonInstance(ModelInstance modelInstance) {

		int jointCount = modelInstance.getModel().getSkeleton().getJointCount();
		this.jointInstances = new JointInstance[jointCount];
		this.jointMatrices = new Matrix4f[jointCount];
		Joint root = modelInstance.getModel().getSkeleton().getRootJoint();
		this.initializeJoint(root);
	}

	private void initializeJoint(Joint joint) {
		this.jointInstances[joint.getID()] = new JointInstance(joint);
		if (joint.getChildrens() != null) {
			for (Joint child : joint.getChildrens()) {
				this.initializeJoint(child);
			}
		}
	}

	/**
	 * Gets an array of the all important model-space transforms of all the
	 * joints (with the current animation pose applied) in the entity. The
	 * joints are ordered in the array based on their joint index. The position
	 * of each joint's transform in the array is equal to the joint's index.
	 * 
	 * This adds the current model-space transform of a joint (and all of its
	 * descendants) into an array of transforms. The joint's transform is added
	 * into the array at the position equal to the joint's index.
	 * 
	 * @return The array of model-space transforms of the joints in the current
	 *         animation pose.
	 */
	public Matrix4f[] getJointTransforms() {
		for (int i = 0; i < this.jointInstances.length; i++) {
			JointInstance jointInstance = this.jointInstances[i];
			this.jointMatrices[i] = jointInstance.getTransformation();
		}
		return (this.jointMatrices);
	}

	/** update this skeleton instance */
	public void update() {
		// TODO Auto-generated method stub
	}
}
