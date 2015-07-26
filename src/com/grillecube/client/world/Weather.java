package com.grillecube.client.world;

import org.lwjgl.util.vector.Vector3f;

import com.grillecube.common.world.Terrain;

public class Weather
{
	/** sun position */
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
		this._sun_pos = new Vector3f(0, Terrain.SIZE_Y * 4, Terrain.SIZE_Z);
		this._sun_color = new Vector3f(1.0f, 1.0f, 1.0f);
		this._sun_intensity = 1;
		
		this._fog_color = new Vector3f(1.0f, 1.0f, 1.0f);
		this._fog_density = 0.008f;
		this._fog_gradient = 2.5f;
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
}
