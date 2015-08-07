package com.grillecube.client.mod.renderer.particles.cube;

import org.lwjgl.util.vector.Matrix4f;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.opengl.Program;

public class ProgramCubeParticles extends Program
{
	private int _proj_matrix;
	private int _view_matrix;
	private int _transf_matrix;

	public ProgramCubeParticles()
	{
		super("cube", "cube");
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
		this._transf_matrix = super.getUniform("transf_matrix");	
	}

	public void	loadUniforms(Camera camera, CubeParticle particle)
	{
		this.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		this.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
		this.loadUniformMatrix(this._transf_matrix, particle.getTransfMatrix());
	}
}
