package com.grillecube.client.renderer.model.animation;

import java.util.ArrayList;

import com.grillecube.client.renderer.model.ModelSkeleton;
import com.grillecube.common.maths.Matrix4f;

public class Bone {

	/** the global skeleton */
	private final ModelSkeleton modelSkeleton;

	/** the bone id */
	private int id;

	/** the name */
	private final String name;

	/** parent name */
	private String parentName;

	/** joint childrenNames */
	private ArrayList<String> childrenNames;

	/** bind matrices */
	private final Matrix4f localBindTransform;
	private final Matrix4f inverseBindTransform;

	/** joint transformation matrix */
	private final Matrix4f animatedTransform;

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
	public Bone(ModelSkeleton modelSkeleton, String name, String parentName, Matrix4f localBindTransform) {
		this.modelSkeleton = modelSkeleton;
		this.name = name;
		this.parentName = parentName;
		this.animatedTransform = new Matrix4f();
		this.localBindTransform = new Matrix4f(localBindTransform);
		this.inverseBindTransform = new Matrix4f();
	}

	/** get the joint name */
	public String getName() {
		return (this.name);
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
	public final Matrix4f getTransformation() {
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
	public final void setAnimationTransform(Matrix4f animationTransform) {
		this.animatedTransform.set(animationTransform);
	}

	/**
	 * Adds a child joint to this joint. Used during the creation of the joint
	 * hierarchy. Bones can have multiple children, which is why they are stored
	 * in a list (e.g. a "hand" joint may have multiple "finger" children
	 * joints).
	 * 
	 * @param child
	 *            - the new child joint of this joint.
	 */
	public String addChild(String child) {
		if (this.childrenNames == null) {
			this.childrenNames = new ArrayList<String>(1);
		}
		this.childrenNames.add(child);
		return (child);
	}

	/** return true if this joint has a children */
	public boolean hasChildren() {
		return (this.childrenNames != null && this.childrenNames.size() > 0);
	}

	/** remove a child of this joint */
	public void removeChild(String childName) {
		this.childrenNames.remove(childName);
		if (this.childrenNames.size() == 0) {
			this.childrenNames = null;
		}
	}

	public ArrayList<String> getChildrens() {
		return (this.childrenNames);
	}

	public final void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public final String getParentName() {
		return (this.parentName);
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
	public void calcInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, this.localBindTransform, null);
		Matrix4f.invert(bindTransform, this.inverseBindTransform);
		if (this.hasChildren()) {
			for (String childName : this.childrenNames) {
				this.modelSkeleton.getBone(childName).calcInverseBindTransform(bindTransform);
			}
		}
	}

	public interface BoneTraveller {
		public void onBoneProcessed(Bone joint);
	}

	/** an iterator on each joints */
	public void travel(BoneTraveller boneTraveller) {
		boneTraveller.onBoneProcessed(this);
		if (this.childrenNames != null) {
			for (String childName : this.childrenNames) {
				this.modelSkeleton.getBone(childName).travel(boneTraveller);
			}
		}
	}

	public final int getID() {
		return (this.id);
	}

	public final void setID(int id) {
		this.id = id;
	}
}
