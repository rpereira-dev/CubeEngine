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
import com.grillecube.client.opengl.GLProgram;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.R;
import com.grillecube.common.world.WorldFlat;

public class ProgramTerrain extends GLProgram {

	public static final int MESH_TYPE_OPAQUE = 0;
	public static final int MESH_TYPE_TRANSPARENT = 1;

	/** uniforms location */
	private int _view_matrix;
	private int _proj_matrix;
	private int _transf_matrix;
	private int _mesh_type;

	private int _fog_color;
	private int _fog_density;
	private int _fog_gradient;

	private int _sun_color;
	private int _sun_position;
	private int _sun_intensity;

	private int _ambient_light;

	private int _tx_atlas;

	private int _camera_position;

	private int _move_factor;
	private float _move_factor_value;

	public ProgramTerrain() {
		super();
		String header = "# define MESH_TYPE_OPAQUE (" + MESH_TYPE_OPAQUE + ")\n" + "# define MESH_TYPE_TRANSPARENT ("
				+ MESH_TYPE_TRANSPARENT + ")\n";
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain.fs"), GL20.GL_FRAGMENT_SHADER, header));
		this.addShader(GLH.glhLoadShader(R.getResPath("shaders/terrain.vs"), GL20.GL_VERTEX_SHADER, header));
		this.link();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "normal");
		super.bindAttribute(2, "uv");
		super.bindAttribute(3, "color");
		super.bindAttribute(4, "brightness");
	}

	@Override
	public void linkUniforms() {
		this._camera_position = super.getUniform("camera_position");

		this._proj_matrix = super.getUniform("proj_matrix");
		this._view_matrix = super.getUniform("view_matrix");
		this._transf_matrix = super.getUniform("transf_matrix");
		this._mesh_type = super.getUniform("mesh_type");
		this._fog_color = super.getUniform("fog_color");
		this._fog_gradient = super.getUniform("fog_gradient");
		this._fog_density = super.getUniform("fog_density");
		this._sun_color = super.getUniform("sun_color");
		this._sun_position = super.getUniform("sun_position");
		this._sun_intensity = super.getUniform("sun_intensity");
		this._ambient_light = super.getUniform("ambient_light");

		this._move_factor = super.getUniform("move_factor");

		this._tx_atlas = super.getUniform("tx_atlas");
	}

	/** load global terrain uniform */
	private Vector3f sunpos = new Vector3f();

	public void loadUniforms(CameraProjective camera, WorldFlat world) {

		this.loadUniformVec(this._camera_position, camera.getPosition());
		this.loadUniformMatrix(this._proj_matrix, camera.getProjectionMatrix());
		this.loadUniformMatrix(this._view_matrix, camera.getViewMatrix());

		this.loadUniformVec(this._fog_color, world.getSky().getFogColor());
		this.loadUniformFloat(this._fog_gradient, world.getSky().getFogGradient());
		this.loadUniformFloat(this._fog_density, world.getSky().getFogDensity());

		this.loadUniformVec(this._sun_color, world.getSky().getSun().getColor());
		this.loadUniformFloat(this._sun_intensity, world.getSky().getSun().getIntensity());
		this.loadUniformVec(this._sun_position, sunpos.set(world.getSky().getSun().getPosition()));
		this.loadUniformFloat(this._ambient_light, world.getSky().getAmbientLight());

		this.loadUniformInteger(this._tx_atlas, 0);

		this.loadUniformFloat(this._move_factor, this._move_factor_value);
		this._move_factor_value += 0.0002f;
		if (this._move_factor_value >= 1.0f) {
			this._move_factor_value = 0.0f;
		}
	}

	/** load terrain instance uniforms variable */
	public void loadInstanceUniforms(TerrainMesh mesh) {
		this.loadUniformMatrix(this._transf_matrix, mesh.getTransformationMatrix());
	}

	public void loadTypeUniform(int meshType) {
		this.loadUniformInteger(this._mesh_type, meshType);
	}
}
