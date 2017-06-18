package com.grillecube.client.renderer.model.animation;

import java.util.ArrayList;

public class Animation {

	/** the animation name */
	private String name;

	/** the keyframes for this animation */
	private ArrayList<KeyFrame> keyFrames;

	public Animation() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** get the keyframe list of this animation */
	public ArrayList<KeyFrame> getKeyFrames() {
		return (this.keyFrames);
	}

	/** add a keyframe to the animation */
	public KeyFrame addKeyFrame(KeyFrame keyFrame) {
		int i = 0;
		while (this.keyFrames.get(i++).getTime() < keyFrame.getTime())
			;
		this.keyFrames.add(i, keyFrame);
		return (keyFrame);
	}

	/** get the time of the last keyframe for this animation */
	public int getDuration() {
		return (this.keyFrames.size() == 0 ? 0 : this.keyFrames.get(this.keyFrames.size() - 1).getTime());
	}
}
