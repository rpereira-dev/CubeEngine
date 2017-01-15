/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.engine.renderer.gui;

import org.lwjgl.opengl.GL20;

import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLProgram;
import com.grillecube.engine.renderer.gui.font.FontModel;
import com.grillecube.engine.resources.R;

public class ProgramFont extends GLProgram {
	private int _transf_matrix;
	private int _outline_color;
	private int _outline_offset;
	private int _border_edge;
	private int _border_width;
	private int _edge;
	private int _width;

	public ProgramFont() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/gui/font.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/gui/font.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "uv");
		super.bindAttribute(2, "color");
	}

	@Override
	public void linkUniforms() {
		this._transf_matrix = super.getUniform("transf_matrix");
		this._width = super.getUniform("width");
		this._edge = super.getUniform("edge");
		this._border_width = super.getUniform("border_width");
		this._border_edge = super.getUniform("border_edge");
		this._outline_offset = super.getUniform("outline_offset");
		this._outline_color = super.getUniform("outline_color");
	}
	
	public void bindFontModel(FontModel model) {
		super.loadUniformMatrix(this._transf_matrix, model.getTransformationMatrix());
		
		super.loadUniformFloat(this._width, 0.5f);
		super.loadUniformFloat(this._edge, 0.1f);
		
		super.loadUniformFloat(this._border_width, model.getBorderWidth());
		super.loadUniformFloat(this._border_edge, model.getBorderEdge());
		
		super.loadUniformVec(this._outline_offset, model.getOutlineOffset());
		super.loadUniformVec(this._outline_color, model.getOutlineColor());		
	}

}
