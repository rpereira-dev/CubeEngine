package com.grillecube.client.renderer.model.instance;

import java.util.ArrayList;

import com.grillecube.client.renderer.model.animation.KeyFrame;
import com.grillecube.client.renderer.model.animation.ModelSkeletonAnimation;

public class AnimationInstance {

	/** the animation */
	private final ModelSkeletonAnimation animation;

	/** the begin time */
	private long time;

	/** state of the animation (loop, freeze...) */
	private static final int STATE_NONE = 0;
	private static final int STATE_LOOP = 1;
	private static final int STATE_FREEZE = 2;
	private static final int STATE_STOP = 3;
	private byte state;

	public AnimationInstance(ModelSkeletonAnimation animation) {
		this.animation = animation;
		this.time = 0;
		this.state = STATE_NONE;
	}

	/** upate the animation instance, return true if the animation is ended */
	public void update(long dt) {

		if (this.state == STATE_STOP || this.state == STATE_FREEZE) {
			return;
		}

		long duration = this.animation.getDuration();
		this.time += dt;
		if (this.time >= duration) {
			if (this.state == STATE_LOOP) {
				this.time %= duration;
			} else {
				this.state = STATE_STOP;
			}
		}
	}

	/** freeze the animation */
	public void freeze() {
		this.state = STATE_FREEZE;
	}

	/** loop the animation */
	public void loop() {
		this.state = STATE_LOOP;
	}

	/** stop the animation roughly */
	public void stop() {
		this.state = STATE_STOP;
	}

	/** end the animation loop by ending the current loop */
	public void stopLoop() {
		this.state = STATE_NONE;
	}

	public boolean isStopped() {
		return (this.state == STATE_STOP);
	}

	/** return the previous and next frame for this animation */
	public KeyFrame[] getFrames() {
		ArrayList<KeyFrame> frames = this.animation.getKeyFrames();
		if (frames == null || frames.isEmpty()) {
			return (null);
		}
		KeyFrame prev = frames.get(0);
		KeyFrame next = frames.get(0);
		for (int i = 1; i < frames.size(); i++) {
			next = frames.get(i);
			if (next.getTime() > this.time) {
				break;
			}
			prev = next;
		}
		return (new KeyFrame[] { prev, next });

	}

	public long getTime() {
		return (this.time);
	}
}
