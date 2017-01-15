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

import com.grillecube.engine.opengl.GLH;
import com.grillecube.engine.opengl.object.GLProgram;
import com.grillecube.engine.renderer.model.instance.ModelPartInstance;
import com.grillecube.engine.renderer.world.ShadowCamera;
import com.grillecube.engine.resources.R;

public class ProgramModelShadow extends GLProgram {

	private int _mvp_matrix;
	private int _transf_matrix;

	public ProgramModelShadow() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/model_shadow.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/model_shadow.vs"), GL20.GL_VERTEX_SHADER));
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

	public void loadUniforms(ShadowCamera shadow_camera) {
		this.loadUniformMatrix(this._mvp_matrix, shadow_camera.getMVPMatrix());
	}

	public void loadInstanceUniforms(ModelPartInstance part) {
		this.loadUniformMatrix(this._transf_matrix, part.getTransformationMatrix());
	}
}
