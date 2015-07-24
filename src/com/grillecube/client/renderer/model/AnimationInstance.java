package com.grillecube.client.renderer.model;

import org.lwjgl.util.vector.Vector3f;

public class AnimationInstance
{
	private Animation		_animation;
	private int				_prev_frame;
	private int				_next_frame;
	private float			_animation_timer;
	private long			_last_millis;
	private boolean			_loop;
	
	public AnimationInstance()
	{
		this._prev_frame = 0;
		this._next_frame = 1;
		this._animation = null;
		this._animation_timer = 0.0f;
		this._last_millis = 0;
	}
	
	public void	start(Animation animation, boolean loop)
	{
		this._animation = animation;
		this._last_millis = System.currentTimeMillis();
		this._animation_timer = 0;
		this._prev_frame = 0;
		this._next_frame = 1;
		this._loop = loop;
	}

	public void	update()
	{
		if (!this.isPlaying())
		{
			return ;
		}
		this._animation_timer += (System.currentTimeMillis() - this._last_millis) / 1000.0f;
		this._last_millis = System.currentTimeMillis();
		if (this._animation_timer >= this._animation.getDuration())
		{
			if (this._loop)
			{
				this._animation_timer = 0;
			}
			else
			{
				this.stop();
			}
		}
		else
		{
			if (this._animation_timer >= this.getNextFrame().getTime())
			{
				this._prev_frame++;
				this._next_frame++;
			}
		}
	}
	
	public AnimationFrame	getPrevFrame()
	{
		return (this._animation.getFrameAt(this._prev_frame));
	}
	
	
	public AnimationFrame	getNextFrame()
	{
		return (this._animation.getFrameAt(this._next_frame));
	}
	

	public void stop()
	{
		this._animation = null;
	}
	
	public boolean isPlaying()
	{
		return (this._animation != null);
	}
	
	public Vector3f	interpolateVector(Vector3f left, Vector3f right)
	{
		float	ratio;
		
		ratio = (this._animation_timer - this.getPrevFrame().getTime()) / (this.getNextFrame().getTime() - this.getPrevFrame().getTime());
		return (new Vector3f(
				left.x * (1 - ratio) + right.x * ratio,
				left.y * (1 - ratio) + right.y * ratio,
				left.z * (1 - ratio) + right.z * ratio
				));
	}
	
	public Vector3f getOffset()
	{
		return (this.interpolateVector(this.getPrevFrame().getOffsetValue(), this.getNextFrame().getOffsetValue()));
	}
	
	public Vector3f getTranslation()
	{
		return (this.interpolateVector(this.getPrevFrame().getTranslateValue(), this.getNextFrame().getTranslateValue()));
	}

	public Vector3f getRotation()
	{
		return (this.interpolateVector(this.getPrevFrame().getRotValue(), this.getNextFrame().getRotValue()));
	}

	public Vector3f getScaling()
	{
		return (this.interpolateVector(this.getPrevFrame().getScaleValue(), this.getNextFrame().getScaleValue()));
	}

	public Animation getAnimation()
	{
		return (this._animation);
	}	
}
