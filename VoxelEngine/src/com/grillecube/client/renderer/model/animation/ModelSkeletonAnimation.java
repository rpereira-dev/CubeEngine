package com.grillecube.client.renderer.model.animation;

import java.util.ArrayList;

public class ModelSkeletonAnimation {

	/** the animation name */
	private final String name;

	/** the keyframes for this animation */
	private final ArrayList<KeyFrame> keyFrames;

	/** animation length */
	private final long duration;

	public ModelSkeletonAnimation(String name, long duration, ArrayList<KeyFrame> keyFrames) {
		this.name = name;
		this.duration = duration;
		this.keyFrames = keyFrames;
	}

	public String getName() {
		return (name);
	}

	/** get the keyframe list of this animation */
	public ArrayList<KeyFrame> getKeyFrames() {
		return (this.keyFrames);
	}

	/** add a keyframe to the animation */
	public KeyFrame addKeyFrame(KeyFrame keyFrame) {
		int i = 0;
		while (i < this.keyFrames.size() && this.keyFrames.get(i++).getTime() < keyFrame.getTime())
			;
		this.keyFrames.add(i, keyFrame);
		return (keyFrame);
	}

	/** get the time of the last keyframe for this animation */
	public final long getDuration() {
		return (this.duration);
	}
}
