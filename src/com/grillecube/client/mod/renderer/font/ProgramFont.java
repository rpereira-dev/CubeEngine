package com.grillecube.client.mod.renderer.font;

import com.grillecube.client.renderer.opengl.Program;

public class ProgramFont extends Program
{
	private int	_transf_matrix;

	public ProgramFont()
	{
		super("font", "font");
	}

	@Override
	public void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "uv");
		super.bindAttribute(2, "color");
	}

	@Override
	public void linkUniforms()
	{
		this._transf_matrix	= super.getUniform("transf_matrix");
	}
	
	public void	bindFontModel(FontModel model)
	{
		super.loadUniformMatrix(this._transf_matrix, model.getTransformationMatrix());
	}

}
