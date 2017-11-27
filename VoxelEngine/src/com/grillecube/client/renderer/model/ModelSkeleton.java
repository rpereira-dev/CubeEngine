package com.grillecube.client.renderer.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.common.Logger;

public class ModelSkeleton {

	/** root bone instance */
	private final HashMap<String, Bone> bonesMap;
	private final ArrayList<Bone> bonesList;
	private ArrayList<Bone> rootBones;

	public ModelSkeleton() {
		this.rootBones = new ArrayList<Bone>();
		this.bonesMap = new HashMap<String, Bone>();
		this.bonesList = new ArrayList<Bone>();
	}

	public ModelSkeleton(ModelSkeleton skeleton) {
		this.bonesMap = new HashMap<String, Bone>(skeleton.bonesMap);
		this.bonesList = new ArrayList<Bone>(skeleton.bonesList);
		this.rootBones = skeleton.rootBones;
	}

	/**
	 * @return : the root bones names
	 */
	public final ArrayList<Bone> getRootBones() {
		return (this.rootBones);
	}

	/** get the number of bones for this skeleton */
	public final int getJointCount() {
		return (this.bonesMap.size());
	}

	/**
	 * add a bone to this skeleton
	 * 
	 * @param boneName
	 * @param boneParentName
	 * @param boneBindLocalTransform
	 * @return
	 */
	public final void addBone(Bone bone) {
		this.bonesMap.put(bone.getName(), bone);
		bone.setID(this.bonesList.size());
		this.bonesList.add(bone);
		if (bone.getParentName() == null) {
			Logger.get().log(Logger.Level.DEBUG, "added a root bone (" + bone.getName() + ") to a ModelSkeleton");
			this.rootBones.add(bone);
		}
	}

	/** get a bone by it name */
	public final Bone getBone(String boneName) {
		return (this.bonesMap.get(boneName));
	}

	/** get a bone by it id */
	public final Bone getBone(int boneID) {
		return (boneID < 0 || boneID >= this.bonesList.size() ? null : this.bonesList.get(boneID));
	}

	/** get a bone by it id */
	public final String getBoneName(int boneID) {
		Bone bone = this.getBone(boneID);
		return (bone == null ? null : bone.getName());
	}

	/** remove a bone */
	public final void removeBone(Bone bone) {
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
