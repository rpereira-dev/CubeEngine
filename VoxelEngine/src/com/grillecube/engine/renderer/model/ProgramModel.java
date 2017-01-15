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

package com.grillecube.engine.renderer.model;

import org.lwjgl.opengl.GL20;

import com.grillecube.engine.maths.Matrix4f;
import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLProgram;
import com.grillecube.engine.renderer.camera.CameraProjective;
import com.grillecube.engine.resources.R;

public class ProgramModel extends GLProgram {

	private int _mvp_matrix;
	private int _transf_matrix;

	public ProgramModel() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/model.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/model.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "normal");
		super.bindAttribute(2, "color");
	}

	@Override
	public void linkUniforms() {
		this._mvp_matrix = super.getUniform("mvp_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
	}

	public void loadUniforms(CameraProjective camera) {
		this.loadUniformMatrix(this._mvp_matrix, camera.getMVPMatrix());
	}

	public void loadInstanceUniforms(Matrix4f transf_matrix) {
		this.loadUniformMatrix(this._transf_matrix, transf_matrix);
	}
}
