package com.grillecube.client.renderer.model;

import org.lwjgl.util.vector.Vector3f;

/**
 * 
 * @author Romain
 * 
 * Frame:
 *		_time:		time in sec for this frame
 *		_rotation:	rotation (in radian) for this frame
 *		_scale:		scaling for this frame (1 is the unit)
 *		_translate:	translate for this frame (1 is the unit)
 *		_offset: offset rotation point
 */
public class AnimationFrame
{
	private float		_time;
	private Vector3f	_rotation;
	private Vector3f	_scale;
	private Vector3f	_translate;
	private Vector3f	_offset;
	
	public AnimationFrame(float t, Vector3f translate, Vector3f rotation, Vector3f scale, Vector3f offset)
	{
		this._time = t;
		this._rotation = rotation;
		this._scale = scale;
		this._translate = translate;
		this._offset = offset;
	}

	public Vector3f getTranslateValue()
	{
		return (this._translate);
	}

	public Vector3f getRotValue()
	{
		return (this._rotation);
	}

	public Vector3f getScaleValue()
	{
		return (this._scale);
	}
	
	public Vector3f getOffsetValue()
	{
		return (this._offset);
	}
	
	public float	getTime()
	{
		return (this._time);
	}
}