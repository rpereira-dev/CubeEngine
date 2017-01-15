package com.grillecube.engine.renderer.model.animation;

import java.util.ArrayList;

import com.grillecube.engine.Logger;

public class ModelAnimation {

	/** each part animations */
	private ArrayList<ModelPartAnimation> _parts;

	/** animation name and id */
	private String _name;

	/**
	 * time this animation last in ms
	 */
	private int _duration;

	public ModelAnimation(String name) {
		this._parts = new ArrayList<ModelPartAnimation>();
		this._name = name;
		this._duration = 0;
	}

	public ModelPartAnimation getAnimationPart(int partID) {

		if (partID < 0 || partID >= this._parts.size()) {
			Logger.get().log(Logger.Level.DEBUG, "Tried to get an unknown part id", this,
					partID + "/" + this._parts.size());
			return (null);
		}
		return (this._parts.get(partID));
	}

	public int getDuration() {
		return (this._duration);
	}

	public void updateDuration() {
		int maxduration = 0;
		for (ModelPartAnimation part : this._parts) {
			// ensure frames are sorted
			part.sortFrames();
			// get last frame
			ModelAnimationFrame frame = part.getFrameAt(part.getFramesCount() - 1);
			if (frame != null && frame.getTime() > maxduration) {
				maxduration = frame.getTime();
			}
		}
		this._duration = maxduration;
	}

	/** return animation name */
	public String getName() {
		return (this._name);
	}

	@Override
	public String toString() {
		return (this._name);
	}

	public int getAnimationPartCount() {
		return (this._parts.size());
	}

	public ArrayList<ModelPartAnimation> getAnimationParts() {
		return (this._parts);
	}

	public void removePart(int index) {
		this._parts.remove(index);
	}

	public void addPart(ModelPartAnimation part) {
		this._parts.add(part);
	}
}
