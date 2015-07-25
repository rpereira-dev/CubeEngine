package com.grillecube.client.renderer.program;

public class ProgramQuad extends Program
{
	private int	_transf_matrix;
	
	public ProgramQuad()
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
		this._transf_matrix = super.getUniform("transf_matrix");
	}
	
	public int	getTransfMatrix()
	{
		return (this._transf_matrix);
	}

}
