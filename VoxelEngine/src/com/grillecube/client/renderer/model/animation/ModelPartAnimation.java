/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.model.animation;

import java.util.ArrayList;
import java.util.Comparator;

import com.grillecube.common.Logger;

/**
 * 
 * @author Romain
 * 
 *         Animation: animation_id: animation frames: animations frames
 */

public class ModelPartAnimation {

	/** every animation's frames */
	private ArrayList<ModelAnimationFrame> _frames;

	/** frame comparator, to always make them sorted by time */
	private Comparator<ModelAnimationFrame> _frame_comparator = new Comparator<ModelAnimationFrame>() {
		public int compare(ModelAnimationFrame a1, ModelAnimationFrame a2) {
			if (a1 == null || a2 == null) {
				return (-1);
			}
			if (a1.getTime() == a2.getTime()) {
				return (0);
			}
			if (a1.getTime() < a2.getTime()) {
				return (-1);
			}
			return (1);
		}
	};

	public ModelPartAnimation() {
		this._frames = new ArrayList<ModelAnimationFrame>();
	}

	/** add a frame */
	public void addFrame(ModelAnimationFrame frame) {
		if (frame == null) {
			Logger.get().log(Logger.Level.WARNING, "Tried to add a NULL frame to : " + this.toString());
			return;
		}
		this._frames.add(frame);
	}

	public void sortFrames() {
		this._frames.sort(this._frame_comparator);
	}

	public void removeFrame(ModelAnimationFrame animation) {
		this._frames.remove(animation);
	}

	public ArrayList<ModelAnimationFrame> getFrames() {
		return (this._frames);
	}

	/** return number of frames for this animation */
	public int getFramesCount() {
		return (this._frames.size());
	}

	public ModelAnimationFrame getFrameAt(int i) {

		if (i < 0 || i >= this._frames.size()) {
			return (null);
		}

		return (this._frames.get(i));
	}
}
