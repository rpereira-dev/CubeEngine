package com.grillecube.client.renderer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.client.renderer.model.animation.BoneTransform;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.instance.AnimationInstance;
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

	/** update this skeleton instance */
	public void update(ArrayList<AnimationInstance> animationInstances) {

		if (animationInstances == null || animationInstances.size() == 0) {
			return;
		}

		// only handle the first animation for now
		AnimationInstance animationInstance = animationInstances.get(0);
		KeyFrame[] frames = animationInstance.getFrames();
		if (frames != null) {
			KeyFrame prev = frames[0];
			KeyFrame next = frames[1];
			HashMap<String, Matrix4f> currentPose = this.interpolateFrames(animationInstance, prev, next);
			this.applyPoseToBones(currentPose, this.getRootBone(), Matrix4f.IDENTITY);
		}
	}

	/**
	 * This is the method where the animator calculates and sets those all-
	 * important "bone transforms" that I talked about so much in the tutorial.
	 * 
	 * This method applies the current pose to a given bone, and all of its
	 * descendants. It does this by getting the desired local-transform for the
	 * current bone, before applying it to the bone. Before applying the
	 * transformations it needs to be converted from local-space to model-space
	 * (so that they are relative to the model's origin, rather than relative to
	 * the parent bone). This can be done by multiplying the local-transform of
	 * the bone with the model-space transform of the parent bone.
	 * 
	 * The same thing is then done to all the child bones.
	 * 
	 * Finally the inverse of the bone's bind transform is multiplied with the
	 * model-space transform of the bone. This basically "subtracts" the bone's
	 * original bind (no animation applied) transform from the desired pose
	 * transform. The result of this is then the transform required to move the
	 * bone from its original model-space transform to it's desired model-space
	 * posed transform. This is the transform that needs to be loaded up to the
	 * vertex shader and used to transform the vertices into the current pose.
	 * 
	 * @param currentPose
	 *            - a map of the local-space transforms for all the bones for
	 *            the desired pose. The map is indexed by the name of the bone
	 *            which the transform corresponds to.
	 * @param bone
	 *            - the current bone which the pose should be applied to.
	 * @param parentTransform
	 *            - the desired model-space transform of the parent bone for the
	 *            pose.
	 */
	private void applyPoseToBones(HashMap<String, Matrix4f> currentPose, Bone bone, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(bone.getName());
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		Matrix4f.mul(currentTransform, bone.getInverseBindTransform(), currentTransform);
		bone.setAnimationTransform(currentTransform);

		if (bone.hasChildren()) {
			for (String childName : bone.getChildrens()) {
				this.applyPoseToBones(currentPose, this.getBone(childName), currentTransform);
			}
		}
	}

	/**
	 * Calculates all the local-space bone transforms for the desired current
	 * pose by interpolating between the transforms at the previous and next
	 * keyframes.
	 * 
	 * @param prev
	 *            - the previous keyframe in the animation.
	 * @param next
	 *            - the next keyframe in the animation.
	 * @return The local-space transforms for all the bones for the desired
	 *         current pose. They are returned in a map, indexed by the name of
	 *         the bone to which they should be applied.
	 */
	private HashMap<String, Matrix4f> interpolateFrames(AnimationInstance animInstance, KeyFrame prev, KeyFrame next) {

		float progression = (animInstance.getTime() - prev.getTime()) / (float) (next.getTime() - prev.getTime());

		HashMap<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();

		HashMap<String, BoneTransform> prevTransforms = prev.getBoneKeyFrames();
		HashMap<String, BoneTransform> nextTransforms = next.getBoneKeyFrames();
		for (Entry<String, BoneTransform> entry : prevTransforms.entrySet()) {
			BoneTransform prevTransform = entry.getValue();
			BoneTransform nextTransform = nextTransforms.get(entry.getKey());

			BoneTransform interpolation = BoneTransform.interpolate(prevTransform, nextTransform, progression);

			Matrix4f localTransform = interpolation.getLocalTransform();

			currentPose.put(entry.getKey(), localTransform);
		}

		return (currentPose);
	}

	/**
	 * Gets an array of the all important model-space transforms of all the
	 * bones (with the current animation pose applied) in the entity. The bones
	 * are ordered in the array based on their bone index. The position of each
	 * bone's transform in the array is equal to the bone's index.
	 * 
	 * This adds the current model-space transform of a bone (and all of its
	 * descendants) into an array of transforms. The bone's transform is added
	 * into the array at the position equal to the bone's index.
	 * 
	 * @return The array of model-space transforms of the bones in the current
	 *         animation pose.
	 */
	public Matrix4f[] getBoneTransforms() {
		Matrix4f[] boneMatrices = new Matrix4f[this.bonesList.size()];
		for (Bone bone : this.bonesList) {
			int id = bone.getID();
			boneMatrices[id] = bone.getTransformation();
		}
		return (boneMatrices);
	}

	/** return the number of bones for this skeleton */
	public final int getBoneCount() {
		return (this.bonesList.size());
	}
}
