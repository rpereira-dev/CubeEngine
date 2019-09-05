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

package com.grillecube.client.renderer.particles;

import org.lwjgl.opengl.GL20;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.GLProgram;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.resources.R;

public class ProgramParticleCube extends GLProgram {

	protected int mvpMatrix;

	public ProgramParticleCube() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/cube.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/cube.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "transf_matrix"); // matrix take 1, 2, 3, 4
		super.bindAttribute(5, "color");
		super.bindAttribute(6, "health");
	}

	@Override
	public void linkUniforms() {
		this.mvpMatrix = super.getUniform("mvp_matrix");
	}

	/** load global uniforms */
	public void loadGlobalUniforms(CameraProjective camera) {
		super.loadUniformMatrix(this.mvpMatrix, camera.getMVPMatrix());
	}
}