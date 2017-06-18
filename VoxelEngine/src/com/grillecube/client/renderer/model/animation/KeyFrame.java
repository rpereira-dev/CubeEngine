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

public class KeyFrame implements Comparable<KeyFrame> {
	
	/** keyframe time in ms */
	private int time;

	/** the transformation */
	private JointTransform[] transformations;

	public KeyFrame(int time) {
		this.time = time;
		this.transformations = null;
	}

	public int getTime() {
		return (this.time);
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return ("KeyFrame: " + String.valueOf(this.time / 1000.0f));
	}

	public void set(KeyFrame frame) {
		if (frame == null) {
			return;
		}
		this.time = frame.time;
		this.transformations = frame.transformations;
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