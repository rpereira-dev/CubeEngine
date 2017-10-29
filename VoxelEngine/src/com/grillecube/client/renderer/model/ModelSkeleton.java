package com.grillecube.client.renderer.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.common.maths.Matrix4f;

public class ModelSkeleton {

	/** root bone instance */
	private final HashMap<String, Bone> bonesMap;
	private final ArrayList<Bone> bonesList;
	private String rootBone;

	public ModelSkeleton() {
		this.rootBone = null;
		this.bonesMap = new HashMap<String, Bone>();
		this.bonesList = new ArrayList<Bone>();
	}

	public ModelSkeleton(ModelSkeleton skeleton) {
		this.bonesMap = new HashMap<String, Bone>(skeleton.bonesMap);
		this.bonesList = new ArrayList<Bone>(skeleton.bonesList);
		this.rootBone = skeleton.rootBone;
	}

	/**
	 * @return : the root bone
	 */
	public final Bone getRootBone() {
		return (this.bonesMap.get(this.rootBone));
	}

	/**
	 * @return : the root bone name
	 */
	public final String getRootBoneName() {
		return (this.rootBone);
	}

	/** get the number of bones for this skeleton */
	public final int getJointCount() {
		return (this.bonesMap.size());
	}

	/**
	 * set the root bone of this model skeleton
	 * 
	 * @param rootBoneName
	 */
	public final void setRootBone(String rootBoneName) {
		this.rootBone = rootBoneName;
	}

	/**
	 * add a bone to this skeleton
	 * 
	 * @param boneName
	 * @param boneParentName
	 * @param boneBindLocalTransform
	 * @return
	 */
	public final Bone addBone(String boneName, String boneParentName, Matrix4f boneBindLocalTransform) {
		Bone bone = new Bone(this, boneName, boneParentName, boneBindLocalTransform);
		this.bonesMap.put(bone.getName(), bone);
		bone.setID(this.bonesList.size());
		this.bonesList.add(bone);
		return (bone);
	}

	/** get a bone by it name */
	public final Bone getBone(String boneName) {
		return (this.bonesMap.get(boneName));
	}

	/** remove a bone */
	private final void removeBone(Bone bone) {
		this.bonesMap.remove(bone.getName());

		int boneID = bone.getID();
		this.bonesList.remove(boneID);
		Bone parent = this.getBone(bone.getParentName());
		if (parent != null) {
			parent.removeChild(bone.getName());
		}

		for (int i = boneID; i < this.bonesList.size(); i++) {
			this.bonesList.get(i).setID(i);
		}
	}

	/** return the number of bones for this skeleton */
	public final int getBoneCount() {
		return (this.bonesList.size());
	}

	public final ArrayList<Bone> getBones() {
		return (this.bonesList);
	}
}
