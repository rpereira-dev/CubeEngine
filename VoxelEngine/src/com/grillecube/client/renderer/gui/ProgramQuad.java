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

package com.grillecube.client.renderer.gui;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLProgram;
import com.grillecube.common.resources.R;

public class ProgramQuad extends GLProgram {
	private int _quad_pos;
	private int _quad_size;
	private int _uvs;

	public ProgramQuad() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/gui/quad.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/gui/quad.gs"), GL32.GL_GEOMETRY_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/gui/quad.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
	}

	@Override
	public void linkUniforms() {
		this._quad_pos = super.getUniform("quad_pos");
		this._quad_size = super.getUniform("quad_size");
		this._uvs = super.getUniform("uvs");
	}

	public void loadQuadTextured(float x, float y, float width, float height, float uvxmin, float uvymin, float uvxmax,
			float uvymax) {
		super.loadUniformVec(this._quad_pos, x, y);
		super.loadUniformVec(this._quad_size, width, height);
		super.loadUniformVec(this._uvs, uvxmin, uvymin, uvxmax, uvymax);
	}
}
