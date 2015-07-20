package com.grillecube.renderer;

import com.grillecube.renderer.program.Program;

public class ProgramTerrain extends Program
{
	/** uniforms location */
	private int	_proj_matrix;
	private int	_view_matrix;
	private int	_transf_matrix;
	private int	_fog_density;
	private int	_fog_gradient;
	
	public ProgramTerrain()
	{
		super("terrain", "terrain");
	}

	@Override
	public void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "uv");
		super.bindAttribute(2, "normal");
	}

	@Override
	public void linkUniforms()
	{
		this._proj_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
		this._fog_density = super.getUniform("fog_density");
		this._fog_gradient = super.getUniform("fog_gradient");
	}
	
	public int	getProjMatrix()
	{
		return (this._proj_matrix);
	}
	
	public int	getViewMatrix()
	{
		return (this._view_matrix);
	}
	
	
	public int	getTransfMatrix()
	{
		return (this._transf_matrix);
	}
	
	public int	getFogDensity()
	{
		return (this._fog_density);
	}
	
	public int	getFogGradient()
	{
		return (this._fog_gradient);
	}
}
