package com.grillecube.client.renderer.model.animation;

import java.util.HashMap;

/**
 * 
 * Represents one keyframe of an animation. This contains the timestamp of the
 * keyframe, which is the time (in seconds) from the start of the animation when
 * this keyframe occurs.
 * 
 * It also contains the desired bone-space transforms of all of the joints in
 * the animated entity at this keyframe in the animation (i.e. it contains all
 * the joint transforms for the "pose" at this time of the animation.). The
 * joint transforms are stored in a map, indexed by the name of the joint that
 * they should be applied to.
 * 
 * @author Karl
 *
 */
public class KeyFrame implements Comparable<KeyFrame> {

	/**
	 * @param time
	 *            - the time (in millis) that this keyframe occurs during the
	 *            animation.
	 */
	private long time;

	/**
	 * @param pose
	 *            - the local-space transforms for all the joints at this
	 *            keyframe, indexed by the name of the joint that they should be
	 *            applied to.
	 */
	private final HashMap<String, BoneTransform> pose;

	public KeyFrame() {
		this.pose = new HashMap<String, BoneTransform>();
		this.time = 0;
	}

	/**
	 * @return The time in ms of the keyframe in the animation.
	 */
	public final long getTime() {
		return (this.time);
	}

	/**
	 * @return The desired bone-space transforms of all the joints at this
	 *         keyframe, of the animation, indexed by the name of the joint that
	 *         they correspond to. This basically represents the "pose" at this
	 *         keyframe.
	 */
	public final HashMap<String, BoneTransform> getBoneKeyFrames() {
		return (this.pose);
	}

	public final void setTime(long t) {
		this.time = t;
	}

	public final void setBoneTransform(String boneName, BoneTransform boneTransform) {
		this.pose.put(boneName, boneTransform);
	}

	@Override
	public int compareTo(KeyFrame other) {
		if (other.getTime() < this.getTime()) {
			return (-1);
		}
		if (other.getTime() == this.getTime()) {
			return (0);
		}
		return (1);
	}

}
