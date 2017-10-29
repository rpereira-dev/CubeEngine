package com.grillecube.client.renderer.model.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.grillecube.client.renderer.model.ModelSkeleton;
import com.grillecube.client.renderer.model.animation.Bone;
import com.grillecube.client.renderer.model.animation.BoneTransform;
import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.common.maths.Matrix4f;

//TODO : bone instances
public class ModelSkeletonInstance {

	private final ModelSkeleton modelSkeleton;
	private final HashMap<Bone, BoneInstance> bonesInstances;

	public ModelSkeletonInstance(ModelSkeleton modelSkeleton) {
		this.modelSkeleton = modelSkeleton;
		this.bonesInstances = new HashMap<Bone, BoneInstance>();
	}

	public final ModelSkeleton getSkeleton() {
		return (this.modelSkeleton);
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
			this.applyPoseToBones(currentPose, this.getSkeleton().getRootBone(), Matrix4f.IDENTITY);
		}
	}

	/**
	 * This is the method where the animator calculates and sets those all-
	 * important "bone transforms" that I talked about so much in the tutorial.
	 * 
	 * This method applies the current pose to a given bone, and all of its
	 * descendants. It does this by getting the desired local-transform for the
	 * current bone, before applying it to the bone. Before applying the
	 * transformations it needs to be converted from local-space to model-space (so
	 * that they are relative to the model's origin, rather than relative to the
	 * parent bone). This can be done by multiplying the local-transform of the bone
	 * with the model-space transform of the parent bone.
	 * 
	 * The same thing is then done to all the child bones.
	 * 
	 * Finally the inverse of the bone's bind transform is multiplied with the
	 * model-space transform of the bone. This basically "subtracts" the bone's
	 * original bind (no animation applied) transform from the desired pose
	 * transform. The result of this is then the transform required to move the bone
	 * from its original model-space transform to it's desired model-space posed
	 * transform. This is the transform that needs to be loaded up to the vertex
	 * shader and used to transform the vertices into the current pose.
	 * 
	 * @param currentPose
	 *            - a map of the local-space transforms for all the bones for the
	 *            desired pose. The map is indexed by the name of the bone which the
	 *            transform corresponds to.
	 * @param bone
	 *            - the current bone which the pose should be applied to.
	 * @param parentTransform
	 *            - the desired model-space transform of the parent bone for the
	 *            pose.
	 */
	private void applyPoseToBones(HashMap<String, Matrix4f> currentPose, Bone bone, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(bone.getName());
		if (currentLocalTransform == null) {
			currentLocalTransform = Matrix4f.IDENTITY;
		}
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		Matrix4f.mul(currentTransform, bone.getInverseBindTransform(), currentTransform);
		BoneInstance boneInstance = this.bonesInstances.get(bone);
		if (boneInstance == null) {
			boneInstance = new BoneInstance();
			this.bonesInstances.put(bone, new BoneInstance());
		}
		boneInstance.setAnimationTransform(currentTransform);

		if (bone.hasChildren()) {
			for (String childName : bone.getChildrens()) {
				this.applyPoseToBones(currentPose, this.getSkeleton().getBone(childName), currentTransform);
			}
		}
	}

	/**
	 * Calculates all the local-space bone transforms for the desired current pose
	 * by interpolating between the transforms at the previous and next keyframes.
	 * 
	 * @param prev
	 *            - the previous keyframe in the animation.
	 * @param next
	 *            - the next keyframe in the animation.
	 * @return The local-space transforms for all the bones for the desired current
	 *         pose. They are returned in a map, indexed by the name of the bone to
	 *         which they should be applied.
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
	 * Gets an array of the all important model-space transforms of all the bones
	 * (with the current animation pose applied) in the entity. The bones are
	 * ordered in the array based on their bone index. The position of each bone's
	 * transform in the array is equal to the bone's index.
	 * 
	 * This adds the current model-space transform of a bone (and all of its
	 * descendants) into an array of transforms. The bone's transform is added into
	 * the array at the position equal to the bone's index.
	 * 
	 * @return The array of model-space transforms of the bones in the current
	 *         animation pose.
	 */
	public Matrix4f[] getBoneTransforms() {
		Matrix4f[] boneMatrices = new Matrix4f[this.getSkeleton().getBoneCount()];
		for (Bone bone : this.getSkeleton().getBones()) {
			int id = bone.getID();
			boneMatrices[id] = this.bonesInstances.get(bone).getTransformation();
		}
		return (boneMatrices);
	}
}
