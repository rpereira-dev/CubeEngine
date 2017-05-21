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

package com.grillecube.client.renderer.world;

import org.lwjgl.opengl.GL20;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLProgram;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.World;

public class ProgramMVP extends GLProgram {
	public static final int ATTR_POS = 0;

	private int _mvp_matrix;
	private int _transf_matrix;
	private int _color;
	
	private int _fog_color;
	private int _fog_density;
	private int _fog_gradient;

	public ProgramMVP() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/mvp.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/mvp.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(ATTR_POS, "position");
	}

	@Override
	public void linkUniforms() {
		
		this._mvp_matrix = super.getUniform("mvp_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
		this._color = super.getUniform("color");
		
		this._fog_color = super.getUniform("fog_color");
		this._fog_gradient = super.getUniform("fog_gradient");
		this._fog_density = super.getUniform("fog_density");
	}

	public void loadUniforms(World world, CameraProjective camera) {
		
		this.loadUniformMatrix(this._mvp_matrix, camera.getMVPMatrix());
		
		this.loadUniformVec(this._fog_color, world.getWeather().getFogColor());
		this.loadUniformFloat(this._fog_gradient, world.getWeather().getFogGradient());
		this.loadUniformFloat(this._fog_density, world.getWeather().getFogDensity());
	}

	private static final Matrix4f matrixbuffer = new Matrix4f();
	public void loadUniforms(MVPObject object) {
		Matrix4f matrix = object.getTransformationMatrix();
		if (matrix == null) {
			matrix = Matrix4f.createTransformationMatrix(matrixbuffer, null, null, null);
		}
		this.loadUniformMatrix(this._transf_matrix, matrix);
		this.loadUniformVec(this._color, object.getColor());
	}
}
