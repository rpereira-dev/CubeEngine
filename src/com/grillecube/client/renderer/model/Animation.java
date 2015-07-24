package com.grillecube.client.renderer.model;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 
 * @author Romain
 * 
 * Animation:
 * 		animation_id: 	animation
 *		frames:			animations frames
 */

public class Animation
{	
	private AnimationFrame[]	_frames;
	private String				_name;
	private float				_duration;
	private int 				_animationID;

	public Animation() {}
	
	public AnimationFrame[]	getFrames()
	{
		return (this._frames);
	}
	
	public float getDuration()
	{
		return (this._duration);
	}
	
	/** set frames for this animation */
	public void	setFrames(AnimationFrame[] frames)
	{
		if (frames.length == 0)
		{
			this._frames = null;
			this._duration = 0;
		}
		else
		{
			Arrays.sort(frames, new Comparator<AnimationFrame>()
					{	
						public int compare(AnimationFrame a1, AnimationFrame a2)
						{
							if (a1.getTime() == a2.getTime())
							{
								return (0);
							}
							if (a1.getTime() < a2.getTime())
							{
								return (-1);
							}
							return (1);
						}
					});
			this._frames = frames;
			this._duration = frames[frames.length - 1].getTime();	
		}
	}
	
	/** return animation ID */
	public int getID()
	{
		return (this._animationID);
	}
	
	/** return animation name */
	public String getName()
	{
		return (this._name);
	}

	/** return number of frames for this animation */
	public int getFramesCount()
	{
		return (this._frames.length);
	}

	public AnimationFrame	getFrameAt(int i)
	{
		while (i >= this._frames.length)
		{
			--i;
		}
		while (i < 0)
		{
			++i;
		}
		return (this._frames[i]);
	}
}
