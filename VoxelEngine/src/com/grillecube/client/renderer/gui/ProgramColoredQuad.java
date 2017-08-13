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
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.resources.R;

public class ProgramColoredQuad extends GLProgram {

	private int transfMatrix;
	private int color;

	public ProgramColoredQuad() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/gui/quadColored.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/gui/quadColored.gs"), GL32.GL_GEOMETRY_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/gui/quadColored.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
	}

	@Override
	public void linkUniforms() {
		this.transfMatrix = super.getUniform("transfMatrix");
		this.color = super.getUniform("color");
	}

	public void loadQuadColored(float r, float g, float b, float a, Matrix4f transformMatrix) {
		super.loadUniformMatrix(this.transfMatrix, transformMatrix);
		super.loadUniformVec(this.color, r, g, b, a);
	}
}
