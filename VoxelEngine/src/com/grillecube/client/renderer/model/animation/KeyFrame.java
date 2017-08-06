package com.grillecube.client.renderer.model.animation;

import java.util.ArrayList;

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

	private final int time;
	private final ArrayList<JointTransform> pose;

	/**
	 * @param time
	 *            - the time (in millis) that this keyframe occurs during the
	 *            animation.
	 * @param jointKeyFrames
	 *            - the local-space transforms for all the joints at this
	 *            keyframe, indexed by the name of the joint that they should be
	 *            applied to.
	 */
	public KeyFrame(int time, ArrayList<JointTransform> jointKeyFrames) {
		this.time = time;
		this.pose = jointKeyFrames;
	}

	/**
	 * @return The time in ms of the keyframe in the animation.
	 */
	public int getTime() {
		return (this.time);
	}

	/**
	 * @return The desired bone-space transforms of all the joints at this
	 *         keyframe, of the animation, indexed by the name of the joint that
	 *         they correspond to. This basically represents the "pose" at this
	 *         keyframe.
	 */
	public ArrayList<JointTransform> getJointKeyFrames() {
		return (this.pose);
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
