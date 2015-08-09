package com.grillecube.client.world;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.client.renderer.Camera;

public class Weather
{
	/** minutes for a full day / night cycle */
	private static final float MINUTE_PER_CYCLE = 1f;
	private static final long MILLIS_PER_CYCLE = (long) (1000 * 60 * MINUTE_PER_CYCLE);
	
	/** time constants */
	private static final float NIGHT_END	= 0.0f;
	private static final float DAY_START	= 0.1f;
	private static final float DAY_END		= 0.5f;
	private static final float NIGHT_START	= 0.6f;
	
	/** weather state */
	public static final int STATE_DAY			= 0;
	public static final int STATE_NIGHT			= 1;
	public static final int STATE_DAY_ENDING	= 2;
	public static final int STATE_NIGHT_ENDING	= 3;
	public static final int STATE_RAINING		= 4;
	
	/** state */
	private int	_state;
	
	/** time [0.0 ; 1.0] */
	private float	_cycle_ratio;
	private int		_cycle_count;
	
	/** time in millis [0.0 ; MILLIS_PER_CYCLE] */
	private long	_cycle_millis;
	private long	_prev_millis;
	
	/** sky data */
	private static final Vector3f	SKY_DEFAULT_COLOR	= new Vector3f(0.46f, 0.71f, 0.99f);
	private static final Vector3f	SKY_NIGHT_COLOR		= new Vector3f(0.06f, 0.11f, 0.16f);
	private Vector3f	_sky_color;
	
	/** sun position */
	public static final float		SUN_DIST = Camera.RENDER_DISTANCE;
	
	/** sun color */
	private static final Vector3f	SUN_RISE_COLOR 		= new Vector3f(1.2f, 1.0f, 0.8f);
	private static final Vector3f	SUN_DEFAULT_COLOR	= new Vector3f(1.0f, 1.0f, 1.0f);
	
	private Vector3f	_sun_pos;
	private Vector3f	_sun_color;
	private float		_sun_intensity;
	
	/** fog data */
	private Vector3f	_fog_color;
	private float		_fog_density;
	private float		_fog_gradient;
	
	/** Weather constructor */
	public Weather()
	{		
		this.setCycleRatio(0);
		this._state = 0;
		
		this._cycle_count = 0;
		
		this._sky_color = SKY_DEFAULT_COLOR;

		this._sun_pos = new Vector3f();
		this._sun_color = SUN_RISE_COLOR;
		this._sun_intensity = 1.1f;
		
		this._fog_color = new Vector3f(1.0f, 1.0f, 1.0f);
		this._fog_density = 0.004f;
		this._fog_gradient = 2.5f;
	}
	
	private void setCycleRatio(float f)
	{
		this._cycle_ratio = f;
		this._prev_millis = System.currentTimeMillis();
		this._cycle_millis = (long) (f * MILLIS_PER_CYCLE);
	}
	
	/** update the weather (mb use a 1D noise for rains, storm ...) */
	/** ACUALLY CALLED IN TERRAIN RENDERER! */
	public void	update()
	{
		this.updateTime();
		this.calculateSun();
		this.calculateFog();
	}
	
	private void updateTime()
	{
		long	time;
		
		time = System.currentTimeMillis();
		this._cycle_millis += (time - this._prev_millis);
		if (this._cycle_millis >= MILLIS_PER_CYCLE)
		{
			this._cycle_millis = this._cycle_millis % MILLIS_PER_CYCLE;
			this._cycle_count++;
		}
		this._cycle_ratio = this._cycle_millis / (float)MILLIS_PER_CYCLE;
		this._prev_millis = time;
		
		if (this._cycle_count >= Weather.NIGHT_END && this._cycle_count < Weather.DAY_START)
		{
			this.setState(Weather.STATE_NIGHT_ENDING);
		}
		
		if (this._cycle_count >= Weather.DAY_START && this._cycle_count < Weather.DAY_END)
		{
			this.setState(Weather.STATE_DAY);
		}
		
		if (this._cycle_count >= Weather.DAY_END && this._cycle_count < Weather.NIGHT_START)
		{
			this.setState(Weather.STATE_DAY_ENDING);
		}
		
		if (this._cycle_count > Weather.NIGHT_START)
		{
			this.setState(Weather.STATE_NIGHT);
		}
	}
	
	private void calculateFog()
	{

	}

	private void calculateSun()
	{
		this._sun_pos.x = (float) Math.cos(this._cycle_ratio * Math.PI * 2) * Weather.SUN_DIST;
		this._sun_pos.y = (float) Math.sin(this._cycle_ratio * Math.PI * 2) * Weather.SUN_DIST;
		this._sun_pos.z = 0;
	}
	
	/** return the current state */
	public int getState()
	{
		return (this._state);
	}
	
	private void setState(int state)
	{
		this._state = state;
	}

	public Vector3f getFogColor()
	{
		return (this._fog_color);
	}

	public void setFogColor(Vector3f color)
	{
		this._fog_color = color;
	}
	
	public float getFogGradient()
	{
		return (this._fog_gradient);
	}

	public void setFogGradient(float f)
	{
		this._fog_gradient = f;
	}
	
	public float getFogDensity()
	{
		return (this._fog_density);
	}

	public void setFogDensity(float f)
	{
		this._fog_density = f;
	}
	
	public Vector3f	getSunPos()
	{
		return (this._sun_pos);
	}
	
	public Vector3f	getSunColor()
	{
		return (this._sun_color);
	}
	
	public float	getSunIntensity()
	{
		return (this._sun_intensity);
	}

	public Vector3f getSkyColor()
	{
		return (this._sky_color);
	}
}
