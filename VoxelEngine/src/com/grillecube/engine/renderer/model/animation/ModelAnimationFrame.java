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

package com.grillecube.engine.renderer.model.animation;

import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.maths.Vector3f;

/**
 * 
 * @author Romain
 * 
 *         Frame: _time: time in millis for this frame _rotation: rotation (in
 *         radian) for this frame _scale: scaling for this frame (1 is the unit)
 *         _translate: translate for this frame (1 is the unit) _offset: offset
 *         rotation point
 */
public class ModelAnimationFrame implements Comparable<ModelAnimationFrame> {

	public static final ModelAnimationFrame DEFAULT_FRAME = new ModelAnimationFrame();

	private int _time; // in millis
	private Vector3f _rotation;
	private Vector3f _scale;
	private Vector3f _translate;
	private Vector3f _offset;

	public ModelAnimationFrame(int t, Vector3f translate, Vector3f rotation, Vector3f offset, Vector3f scale) {
		this._time = t;
		this._rotation = new Vector3f(rotation);
		this._scale = new Vector3f(scale);
		this._translate = new Vector3f(translate);
		this._offset = new Vector3f(offset);
	}

	public ModelAnimationFrame() {
		this(0, Matrix4f.DEFAULT_POS, Matrix4f.DEFAULT_ROT, Matrix4f.DEFAULT_OFFSET, Matrix4f.DEFAULT_SCALE);
	}

	public ModelAnimationFrame clone() {
		return new ModelAnimationFrame(this._time, this._translate, this._rotation, this._offset, this._scale);
	}

	public Vector3f getTranslation() {
		return (this._translate);
	}

	public Vector3f getRotation() {
		return (this._rotation);
	}

	public Vector3f getScaling() {
		return (this._scale);
	}

	public Vector3f getOffset() {
		return (this._offset);
	}

	public int getTime() {
		return (this._time);
	}

	public void setTime(int time) {
		this._time = time;
	}

	@Override
	public String toString() {
		return (String.valueOf(this._time / 1000.0f));
	}

	public void set(ModelAnimationFrame frame) {
		if (frame == null) {
			return;
		}
		this._time = frame._time;
		this._translate.set(frame._translate);
		this._rotation.set(frame._rotation);
		this._scale.set(frame._scale);
		this._offset.set(frame._offset);
	}

	@Override
	public int compareTo(ModelAnimationFrame other) {
		if (other.getTime() < this.getTime()) {
			return (-1);
		}
		if (other.getTime() == this.getTime()) {
			return (0);
		}
		return (1);
	}
}