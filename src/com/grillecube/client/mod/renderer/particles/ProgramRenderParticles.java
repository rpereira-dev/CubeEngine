package com.grillecube.client.mod.renderer.particles;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.opengl.Program;

public class ProgramRenderParticles extends Program
{
	private int	_proj_matrix;
	private int	_view_matrix;
	
	public ProgramRenderParticles()
	{
		super("particle", "particle");
	}

	@Override
	public void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "color");
	}

	@Override
	public void linkUniforms()
	{
		this._proj_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");
	}
	
	public void	loadUniforms(Camera camera)
	{
		super.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		super.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
	}

}
