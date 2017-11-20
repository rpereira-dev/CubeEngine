package com.grillecube.client.renderer.model.animation;

import java.util.ArrayList;

import com.grillecube.client.renderer.model.ModelSkeleton;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Quaternion;
import com.grillecube.common.maths.Vector3f;

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
	private final Vector3f translate;
	private final Vector3f rotate;

	/**
	 * @param ModelSkeleton
	 *            : the parent skeleton
	 * @param name
	 *            - the name of the bone
	 */
	public Bone(ModelSkeleton modelSkeleton, String name) {
		this.modelSkeleton = modelSkeleton;
		this.name = name;
		this.parentName = null;
		this.localBindTransform = new Matrix4f();
		this.inverseBindTransform = new Matrix4f();
		this.translate = new Vector3f();
		this.rotate = new Vector3f(0.0f, 0.0f, 0.0f);
	}

	/** get the joint name */
	public String getName() {
		return (this.name);
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

	public final void setParent(String parentName) {
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

	public final void setLocalBindTransform(Matrix4f setLocalBindTransform) {
		this.localBindTransform.set(setLocalBindTransform);
	}

	public final void setLocalBindTransform(float x, float y, float z, float rx, float ry, float rz, float rw) {
		this.translate.set(x, y, z);
		Quaternion.toEulerAngle(this.rotate, rx, ry, rz, rw);

		this.localBindTransform.setIdentity();
		this.localBindTransform.translate(x, y, z);
		this.localBindTransform.rotateXYZ(this.rotate);
		this.localBindTransform.translate(this.translate);
	}

	public final Vector3f getLocalRotation() {
		return (this.rotate);
	}

	public final Vector3f getLocalTranslation() {
		return (this.translate);
	}

	public final Matrix4f getLocalBindTransform() {
		return (this.localBindTransform);
	}

	/**
	 * 
	 * @see #Bone{@link #calcInverseBindTransform()}
	 * 
	 *      It then recursively calls the method for all of the children joints,
	 *      so that they too calculate and store their inverse bind-pose
	 *      transform.
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
	 * frame.
	 * 
	 * This method should be called to calculate only one specific bone
	 * bindTransform (basically, when this bone referential changes, but the
	 * rest of the bones in the skeleton remain un-changed)
	 * 
	 * To calculate very bind transform matrices more efficiently, call
	 * {@link #calcInverseBindTransform(Matrix4f.IDENTITY)} on the root bone.
	 */
	public void calcInverseBindTransform() {
		Matrix4f bindTransform = new Matrix4f();
		Bone parent = this;
		do {
			Matrix4f.mul(parent.localBindTransform, bindTransform, bindTransform);
			parent = this.modelSkeleton.getBone(parent.getParentName());
		} while (parent != null && parent.getParentName() != null);
		Matrix4f.invert(bindTransform, this.inverseBindTransform);
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
