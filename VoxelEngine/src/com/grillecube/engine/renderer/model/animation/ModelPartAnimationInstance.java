package com.grillecube.engine.renderer.model.animation;

import java.util.ArrayList;

import com.grillecube.engine.maths.Vector3f;

public class ModelPartAnimationInstance {

	/** the animation */
	private final ModelPartAnimation _animation;

	/** vectors buffers */
	private Vector3f _interpolated_translation;
	private Vector3f _interpolated_offset;
	private Vector3f _interpolated_rotation;
	private Vector3f _interpolated_scale;

	public ModelPartAnimationInstance(ModelPartAnimation animation) {
		this._interpolated_translation = new Vector3f(ModelAnimationFrame.DEFAULT_FRAME.getTranslation());
		this._interpolated_offset = new Vector3f(ModelAnimationFrame.DEFAULT_FRAME.getOffset());
		this._interpolated_rotation = new Vector3f(ModelAnimationFrame.DEFAULT_FRAME.getRotation());
		this._interpolated_scale = new Vector3f(ModelAnimationFrame.DEFAULT_FRAME.getScaling());
		this._animation = animation;
	}

	/** update the animation */
	public void update(int timer) {

		ArrayList<ModelAnimationFrame> frames = this._animation.getFrames();

		// handle trivial cases
		int size = frames.size();
		if (size == 0) {
			this.setVectorToFrame(ModelAnimationFrame.DEFAULT_FRAME);
		} else if (size == 1) {
			this.setVectorToFrame(frames.get(0));
		} else {
			// find the two frames in between the timer
			ModelAnimationFrame first = null;

			// for each frames
			int i = 0;
			while (i < size) {

				// get the two frame (timer is between two frames)
				ModelAnimationFrame frame = frames.get(i);
				if (frame.getTime() == timer) {
					this.setVectorToFrame(frame);
					return;
				} else if (frame.getTime() < timer) {
					first = frame;
				} else {
					if (first == null) {
						this.setVectorToFrame(frame);
					} else {
						this.interpolateVectors(first, frame, timer);
					}
					return;
				}
				++i;
			}
			this.setVectorToFrame(first);
		}
	}

	private void setVectorToFrame(ModelAnimationFrame frame) {
		this._interpolated_translation.set(frame.getTranslation());
		this._interpolated_rotation.set(frame.getRotation());
		this._interpolated_offset.set(frame.getOffset());
		this._interpolated_scale.set(frame.getScaling());
	}

	/** interpolate the animation vectors */
	private void interpolateVectors(ModelAnimationFrame first, ModelAnimationFrame second, int timer) {
		int min = timer - first.getTime();
		int max = second.getTime() - first.getTime();
		float ratio = min / (float) max;

		this.interpolateVector(this._interpolated_translation, first.getTranslation(), second.getTranslation(), ratio);
		this.interpolateVector(this._interpolated_offset, first.getOffset(), second.getOffset(), ratio);
		this.interpolateVector(this._interpolated_rotation, first.getRotation(), second.getRotation(), ratio);
		this.interpolateVector(this._interpolated_scale, first.getScaling(), second.getScaling(), ratio);
	}

	private Vector3f interpolateVector(Vector3f dst, Vector3f left, Vector3f right, float ratio) {
		dst.set(left.x * (1 - ratio) + right.x * ratio, left.y * (1 - ratio) + right.y * ratio,
				left.z * (1 - ratio) + right.z * ratio);
		return (dst);
	}

	public Vector3f getInterpolatedTranslation() {
		return (this._interpolated_translation);
	}

	public Vector3f getInterpolatedRotation() {
		return (this._interpolated_rotation);
	}

	public Vector3f getInterpolatedScaling() {
		return (this._interpolated_scale);
	}

	public Vector3f getInterpolatedOffset() {
		return (this._interpolated_offset);
	}
}
