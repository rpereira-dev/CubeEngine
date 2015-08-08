package com.grillecube.client.mod.renderer.particles.quad;

import com.grillecube.client.mod.renderer.particles.cube.CubeParticle;
import com.grillecube.client.renderer.Camera;
import com.grillecube.client.renderer.opengl.object.Program;

public class ProgramQuadParticle extends Program
{
	private int _proj_matrix;
	private int _view_matrix;
	private int _transf_matrix;
	private int _column;
	private int _line;
	private int _color;

	public ProgramQuadParticle()
	{
		super("quad", "quad");
	}

	@Override
	public void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "uv");
	}

	@Override
	public void linkUniforms()
	{
		this._proj_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
		this._column = super.getUniform("column");
		this._line = super.getUniform("line");
		this._color = super.getUniform("color");
	}

	/** load global uniforms */
	public void	loadGlobalUniforms(Camera camera)
	{
		super.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		super.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());
	}
	
	/** load sprite instance uniforms */
	public void	loadSpriteUniform(ParticleSprite sprite)
	{
		super.loadUniformFloat(this._line, sprite.getLines());		
		super.loadUniformFloat(this._column, sprite.getCols());	
	}
	
	/** load particle instance uniforms */
	public void	loadInstanceUniforms(CubeParticle particle)
	{
		super.loadUniformMatrix(this._transf_matrix, particle.getTransfMatrix());
		super.loadUniformVec(this._color, particle.getColor());
		super.loadUniformFloat(this._color, particle.getSpriteID());
	}
	


}
