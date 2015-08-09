package com.grillecube.client.renderer.particles;

import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.opengl.object.Program;

public class ProgramParticle extends Program
{
	protected int _proj_matrix;
	protected int _view_matrix;
	protected int _transf_matrix;
	protected int _color;
	protected int _health;

	public ProgramParticle(String vertex, String fragment)
	{
		super(vertex, fragment);
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
		this._health = super.getUniform("health");
	}

	/** load global uniforms */
	public void	loadGlobalUniforms(Camera camera)
	{
		super.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		super.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
	}
	
	/** load instance uniforms */
	public void	loadInstanceUniforms(Particle particle)
	{
		super.loadUniformMatrix(this._transf_matrix, particle.getTransfMatrix());
		super.loadUniformVec(this._color, particle.getColor());
		super.loadUniformFloat(this._health, particle.getHealthRatio());		
	}
}
