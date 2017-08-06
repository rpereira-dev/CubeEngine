package com.grillecube.client.renderer.model;

import java.util.ArrayList;

import com.grillecube.client.renderer.model.animation.Joint;
import com.grillecube.client.renderer.model.animation.JointTransform;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.instance.AnimationInstance;
import com.grillecube.common.maths.Matrix4f;

public class ModelSkeleton {

	/** root joint instance */
	private final ArrayList<Joint> joints;
	private final Joint rootJoint;

	/** the matrices */
	private Matrix4f[] jointMatrices;

	public ModelSkeleton() {
		this.rootJoint = new Joint("root", new Matrix4f());
		this.joints = new ArrayList<Joint>();
		this.joints.add(this.rootJoint);
		this.jointMatrices = new Matrix4f[1];
		this.jointMatrices[0] = Matrix4f.IDENTITY;
	}

	public ModelSkeleton(ModelSkeleton skeleton) {
		this.joints = new ArrayList<Joint>(skeleton.joints.size());
		this.joints.addAll(skeleton.joints);
		this.rootJoint = skeleton.rootJoint;
		this.jointMatrices = new Matrix4f[skeleton.jointMatrices.length];
	}

	/**
	 * @return : the joint transform array
	 */
	public Joint getRootJoint() {
		return (this.rootJoint);
	}

	/** get the number of joints for this skeleton */
	public int getJointCount() {
		return (this.joints.size());
	}

	/** add a joint to this skeleton */
	public final void addJoint(Joint parent, Joint child) {
		parent.addChild(child);
		child.setParent(parent);
		child.setID(this.joints.size());
		this.joints.add(child);
		this.jointMatrices = new Matrix4f[this.joints.size()];
	}

	/** remove a joint */
	public final void removeJoint(Joint joint) {
		int id = joint.getID();
		this.joints.remove(id);
		joint.getParent().removeChild(joint);
		for (int i = id; i < this.joints.size(); i++) {
			this.joints.get(i).setID(i);
		}
	}

	/** update this skeleton instance */
	public void update(ArrayList<AnimationInstance> animationInstances) {

		if (animationInstances == null || animationInstances.size() == 0) {
			return;
		}

		AnimationInstance animationInstance = animationInstances.get(0);
		KeyFrame[] frames = animationInstance.getFrames();
		if (frames != null) {
			KeyFrame prev = frames[0];
			KeyFrame next = frames[1];
			ArrayList<Matrix4f> currentPose = this.interpolatePoses(animationInstance, prev, next);
			this.applyPoseToJoints(currentPose, this.rootJoint, new Matrix4f());
		}
	}

	/**
	 * This is the method where the animator calculates and sets those all-
	 * important "joint transforms" that I talked about so much in the tutorial.
	 * 
	 * This method applies the current pose to a given joint, and all of its
	 * descendants. It does this by getting the desired local-transform for the
	 * current joint, before applying it to the joint. Before applying the
	 * transformations it needs to be converted from local-space to model-space
	 * (so that they are relative to the model's origin, rather than relative to
	 * the parent joint). This can be done by multiplying the local-transform of
	 * the joint with the model-space transform of the parent joint.
	 * 
	 * The same thing is then done to all the child joints.
	 * 
	 * Finally the inverse of the joint's bind transform is multiplied with the
	 * model-space transform of the joint. This basically "subtracts" the
	 * joint's original bind (no animation applied) transform from the desired
	 * pose transform. The result of this is then the transform required to move
	 * the joint from its original model-space transform to it's desired
	 * model-space posed transform. This is the transform that needs to be
	 * loaded up to the vertex shader and used to transform the vertices into
	 * the current pose.
	 * 
	 * @param currentPose
	 *            - a map of the local-space transforms for all the joints for
	 *            the desired pose. The map is indexed by the name of the joint
	 *            which the transform corresponds to.
	 * @param joint
	 *            - the current joint which the pose should be applied to.
	 * @param parentTransform
	 *            - the desired model-space transform of the parent joint for
	 *            the pose.
	 */
	private void applyPoseToJoints(ArrayList<Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.getID());
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		if (joint.hasChildren()) {
			for (Joint childJoint : joint.getChildrens()) {
				this.applyPoseToJoints(currentPose, childJoint, currentTransform);
			}
		}
		Matrix4f.mul(currentTransform, joint.getInverseBindTransform(), currentTransform);
		this.joints.get(joint.getID()).setAnimationTransform(currentTransform);
	}

	/**
	 * Calculates all the local-space joint transforms for the desired current
	 * pose by interpolating between the transforms at the previous and next
	 * keyframes.
	 * 
	 * @param prev
	 *            - the previous keyframe in the animation.
	 * @param next
	 *            - the next keyframe in the animation.
	 * @return The local-space transforms for all the joints for the desired
	 *         current pose. They are returned in a map, indexed by the name of
	 *         the joint to which they should be applied.
	 */
	private ArrayList<Matrix4f> interpolatePoses(AnimationInstance animationInstance, KeyFrame prev, KeyFrame next) {

		float progression = (animationInstance.getTime() - prev.getTime()) / (float) (next.getTime() - prev.getTime());
		ArrayList<Matrix4f> currentPose = new ArrayList<Matrix4f>();

		ArrayList<JointTransform> prevTransforms = prev.getJointKeyFrames();
		ArrayList<JointTransform> nextTransforms = next.getJointKeyFrames();
		for (int i = 0; i < prevTransforms.size(); i++) {
			JointTransform prevTransform = prevTransforms.get(i);
			JointTransform nextTransform = nextTransforms.get(i);

			JointTransform interpolation = JointTransform.interpolate(prevTransform, nextTransform, progression);

			Matrix4f localTransform = interpolation.getLocalTransform();
			currentPose.add(localTransform);
		}

		return (currentPose);
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
		for (int i = 0; i < this.joints.size(); i++) {
			this.jointMatrices[i] = this.joints.get(i).getTransformation();
		}
		return (this.jointMatrices);
	}

}
