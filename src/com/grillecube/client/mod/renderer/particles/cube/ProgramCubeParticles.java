package com.grillecube.client.mod.renderer.particles.cube;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.opengl.Program;

public class ProgramCubeParticles extends Program
{
	private int _proj_matrix;
	private int _view_matrix;
	private int _transf_matrix;
	private int _color;

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
		this._color = super.getUniform("color");
	}

	/** load global uniforms */
	public void	loadGlobalUniforms(Camera camera)
	{
		super.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		super.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
	}
	
	/** load instance uniforms */
	public void	loadInstanceUniforms(CubeParticle particle)
	{
		super.loadUniformMatrix(this._transf_matrix, particle.getTransfMatrix());
		super.loadUniformVec(this._color, particle.getColor());
	}
	


}
