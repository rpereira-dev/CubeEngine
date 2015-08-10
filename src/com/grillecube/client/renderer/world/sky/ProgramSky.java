package com.grillecube.client.renderer.world.sky;

import org.lwjgl.util.vector.Matrix4f;

import com.grillecube.client.opengl.object.Program;
import com.grillecube.client.renderer.camera.Camera;
import com.grillecube.client.world.Weather;

public class ProgramSky extends Program
{
	private int	_proj_matrix;
	private int	_view_matrix;

	private int	_sky_color;
	
	private int	_sun_pos;
	private int	_sun_color;
	private int _sun_intensity;
	
	private int	_fog_color;

	public ProgramSky()
	{
		super("sky", "sky");
	}

	@Override
	public void bindAttributes()
	{
		super.bindAttribute(0, "position");
	}

	@Override
	public void linkUniforms()
	{
		this._proj_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");
				
		this._sky_color = super.getUniform("sky_color");
		
		this._sun_pos = super.getUniform("sun_pos");
		this._sun_color = super.getUniform("sun_color");
		this._sun_intensity = super.getUniform("sun_intensity");

		this._fog_color = super.getUniform("fog_color");		
	}
	
	public void	loadUniforms(Weather weather, Camera camera)
	{
		Matrix4f matrix;
		
		this.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		
		matrix = new Matrix4f(camera.getViewMatrix());
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		this.loadUniformMatrix(this._view_matrix, matrix);
		
		this.loadUniformVec(this._sky_color, weather.getSkyColor());
		
		this.loadUniformFloat(this._sun_intensity, weather.getSunIntensity());
		this.loadUniformVec(this._sun_pos, weather.getSunPos());
		this.loadUniformVec(this._sun_color, weather.getSunColor());
		
		this.loadUniformVec(this._fog_color, weather.getFogColor());		
	}

}
