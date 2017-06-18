package com.grillecube.client.renderer.model.instance;

import com.grillecube.client.renderer.model.animation.Joint;
import com.grillecube.common.maths.Matrix4f;

public class JointInstance {

	/** the joint linked */
	private final Joint joint;

	/** joint transformation matrix */
	private Matrix4f animatedTransform;

	public JointInstance(Joint joint) {
		this.joint = joint;
		this.animatedTransform = new Matrix4f();
	}

	/**
	 * The animated transform is the transform that gets loaded up to the shader
	 * and is used to deform the vertices of the "skin". It represents the
	 * transformation from the joint's bind position (original position in
	 * model-space) to the joint's desired animation pose (also in model-space).
	 * This matrix is calculated by taking the desired model-space transform of
	 * the joint and multiplying it by the inverse of the starting model-space
	 * transform of the joint.
	 * 
	 * @return The transformation matrix of the joint which is used to deform
	 *         associated vertices of the skin in the shaders.
	 */
	public Matrix4f getTransformation() {
		return (this.animatedTransform);
	}

	/**
	 * This method allows those all important "joint transforms" (as I referred
	 * to them in the tutorial) to be set by the animator. This is used to put
	 * the joints of the animated model in a certain pose.
	 * 
	 * @param animationTransform
	 *            - the new joint transform.
	 */
	public void setAnimationTransform(Matrix4f animationTransform) {
		this.animatedTransform = animationTransform;
	}

	/** get the joint */
	public Joint getJoint() {
		return (this.joint);
	}
}
