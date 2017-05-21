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

package com.grillecube.client.renderer.world.terrain;

import org.lwjgl.opengl.GL20;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.opengl.object.GLProgram;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.Weather;

public class ProgramTerrainReflectionRefraction extends GLProgram {

	/** uniforms location */
	private int _mvp_matrix;
	private int _transf_matrix;

	private int _sun_color;
	private int _sun_position;
	private int _sun_intensity;

	private int _ambient_light;

	private int _tx_atlas;

	private int _clip_plane;

	private int _camera_position;

	public ProgramTerrainReflectionRefraction() {
		super();
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain_reflection_refraction.fs"), GL20.GL_FRAGMENT_SHADER));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain_reflection_refraction.vs"), GL20.GL_VERTEX_SHADER));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "normal");
		super.bindAttribute(2, "uv");
		super.bindAttribute(3, "brightness");
		super.bindAttribute(4, "reflection");
	}

	@Override
	public void linkUniforms() {
		this._camera_position = super.getUniform("camera_position");
		this._mvp_matrix = super.getUniform("mvp_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
		this._sun_color = super.getUniform("sun_color");
		this._sun_position = super.getUniform("sun_position");
		this._sun_intensity = super.getUniform("sun_intensity");
		this._ambient_light = super.getUniform("ambient_light");

		this._clip_plane = super.getUniform("clip_plane");

		this._tx_atlas = super.getUniform("tx_atlas");
	}

	/** load global terrain uniform */
	private Vector3f sunpos = new Vector3f();
	public void loadUniforms(CameraProjectiveWorld camera, Weather weather, Vector4f clipping_plane) {

		this.loadUniformVec(this._camera_position, camera.getPosition());
		this.loadUniformMatrix(this._mvp_matrix, camera.getMVPMatrix());

		this.loadUniformVec(this._sun_color, weather.getSun().getColor());
		this.loadUniformFloat(this._sun_intensity, weather.getSun().getIntensity());
		this.loadUniformVec(this._sun_position, sunpos.set(weather.getSun().getPosition()).scale(10e10f));
		this.loadUniformFloat(this._ambient_light, weather.getAmbientLight());
		super.loadUniformVec(this._clip_plane, clipping_plane);

		this.loadUniformInteger(this._tx_atlas, 0);
	}

	/** load terrain instance uniforms variable */
	/** load terrain instance uniforms variable */
	public void loadInstanceUniforms(TerrainMesh mesh) {
		this.loadUniformMatrix(this._transf_matrix, mesh.getTransformationMatrix());
	}
}
