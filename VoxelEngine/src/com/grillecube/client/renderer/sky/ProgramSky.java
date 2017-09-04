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

package com.grillecube.client.renderer.sky;

import org.lwjgl.opengl.GL20;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLProgram;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.Sky;

public class ProgramSky extends GLProgram {
	private int _proj_matrix;
	private int _view_matrix;

	private int _sky_color;

	private int _sun_pos;
	private int _sun_color;
	private int _sun_intensity;

	private int _fog_color;

	private int _cloud_color;
	private int _time;

	public ProgramSky() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/sky.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/sky.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	public void linkUniforms() {
		this._proj_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");

		this._sky_color = super.getUniform("sky_color");

		this._sun_pos = super.getUniform("sun_pos");
		this._sun_color = super.getUniform("sun_color");
		this._sun_intensity = super.getUniform("sun_intensity");

		this._fog_color = super.getUniform("fog_color");

		this._cloud_color = super.getUniform("cloud_color");
		this._time = super.getUniform("time");
	}

	private Matrix4f matrix = new Matrix4f();

	public void loadUniforms(Sky sky, CameraProjective camera) {
		this.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());

		this.matrix = new Matrix4f(camera.getViewMatrix());
		this.matrix.m30 = 0;
		this.matrix.m31 = 0;
		this.matrix.m32 = 0;
		this.loadUniformMatrix(this._view_matrix, this.matrix);

		this.loadUniformVec(this._sky_color, sky.getSkyColor());

		this.loadUniformFloat(this._sun_intensity, sky.getSun().getIntensity());
		this.loadUniformVec(this._sun_pos, sky.getSun().getPosition());
		this.loadUniformVec(this._sun_color, sky.getSun().getColor());

		this.loadUniformVec(this._fog_color, sky.getFogColor());
		this.loadUniformVec(this._cloud_color, sky.getCloudColor());
		this.loadUniformFloat(this._time, sky.getDayCount() + sky.getCycleRatio());
	}

}
