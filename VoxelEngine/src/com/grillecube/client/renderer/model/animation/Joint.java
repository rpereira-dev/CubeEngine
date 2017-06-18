package com.grillecube.client.renderer.model.animation;

import java.util.ArrayList;

import com.grillecube.common.maths.Matrix4f;

public class Joint {

	/** joint childrens */
	private ArrayList<Joint> childrens;

	/** joint id */
	private final int id;

	/** the name */
	private final String name;

	private final Matrix4f localBindTransform;
	private final Matrix4f inverseBindTransform;

	/**
	 * @param id
	 *            - the joint's index (ID).
	 * @param name
	 *            - the name of the joint. This is how the joint is named in the
	 *            collada file, and so is used to identify which joint a joint
	 *            transform in an animation keyframe refers to.
	 * @param bindLocalTransform
	 *            - the bone-space transform of the joint in the bind position.
	 */
	public Joint(int id, String name, Matrix4f bindLocalTransform) {
		this.id = id;
		this.name = name;
		this.localBindTransform = bindLocalTransform;
		this.inverseBindTransform = new Matrix4f();
	}

	/**
	 * @return : the joint id
	 */
	public int getID() {
		return (this.id);
	}

	/**
	 * Adds a child joint to this joint. Used during the creation of the joint
	 * hierarchy. Joints can have multiple children, which is why they are
	 * stored in a list (e.g. a "hand" joint may have multiple "finger" children
	 * joints).
	 * 
	 * @param child
	 *            - the new child joint of this joint.
	 */
	public Joint addChild(Joint modelJoint) {
		if (this.childrens == null) {
			this.childrens = new ArrayList<Joint>(1);
		}
		this.childrens.add(modelJoint);
		return (modelJoint);
	}

	public ArrayList<Joint> getChildrens() {
		return (this.childrens);
	}

	/**
	 * This returns the inverted model-space bind transform. The bind transform
	 * is the original model-space transform of the joint (when no animation is
	 * applied). This returns the inverse of that, which is used to calculate
	 * the animated transform matrix which gets used to transform vertices in
	 * the shader.
	 * 
	 * @return The inverse of the joint's bind transform (in model-space).
	 */
	public Matrix4f getInverseBindTransform() {
		return (this.inverseBindTransform);
	}

	/**
	 * This is called during set-up, after the joints hierarchy has been
	 * created. This calculates the model-space bind transform of this joint
	 * like so: </br>
	 * </br>
	 * {@code bindTransform = parentBindTransform * localBindTransform}</br>
	 * </br>
	 * where "bindTransform" is the model-space bind transform of this joint,
	 * "parentBindTransform" is the model-space bind transform of the parent
	 * joint, and "localBindTransform" is the bone-space bind transform of this
	 * joint. It then calculates and stores the inverse of this model-space bind
	 * transform, for use when calculating the final animation transform each
	 * frame. It then recursively calls the method for all of the children
	 * joints, so that they too calculate and store their inverse bind-pose
	 * transform.
	 * 
	 * @param parentBindTransform
	 *            - the model-space bind transform of the parent joint.
	 */
	private void calcInverseBindTransform(Matrix4f parentBindTransform) {// TODO
		Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, this.localBindTransform, null);
		// Matrix4f bindTransform = Matrix4f.mul(parentBindTransform,
		// this.localBindTransform, this.inverseBindTransform);
		Matrix4f.invert(bindTransform, this.inverseBindTransform);
		for (Joint child : this.childrens) {
			child.calcInverseBindTransform(bindTransform);
		}
	}

	public interface JointTraveller {
		public void onJointProcessed(Joint joint);
	}

	/** an iterator on each joints */
	public void travel(JointTraveller jointTraveller) {
		jointTraveller.onJointProcessed(this);
		if (this.childrens != null) {
			for (Joint joint : this.childrens) {
				joint.travel(jointTraveller);
			}
		}
	}
}
